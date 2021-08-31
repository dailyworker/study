package io.dailyworker.framework.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptPinTest {
    @Test
    @DisplayName("PIN 생성 테스트")
    public void generate_crypt_pin() throws Exception {
        //given
        //when
        //then
        String A0_PIN = CryptPin.cryptPin("abcd1234", "id_A0");

        String U0_PIN = CryptPin.cryptPin("abcd1234", "id_U0");

        System.out.println(A0_PIN);
        System.out.println(U0_PIN);
    }


}