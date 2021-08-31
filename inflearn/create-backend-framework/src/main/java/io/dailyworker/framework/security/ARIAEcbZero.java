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
    public byte[] encrypt(byte[] planText) throws Exception {
        if (planText == null) {
            return null;
        }

        if (planText.length == 0) {
            return planText;
        }

        int encLength = planText.length;

        if (planText.length % BLOCK_LENGTH != 0) {
            encLength = (planText.length / BLOCK_LENGTH) * BLOCK_LENGTH + BLOCK_LENGTH;
        }

        byte[] enc = new byte[encLength];

        int nPad = enc.length - planText.length;

        int forCnt = enc.length / BLOCK_LENGTH;

        int srcPos = 0;

        for (int i = 0; i < forCnt; i++) {

            byte[] encBlock = new byte[BLOCK_LENGTH];

            int length = BLOCK_LENGTH;
            if (planText.length < length + srcPos) {
                length = BLOCK_LENGTH - nPad;
            }

            System.arraycopy(planText, srcPos, encBlock, 0, length);

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

        byte[] plan = new byte[encryptedBytes.length];

        int forCnt = encryptedBytes.length / BLOCK_LENGTH;

        int srcPos = 0;

        byte[] planBlock = null;

        for (int i = 0; i < forCnt; i++) {

            byte[] encBlock = new byte[BLOCK_LENGTH];
            System.arraycopy(encryptedBytes, srcPos, encBlock, 0, BLOCK_LENGTH);

            planBlock = ariaEngine.decrypt(encBlock, 0);

            System.arraycopy(planBlock, 0, plan, srcPos, BLOCK_LENGTH);

            srcPos = srcPos + encBlock.length;

        }

        int cntPKCS7 = getPaddingCntZERO(planBlock);
        if (cntPKCS7 == 0) {
            return plan;
        }

        byte[] newPlan = new byte[plan.length - cntPKCS7];
        System.arraycopy(plan, 0, newPlan, 0, newPlan.length);
        return newPlan;
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
