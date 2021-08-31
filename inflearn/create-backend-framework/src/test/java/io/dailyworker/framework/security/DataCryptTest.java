package io.dailyworker.framework.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataCryptTest {
    @Test
    @DisplayName("한 단어 테스트")
    public void  sing_word() throws Exception {
        //given
        DataCrypt dataCrypt = DataCrypt.getDataCrypt("S01");

        //when
        String plan = "abcd 1234 가나다라 !@#$";
        String enc = dataCrypt.encrypt(plan);
        String plan1 = dataCrypt.decrypt(enc);

        dataCrypt = DataCrypt.getDataCrypt("S01");
        String enc2 = dataCrypt.encrypt(plan);
        String plan2 = dataCrypt.decrypt(enc);

        //then
        assertAll(
                () -> assertEquals(plan, plan1),
                () -> assertEquals(plan, plan2)
        );
    }

}