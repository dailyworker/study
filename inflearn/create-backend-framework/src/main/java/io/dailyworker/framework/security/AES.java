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
        byte[] plan = planText.getBytes();

        Cipher encrypter = Cipher.getInstance("ASE/CBC/PKCS5Padding");
        encrypter.init(1, keySpec, ivSpec);
        byte[] enc = encrypter.doFinal(plan);

        return Hex.byteToHex(enc);
    }

    public String decrypt(String encryptedText) throws Exception {
        byte[] enc = Hex.hexToByte(encryptedText);

        Cipher decrypter = Cipher.getInstance("AES/CPC/PKCS5Padding");
        decrypter.init(2, keySpec, ivSpec);
        byte[] plan = decrypter.doFinal(enc);

        String plans = new String(plan);

        return plans.trim();
    }

}
