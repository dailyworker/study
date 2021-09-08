package io.dailyworker.framework.security;


import java.security.InvalidKeyException;

public class ARIACbcPkcs7 implements Crypt {

    private static final int BLOCK_LENGTH = 16;

    private byte[] iv = null;
    private ARIAEngine ariaEngine = null;

    public ARIACbcPkcs7(byte[] key, byte[] iv) throws InvalidKeyException {
        ariaEngine = new ARIAEngine(key.length * 8);
        ariaEngine.setKey(key);

        this.iv = iv;
    }

    @Override
    public byte[] encrypt(byte[] plainByte) throws Exception {
        if(plainByte == null) {
            return null;
        }

        if(plainByte.length == 0) {
             return plainByte;
        }

        byte[] cbcBlock = new byte[BLOCK_LENGTH];
        int encryptLength = (plainByte.length / BLOCK_LENGTH) * BLOCK_LENGTH + BLOCK_LENGTH;

        byte[] encrypted = new byte[encryptLength];
        int padding = encrypted.length - plainByte.length;

        System.arraycopy(this.iv, 0, cbcBlock, 0, BLOCK_LENGTH);

        int iteratorCount = encrypted.length / BLOCK_LENGTH;
        int srcPos = 0;

        for(int i = 0; i < iteratorCount; i++) {
            byte[] encryptedBlock = new byte[BLOCK_LENGTH];

            int length = BLOCK_LENGTH;
            if (plainByte.length < length + srcPos) {
                length = BLOCK_LENGTH - padding;
            }

            System.arraycopy(plainByte, srcPos, encryptedBlock, 0, length);

            for (int j = length; j < BLOCK_LENGTH; j++) {
                encrypted[j] = (byte) padding;
            }

            xor16ToX(encryptedBlock, cbcBlock);

            encryptedBlock = ariaEngine.encrypt(encryptedBlock, 0);

            System.arraycopy(encryptedBlock, 0, encrypted, srcPos, BLOCK_LENGTH);

            cbcBlock = encryptedBlock;
            srcPos = srcPos + encryptedBlock.length;
        }
        return encrypted;
    }

    private static int getPaddingCntPKCS7(byte[] endBlock) {

        byte paddingCnt = endBlock[endBlock.length - 1];

        if (paddingCnt > 16) {
            return 0;
        }

        if (paddingCnt < 0) {
            return 0;
        }

        for (int i = (16 - paddingCnt); i < endBlock.length; i++) {
            if (endBlock[i] != paddingCnt) {
                return 0;
            }
        }

        return paddingCnt;
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes) throws Exception {
        if(encryptedBytes == null) {
            return null;
        }

        if(encryptedBytes.length == 0) {
            return encryptedBytes;
        }

        byte[] cbcBlock = new byte[BLOCK_LENGTH];
        byte[] plainByte = new byte[encryptedBytes.length];

        System.arraycopy(this.iv, 0, cbcBlock, 0, BLOCK_LENGTH);

        int iteratorCount = encryptedBytes.length / BLOCK_LENGTH;
        int srcPos = 0;

        byte[] plainByteBlock = null;

        for(int i = 0; i <  iteratorCount; i++) {
            byte[] encryptBlock = new byte[BLOCK_LENGTH];
            System.arraycopy(encryptedBytes, srcPos, encryptBlock, 0, BLOCK_LENGTH);

            plainByteBlock = ariaEngine.decrypt(encryptBlock, 0);
            xor16ToX(plainByteBlock, cbcBlock);

            System.arraycopy(plainByteBlock, 0, plainByte, srcPos, BLOCK_LENGTH);

            cbcBlock = encryptBlock;
            srcPos = srcPos + encryptBlock.length;
        }

        assert plainByteBlock != null;
        int paddingCntPKCS7 = getPaddingCntPKCS7(plainByteBlock);
        if (paddingCntPKCS7 == 0) {
            return plainByte;
        }

        byte[] newPlainByte = new byte[plainByte.length - paddingCntPKCS7];
        System.arraycopy(plainByte, 0, newPlainByte, 0, newPlainByte.length);
        return newPlainByte;
    }

    private void xor16ToX(byte[] x, byte[] y) {
        x[0] = (byte) (x[0] ^ y[0]);
        x[1] = (byte) (x[1] ^ y[1]);
        x[2] = (byte) (x[2] ^ y[2]);
        x[3] = (byte) (x[3] ^ y[3]);
        x[4] = (byte) (x[4] ^ y[4]);
        x[5] = (byte) (x[5] ^ y[5]);
        x[6] = (byte) (x[6] ^ y[6]);
        x[7] = (byte) (x[7] ^ y[7]);
        x[8] = (byte) (x[8] ^ y[8]);
        x[9] = (byte) (x[9] ^ y[9]);
        x[10] = (byte) (x[10] ^ y[10]);
        x[11] = (byte) (x[11] ^ y[11]);
        x[12] = (byte) (x[12] ^ y[12]);
        x[13] = (byte) (x[13] ^ y[13]);
        x[14] = (byte) (x[14] ^ y[14]);
        x[15] = (byte) (x[15] ^ y[15]);
    }
}
