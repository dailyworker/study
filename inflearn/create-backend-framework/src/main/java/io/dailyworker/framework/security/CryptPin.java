package io.dailyworker.framework.security;

import io.dailyworker.framework.domain.Hex;

import java.security.MessageDigest;

public class CryptPin {
    public static String cryptPin(String pin, String salt) {
        try {

            pin = pin + "-" + salt;

            byte[] pinBytes = pin.getBytes();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();
            md.update(pinBytes);
            byte[] encrypted = md.digest();

            return Hex.byteToHex(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
