package io.dailyworker.framework.domain;

public class Hex {

    public static String byteToHex(byte[] bytes) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (final byte x : b) {
                stringBuilder.append(String.format("%02x", x));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] hexToByte(String str) {
        try {
            int stringLength = str.length();
            byte[] data = new byte[stringLength / 2];
            for(int i = 0; i < stringLength; i += 2) {
              data[i/2] =  (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
