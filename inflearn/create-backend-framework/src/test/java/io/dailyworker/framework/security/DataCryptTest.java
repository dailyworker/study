package io.dailyworker.framework.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataCryptTest {
    @Test
    @DisplayName("한 단어 테스트")
    public void sing_word() throws Exception {
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

    @Test
    @DisplayName("쓰레드 세이프하게 암복호화가 이뤄진다.")
    public void encrypt_and_decrypt_thread_safe() throws Exception {
        //given
        ConCurrentDataCrypt[] conCurrentDataCrypts = new ConCurrentDataCrypt[20];

        //when
        for (int i = 0; i < conCurrentDataCrypts.length; i++) {
            conCurrentDataCrypts[i] = new ConCurrentDataCrypt();
            conCurrentDataCrypts[i].start();
        }

        //then
        for (ConCurrentDataCrypt conCurrentDataCrypt : conCurrentDataCrypts) {
            conCurrentDataCrypt.join();
            assertTrue(conCurrentDataCrypt.isOk);
        }
    }


    static class ConCurrentDataCrypt extends Thread {
        boolean isOk = false;

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100000; i++) {
                    DataCrypt dataCrypt = DataCrypt.getDataCrypt("S02");

                    String plain = i + "abcd 1234 가나다라 !@#$" + i;
                    String enc = dataCrypt.encrypt(plain);
                    String dec = dataCrypt.decrypt(enc);

                    if (i % 1000 == 0) {
                        System.out.println(i + " " + this.hashCode());
                        System.out.println(plain);
                        System.out.println(enc);
                        System.out.println(dec);
                    }

                    if (!plain.equals(dec)) {
                        isOk = false;
                        break;
                    }
                }
                isOk = true;
            } catch (Exception ex) {
                isOk = false;
                ex.printStackTrace();
            }
        }
    }
}
