package com.kevin.bluetooth.bluetooth;

/**
 * Kevin-Tu on 2017/10/26 0026.
 */

public class BluetoothUtils {

    /**
     * * Convert byte[] to hex string.
     * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。 字节数组转换秤16禁止字符串
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        length = length > src.length ? src.length : length;
        for (int i = 0; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
