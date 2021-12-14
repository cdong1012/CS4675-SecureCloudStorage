package com.example.application.data.service;

import com.example.application.data.AWS.AWSController;
import com.example.application.data.crypto.FileEncryption;
import com.example.application.data.crypto.RSACrypt;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.SecurityQuestion;
import com.example.application.data.entity.User;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Service
public class Database {
    private final UserRepository userRepository;
    private final HashMap<User, Contact> userMap;
    private final String OS = System.getProperty("os.name").toLowerCase();
    public Database(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMap = new HashMap<>();
    }

    public void addUser(User user, String password, String address, String favColor, String favAnimal) {
        this.userMap.put(user, new Contact(user.getUsername() + "-cs4675-bucket", encryptPassword(password), new SecurityQuestion(address, favColor, favAnimal)));
        this.userMap.get(user).printEncryptedPassword();

        String bucketName = user.getUsername() + "-cs4675-bucket";

        AWSController.createBucket(bucketName); // create bucket for user
    }

    public Contact getContactFromUser(User user) {
        return this.userMap.get(user);
    }

    private byte[] encryptPassword(String password) {
        char fileSlash = '\\';
        if (isUnix() || isMac()) {
            fileSlash = '/';
        }

        try {
            Path path = Paths.get(System.getProperty("user.dir") + fileSlash + "publicKey");;
            byte[] encodedPublicKey = Files.readAllBytes(path);
            PublicKey publicKey = RSACrypt.importPublicKey(encodedPublicKey);
            return RSACrypt.encrypt(password, publicKey);
        } catch (IOException exception) {
            Notification.show("ERROR: Can't find public key file");
        } catch (Exception exception) {
            Notification.show("ERROR: Can't encrypt password");
        }
        return null;
    }

    public boolean authenticatePassword(User user, String password) {
        String decryptedPassword = decryptPassword(user);
        return decryptedPassword != null && decryptedPassword.equals(password);
    }

    private String decryptPassword(User user) {
        char fileSlash = '\\';
        if (isUnix() || isMac()) {
            fileSlash = '/';
        }
        AWSController.downloadObject("main-cs4675-bucket", "privateKey", System.getProperty("user.dir") + fileSlash + "privateKey");
        try {
            Path path = Paths.get(System.getProperty("user.dir") + fileSlash + "privateKey");
            byte[] encodedPrivateKey = Files.readAllBytes(path);
            PrivateKey privateKey = RSACrypt.importPrivateKey(encodedPrivateKey);
            byte[] decryptedPasswordBytes = RSACrypt.decrypt(this.userMap.get(user).getEncryptedPassword(), privateKey);
            String decryptedPassword = new String(decryptedPasswordBytes);
            File privKeyFile = new File(System.getProperty("user.dir") + fileSlash + "privateKey");
            privKeyFile.delete();
            System.out.println("Delete key successfully");
            return decryptedPassword;
        } catch (IOException exception) {
            Notification.show("ERROR: Can't find public key file");
        } catch (Exception exception) {
            Notification.show("ERROR: Can't decrypt password");
        }
        return null;
    }

    public void upload(User user, String filePath) {
        Contact contact = this.userMap.get(user);

        char fileSlash = '\\';
        if (isUnix() || isMac()) {
            fileSlash = '/';
        }
        String decryptedPassword = decryptPassword(user);

        if (decryptedPassword == null) {
            Notification.show("Can't decrypt password");
            return;
        }
        byte[] key = new byte[32];
        byte[] nonce = new byte[8];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedPassword = md.digest(decryptedPassword.getBytes());
            for (int i = 0; i < 32; i++) {
                key[i] = hashedPassword[i];
            }
            for (int i = 0; i < 8; i++) {
                nonce[i] = hashedPassword[i + 32];
            }
        } catch (NoSuchAlgorithmException exception) {
            Notification.show("SHA-512 not found");
            return;
        }
        String fileName = filePath.substring(filePath.lastIndexOf(fileSlash) + 1);;
        String encryptedFilePath = filePath + ".encrypted";

        File inFile = new File(filePath);
        File outFile = new File(encryptedFilePath);

