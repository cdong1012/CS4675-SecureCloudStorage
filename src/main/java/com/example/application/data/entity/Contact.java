package com.example.application.data.entity;

public class Contact {

    private String bucketName;
    private byte[] encryptedPassword;
    private SecurityQuestion securityQuestion;

    public Contact(String bucketName, byte[] encryptedPassword, SecurityQuestion securityQuestion) {
        this.bucketName = bucketName;
        this.encryptedPassword = encryptedPassword;
        this.securityQuestion = securityQuestion;
    }

    public void printEncryptedPassword() {
        System.out.print("\nEncrypted password: [");
        for (byte a : this.encryptedPassword) {
            System.out.print(a + ", ");
        }
        System.out.println("]\nSize: " + this.encryptedPassword.length);
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(byte[] encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
    /*
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    */
    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
}
