package io.dailyworker.framework.security;

import java.security.InvalidKeyException;

public class ARIAEcbZero implements Crypt {

    private static final int BLOCK_LENGTH = 16;
    private ARIAEngine ariaEngine = null;

    public ARIAEcbZero(byte[] key) throws InvalidKeyException {
        ariaEngine = new ARIAEngine(key.length * 8);
        ariaEngine.setKey(key);
    }

    @Override
    public byte[] encrypt(byte[] planByte) throws Exception {
        if (planByte == null) {
            return null;
        }

        if (planByte.length == 0) {
            return planByte;
        }

        int encLength = planByte.length;

        if (planByte.length % BLOCK_LENGTH != 0) {
            encLength = (planByte.length / BLOCK_LENGTH) * BLOCK_LENGTH + BLOCK_LENGTH;
        }

        byte[] enc = new byte[encLength];

        int nPad = enc.length - planByte.length;

        int forCnt = enc.length / BLOCK_LENGTH;

        int srcPos = 0;

        for (int i = 0; i < forCnt; i++) {

            byte[] encBlock = new byte[BLOCK_LENGTH];

            int length = BLOCK_LENGTH;
            if (planByte.length < length + srcPos) {
                length = BLOCK_LENGTH - nPad;
            }

            System.arraycopy(planByte, srcPos, encBlock, 0, length);

            for (int ii = length; ii < BLOCK_LENGTH; ii++) {
                encBlock[ii] = (byte) 0;
            }

            encBlock = ariaEngine.encrypt(encBlock, 0);

            System.arraycopy(encBlock, 0, enc, srcPos, BLOCK_LENGTH);

            srcPos = srcPos + encBlock.length;

        }
        return enc;
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes) throws Exception {
        if (encryptedBytes == null) {
            return null;
        }

        if (encryptedBytes.length == 0) {
            return encryptedBytes;
        }

        byte[] planByte = new byte[encryptedBytes.length];

        int forCnt = encryptedBytes.length / BLOCK_LENGTH;

        int srcPos = 0;

        byte[] planByteBlock = null;

        for (int i = 0; i < forCnt; i++) {

            byte[] encBlock = new byte[BLOCK_LENGTH];
            System.arraycopy(encryptedBytes, srcPos, encBlock, 0, BLOCK_LENGTH);

            planByteBlock = ariaEngine.decrypt(encBlock, 0);

            System.arraycopy(planByteBlock, 0, planByte, srcPos, BLOCK_LENGTH);

            srcPos = srcPos + encBlock.length;

        }

        int cntPKCS7 = getPaddingCntZERO(planByteBlock);
        if (cntPKCS7 == 0) {
            return planByte;
        }

        byte[] newplanByte = new byte[planByte.length - cntPKCS7];
        System.arraycopy(planByte, 0, newplanByte, 0, newplanByte.length);
        return newplanByte;
    }

    private static int getPaddingCntZERO(byte[] endBlock) {

        int paddingCnt = 0;
        for (int i = endBlock.length - 1; i >= 0; i--) {
            if (endBlock[i] != 0) {
                break;
            }
            paddingCnt++;
        }

        return paddingCnt;
    }
}
