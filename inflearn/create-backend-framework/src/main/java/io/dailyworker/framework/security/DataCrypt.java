package io.dailyworker.framework.security;

import io.dailyworker.framework.aop.CustomHttpRequestLocal;
import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.db.SqlRunner;
import io.dailyworker.framework.domain.Hex;

import java.util.concurrent.ConcurrentHashMap;

public class DataCrypt {

    private static ConcurrentHashMap<String, DataCrypt> dataCryptMap = new ConcurrentHashMap<>();

    private Crypt crypt = null;

    private DataCrypt(Crypt crypt) {
        this.crypt = crypt;
    }

    public static DataCrypt getDataCrypt(String OTP) throws Exception {
        DataCrypt dataCrypt = dataCryptMap.get(OTP);
        if (dataCrypt != null) {
            return dataCrypt;
        }

        CustomHttpRequestLocal customHttpRequestLocal = new CustomHttpRequestLocal();
        customHttpRequestLocal.put("otp", OTP);

        CustomRequest cryptInfo = SqlRunner.getSqlRunner()
                .getTable("DATACRYPT_01", customHttpRequestLocal)
                .getCustomRequest();

        String mode = cryptInfo.getString("MODE");
        String key = cryptInfo.getString("KEY");
        String iv = cryptInfo.getString("IV");

        byte[] keys = decryptKey(key);
        byte[] ivs = decryptKey(iv);

        Crypt crypt = null;

        if("ASE/CBC/PKCS7".equals(mode)) {
            crypt = new AESCbcPkcs7(keys, ivs);
        } else if("ARIA/CBC/PKCS7".equals(mode)) {
            crypt = new ARIACbcPkcs7(keys, ivs);
        } else if("ARIA/ECB/ZERO".equals(mode)) {
            crypt = new ARIAEcbZero(keys);
        }

        if(crypt == null) {
            throw new Exception();
        }

        dataCrypt = new DataCrypt(crypt);
        dataCryptMap.put(OTP, dataCrypt);
        return dataCrypt;
    }

    public String decrypt(String encryptedBytes) throws Exception {
        try {

            if(encryptedBytes == null) {
                return null;
            }

            encryptedBytes = encryptedBytes.trim();

            if("".equals(encryptedBytes)) {
                return "";
            }

            byte[] encryptedText = Hex.hexToByte(encryptedBytes);
            byte[] planByteText = crypt.decrypt(encryptedText);

            String decrypted = new String(planByteText);
            return decrypted.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String encrypt(String planByteText) throws Exception {
        try {
            if(planByteText == null) {
                return null;
            }

            planByteText = planByteText.trim();

            if("".equals(planByteText)) {
                return "";
            }
            byte[] planByteBytes = planByteText.getBytes();
            byte[] encrypt = crypt.encrypt(planByteBytes);

            return Hex.byteToHex(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] decryptKey(String key) throws Exception {
        byte[] bytes = new byte[16];
        bytes[15] = 5;

        ARIAEcbZero ariaEcbZero = new ARIAEcbZero(bytes);
        byte[] encrypted = Hex.hexToByte(key);
        return ariaEcbZero.decrypt(encrypted);
    }
}
