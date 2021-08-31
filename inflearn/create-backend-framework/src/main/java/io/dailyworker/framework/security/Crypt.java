package io.dailyworker.framework.security;

public interface Crypt {
    public byte[] encrypt(byte[] planText) throws Exception;
    public byte[] decrypt(byte[] encryptedBytes) throws Exception;
}
