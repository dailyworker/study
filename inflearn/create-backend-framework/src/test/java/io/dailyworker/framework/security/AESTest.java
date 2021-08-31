package io.dailyworker.framework.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AESTest {
    @Test
    @DisplayName("AES 암복호화 테스트")
    public void  encrypt_and_decrypt() throws Exception {
        //given
        AES aes = new AES();
        String target = "abcd1111";
        String encrypt = aes.encrypt(target);

        //when
        String decrypt = aes.decrypt(encrypt);

        //then
        assertEquals(target, decrypt);
    }

}