package com.example.application.data.crypto;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;

public class FileEncryption {
    /**
     * Use this to encryppt anything less than 10mb.
     * Encrypt file completely
     * Guaranteed time from 0ms to 94.3ms
     * @param inFile File to encrypt
     * @param outFile File to output
     * @param key ChaCha20 key. MUST BE 32 BYTES
     * @param nonce ChaCha20 nonce. MUST BE 8 BYTES
     * @throws IOException In case inFile does not exists
     */
    public static void encryptFileSmall(File inFile, File outFile, byte[] key, byte[] nonce) throws IOException {
        if (!inFile.exists()) {
            System.out.println("Can't encrypt... inFile " + inFile.getAbsolutePath() + " does not exists.");
            return;
        }

        RandomAccessFile fileReader = new RandomAccessFile(inFile, "r");
        RandomAccessFile fileWriter = new RandomAccessFile(outFile, "rw");
        byte[] rawFileBuffer = new byte[ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024];
        byte[] encryptedFileBuffer = new byte[ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024];

        ChaCha20Crypt chaCha20Crypt = new ChaCha20Crypt(key, nonce);
        fileReader.seek(0);
        fileWriter.seek(0);

        while (true) {
            int finalLength = 0;
            if (fileReader.getFilePointer() + ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024 > fileReader.length()) {
                finalLength = (int)(fileReader.length() - fileReader.getFilePointer());
            }
            int readLength = fileReader.read(rawFileBuffer, 0, ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024);
            if (readLength == -1) {
                readLength = finalLength;
            }

            chaCha20Crypt.ChaChaEncrypt(rawFileBuffer, encryptedFileBuffer, readLength);

            fileWriter.write(encryptedFileBuffer, 0, readLength);

            if (readLength < ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024) {
                break;
            }
        }
        fileReader.close();
        fileWriter.close();
    }

    /**
     * Use this to encrypt anything from 10mb to 200mb
     * Encrypt the first half of the file
     * Guaranteed time from 60ms to 600ms
     * @param inFile File to encrypt
     * @param outFile File to output
     * @param key ChaCha20 key. MUST BE 32 BYTES
     * @param nonce ChaCha20 nonce. MUST BE 8 BYTES
     * @throws IOException In case inFile does not exists
     */
    public static void encryptFileMedium(File inFile, File outFile, byte[] key, byte[] nonce) throws IOException {
        if (!inFile.exists()) {
            System.out.println("Can't encrypt... inFile " + inFile.getAbsolutePath() + " does not exists.");
            return;
        }

        RandomAccessFile fileReader = new RandomAccessFile(inFile, "r");
        RandomAccessFile fileWriter = new RandomAccessFile(outFile, "rw");
        byte[] rawFileBuffer = new byte[ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024];
        byte[] encryptedFileBuffer = new byte[ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024];

        ChaCha20Crypt chaCha20Crypt = new ChaCha20Crypt(key, nonce);
        fileReader.seek(0);
        fileWriter.seek(0);

        long maxLengthToEncrypt = fileReader.length()/2;

        while (true) {
            int lengthEncrypt = ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024;
            if (fileReader.getFilePointer() + ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024 > maxLengthToEncrypt) {
                lengthEncrypt = (int)(maxLengthToEncrypt - fileReader.getFilePointer());
            }
            int readLength = fileReader.read(rawFileBuffer, 0, lengthEncrypt);
            if (readLength == -1) {
                readLength = lengthEncrypt;
            }

            chaCha20Crypt.ChaChaEncrypt(rawFileBuffer, encryptedFileBuffer, readLength);

            fileWriter.write(encryptedFileBuffer, 0, readLength);

            if (readLength < ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024) {
                break;
            }
        }

        byte[] secondHalfFileBuffer = new byte[(int)(fileReader.length() - fileReader.getFilePointer())];
        fileReader.read(secondHalfFileBuffer, 0, secondHalfFileBuffer.length);
        fileWriter.write(secondHalfFileBuffer, 0, secondHalfFileBuffer.length);
        fileReader.close();
        fileWriter.close();
    }

    /**
     * Use this to encrypt anything from 200mb and above
     * Block encryption. Encrypt 3 blocks within the file.
     * Guaranteed time from 1.9 seconds
     * @param inFile
     * @param outFile
     * @param key
     * @param nonce
     * @throws IOException
     */
    public static void encryptFileLarge(File inFile, File outFile, byte[] key, byte[] nonce) throws IOException {
        if (!inFile.exists()) {
            System.out.println("Can't encrypt... inFile " + inFile.getAbsolutePath() + " does not exists.");
            return;
        }

        Files.copy(inFile.toPath(), outFile.toPath());

        RandomAccessFile fileReader = new RandomAccessFile(inFile, "r");
        RandomAccessFile fileWriter = new RandomAccessFile(outFile, "rw");
        byte[] rawFileBuffer = new byte[ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024];
        byte[] encryptedFileBuffer = new byte[ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024];

        ChaCha20Crypt chaCha20Crypt = new ChaCha20Crypt(key, nonce);

        fileReader.seek(0);
        fileWriter.seek(0);

        long fileSize = fileReader.length();
        long encryptBlockSize = (long)(((1500.0/9430.0) * fileSize) / 3.0); // Encrypt a fixed block size

        if (encryptBlockSize > 113864456) { // max out at the block size of 2GB.
            encryptBlockSize = 113864456;
        }

        long skipLength = (fileSize - 3 * encryptBlockSize) / 2;
        for (int i = 0; i < 3; i++) {
            int totalReadBlock = 0;
            if (i == 2) {
                Instant start = Instant.now();
                while (true) {
                    int finalLength = 0;
                    if (fileReader.getFilePointer() + ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024 > fileReader.length()) {
                        finalLength = (int)(fileReader.length() - fileReader.getFilePointer());
                    }
                    int readLength = fileReader.read(rawFileBuffer, 0, ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024);
                    if (readLength == -1) {
                        readLength = finalLength;
                    }

                    chaCha20Crypt.ChaChaEncrypt(rawFileBuffer, encryptedFileBuffer, readLength);

                    fileWriter.write(encryptedFileBuffer, 0, readLength);
                    if (readLength < ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024) {
                        break;
                    }
                }
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
            } else {
                Instant start = Instant.now();
                while (true) {
                    int lengthEncrypt = ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024;
                    if (totalReadBlock + ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024 > encryptBlockSize) {
                        lengthEncrypt = (int)(encryptBlockSize - totalReadBlock);
                    }
                    int readLength = fileReader.read(rawFileBuffer, 0, lengthEncrypt);
                    if (readLength == -1) {
                        readLength = lengthEncrypt;
                    }

                    chaCha20Crypt.ChaChaEncrypt(rawFileBuffer, encryptedFileBuffer, readLength);

                    fileWriter.write(encryptedFileBuffer, 0, readLength);
                    totalReadBlock += readLength;
                    if (readLength < ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024) {
                        break;
                    }
                }
                long maxRead = (totalReadBlock + ChaCha20Crypt.CHACHA_BLOCKLENGTH * 1024 - encryptBlockSize) + skipLength;
                fileReader.seek(fileReader.getFilePointer() +  maxRead);
                fileWriter.seek(fileWriter.getFilePointer() +maxRead);
            }
        }
        fileReader.close();
        fileWriter.close();
    }
}
