package io.dailyworker.framework.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCbcPkcs7 implements Crypt {

    private SecretKeySpec keySpec = null;
    private IvParameterSpec ivSpec = null;

    public AESCbcPkcs7(byte[] key, byte[] iv) {
        this.keySpec = new SecretKeySpec(key, "AES");
        this.ivSpec = new IvParameterSpec(iv);
    }

    @Override
    public byte[] encrypt(byte[] planText) throws Exception {
        Cipher encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encrypter.init(1, keySpec, ivSpec);

        return encrypter.doFinal(planText);
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes) throws Exception {
        Cipher decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decrypter.init(2, keySpec, ivSpec);

        return decrypter.doFinal(encryptedBytes);
    }
}
