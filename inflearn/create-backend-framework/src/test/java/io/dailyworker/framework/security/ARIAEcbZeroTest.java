package io.dailyworker.framework.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ARIAEcbZeroTest {
    @Test
    @DisplayName("ARIA/ECB/ZERO 암복호화 테스트")
    public void  encrypt_and_decrypt() throws Exception {
        //given
        //when
        //then
        byte[] key = new byte[16];

        ARIAEcbZero ariaEcbZero = new ARIAEcbZero(key);

        byte[] planByte = new byte[16];

        System.out.println(Base64.getEncoder().encodeToString(ariaEcbZero.encrypt(planByte)));

        planByte[15] = 1;

        System.out.println(Base64.getEncoder().encodeToString(ariaEcbZero.encrypt(planByte)));

        planByte[15] = 2;

        System.out.println(Base64.getEncoder().encodeToString(ariaEcbZero.encrypt(planByte)));

        planByte[15] = 3;

        System.out.println(Base64.getEncoder().encodeToString(ariaEcbZero.encrypt(planByte)));

        planByte[15] = 4;

        System.out.println(Base64.getEncoder().encodeToString(ariaEcbZero.encrypt(planByte)));

        planByte[15] = 5;

        System.out.println(Base64.getEncoder().encodeToString(ariaEcbZero.encrypt(planByte)));
    }


}