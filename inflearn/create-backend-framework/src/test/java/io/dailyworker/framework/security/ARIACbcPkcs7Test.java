package io.dailyworker.framework.security;

import io.dailyworker.framework.domain.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ARIACbcPkcs7Test {
    @Test
    @DisplayName("ARIA/CBC/PKCS7 암호화 처리가 제대로 동작한다")
    public void encryption() throws Exception {
        //given
        byte[] key = new byte[16];
        byte[] iv = new byte[16];

        ARIACbcPkcs7 ariaCbcPkcs7 = new ARIACbcPkcs7(key, iv);

        //when
        // 17 / 16 / 15 / 1 바이트 순
        String[] target = { "11112222333344445", "1111222233334444", "111122223333444", "1"};
        //then
        for (String s : target) {
            byte[] planByte = s.getBytes();

            byte[] encrypted = ariaCbcPkcs7.encrypt(planByte);
            byte[] decrypt = ariaCbcPkcs7.decrypt(encrypted);

            assertEquals(Hex.byteToHex(encrypted), Hex.byteToHex(decrypt));
        }
    }
    

}