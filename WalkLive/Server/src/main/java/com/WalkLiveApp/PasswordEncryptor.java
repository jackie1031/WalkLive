package com.WalkLiveApp;

import java.security.SecureRandom;
import java.security.MessageDigest;

public class PasswordEncryptor {

    public PasswordEncryptor() {

    }

    public int getNewSalt() {
        SecureRandom sr = new SecureRandom();

        int salt = sr.nextInt(10);
        return salt;
    }

    protected static String sha256(String input) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    //encrypts given password with the given salt
    public String encryptPassword(String input, int salt) {
        //concat salt to the password
        String s = Integer.toString(salt);
        input = input + s;

        //hash the concat combo and return
        return sha256(input);
    }

    public boolean checkPassword(String input, int salt, String target) {
        String hashed = encryptPassword(input, salt);

        if (hashed.equals(target)) {
            return true;
        }

        return false;
    }
}
