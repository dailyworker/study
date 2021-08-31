package io.dailyworker.framework.security;

import io.dailyworker.framework.domain.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    private IvParameterSpec ivSpec = new IvParameterSpec(iv);

    public String encrypt(String planText) throws Exception {
        byte[] planByte = planText.getBytes();

        Cipher encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encrypter.init(1, keySpec, ivSpec);
        byte[] enc = encrypter.doFinal(planByte);

        return Hex.byteToHex(enc);
    }

    public String decrypt(String encryptedText) throws Exception {
        byte[] encrypted = Hex.hexToByte(encryptedText);

        Cipher decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decrypter.init(2, keySpec, ivSpec);

        assert encrypted != null;
        byte[] planByte = decrypter.doFinal(encrypted);

        String planBytes = new String(planByte);
        return planBytes.trim();
    }
}
