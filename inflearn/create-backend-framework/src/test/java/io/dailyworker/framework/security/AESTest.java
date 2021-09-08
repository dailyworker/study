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
    
    @Test
    @DisplayName("AES/CBC/PKCS7 암호화 처리가 제대로 동작한다")
    public void encryption() throws Exception {
        //given
        AES aes = new AES();
        //when
        // 17 / 16 / 15 / 1 바이트 순
        String[] target = { "11112222333344445", "1111222233334444", "111122223333444", "1"};
        //then
        for(int i = 0; i < target.length; i++) {
            String encrypted = aes.encrypt(target[i]);
            String decrypted = aes.decrypt(encrypted);
            assertEquals(target[i], decrypted);
        }
    }
    

}