        long fileLength = inFile.length();
        try {
            if (fileLength > 0 && fileLength < 10485760) {
                FileEncryption.encryptFileSmall(inFile, outFile, key, nonce);
            } else if (fileLength >= 10485760 && fileLength < 209715200) {
                FileEncryption.encryptFileMedium(inFile, outFile, key, nonce);
            } else {
                FileEncryption.encryptFileLarge(inFile, outFile, key, nonce);
            }
        } catch (IOException exception) {
            Notification.show("Can't encrypt file");
            if (outFile.exists()) {
                outFile.delete();
            }
            return;
        }

        AWSController.uploadObject(contact.getBucketName(), encryptedFilePath, fileName);

        if (outFile.exists()) {
            outFile.delete();
        }
        Notification.show("Upload file successfully");
    }

    public void downloadFile(User user, String fileName, String folderPath) {
        Contact contact = this.userMap.get(user);
        char fileSlash = '\\';

        if (isUnix() || isMac()) {
            fileSlash = '/';
        }

        String encryptedFilePath = folderPath + fileSlash + fileName + ".encrypted";
        AWSController.downloadObject(contact.getBucketName(), fileName, encryptedFilePath);
        String downloadFilePath = folderPath + fileSlash + fileName;

        File inFile = new File(encryptedFilePath);

        if (!inFile.exists()) {
            Notification.show("Download file fail");
            return;
        }
        File outFile = new File(downloadFilePath);

        int j = 1;
        while (outFile.exists()) {
            downloadFilePath = folderPath + fileSlash + fileName.substring(0, fileName.lastIndexOf('.')) + "(" + j + ")"
                    + fileName.substring(fileName.lastIndexOf('.'));
            outFile = new File(downloadFilePath);
            j++;
        }

        if (j != 1) {
            Notification.show(fileName + " already exists in folder.");
            Notification.show("Downloading file to " + downloadFilePath);
        }

        String decryptedPassword = decryptPassword(user);

        if (decryptedPassword == null) {
            Notification.show("Can't decrypt password");
            return;
        }
        byte[] key = new byte[32];
        byte[] nonce = new byte[8];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedPassword = md.digest(decryptedPassword.getBytes());
            for (int i = 0; i < 32; i++) {
                key[i] = hashedPassword[i];
            }
            for (int i = 0; i < 8; i++) {
                nonce[i] = hashedPassword[i + 32];
            }
        } catch (NoSuchAlgorithmException exception) {
            Notification.show("SHA-512 not found");
            return;
        }

        long fileLength = inFile.length();
        try {
            if (fileLength > 0 && fileLength < 10485760) {
                FileEncryption.encryptFileSmall(inFile, outFile, key, nonce);
            } else if (fileLength >= 10485760 && fileLength < 209715200) {
                FileEncryption.encryptFileMedium(inFile, outFile, key, nonce);
            } else {
                FileEncryption.encryptFileLarge(inFile, outFile, key, nonce);
            }
        } catch (IOException exception) {
            Notification.show("Can't decrypt file");
            if (inFile.exists()) {
                inFile.delete();
            }
            return;
        }

        if (inFile.exists()) {
            inFile.delete();
        }
        Notification.show("Download successfully");
    }


    public String getBucketName(User user) {
        return this.userMap.get(user).getBucketName();
    }

    public List<String> getUserFileNames(User user) {
        return AWSController.listObjectNames(getBucketName(user));
    }


    public void deleteFile(User user, String fileName) {
        Contact contact = this.userMap.get(user);
        AWSController.deleteFile(contact.getBucketName(), fileName);
        Notification.show("File " + fileName + " has been deleted");
    }

    public void deleteUser(User user) {
        Contact contact = this.userMap.get(user);
        AWSController.cleanBucket(contact.getBucketName());
        this.userMap.remove(user);
        userRepository.delete(user);
    }

    public void updatePassword(User user, String password) {
        Contact contact = this.userMap.get(user);

        byte[] encryptedPassword = this.encryptPassword(password);
        contact.setEncryptedPassword(encryptedPassword);
        Notification.show("Your account password has been updated.");
    }

    public boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public boolean isUnix() {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0);
    }

    public boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
}
