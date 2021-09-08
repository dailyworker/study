package io.dailyworker.framework.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptPinTest {
    @Test
    @DisplayName("같은 문자열이라도 사용자에 따라 다른 문자열이 나와야한다.")
    public void generate_crypt_pin() throws Exception {
        //given
        //when
        String A0_PIN = CryptPin.cryptPin("abcd1234", "id_A0");
        String U0_PIN = CryptPin.cryptPin("abcd1234", "id_U0");

        //then
        assertNotEquals(A0_PIN, U0_PIN);
    }


}