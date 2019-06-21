package com.zzx.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMACSHA256 {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 生成签名数据_HmacSHA256加密
     *
     * @param data
     *            待加密的数据
     * @param key
     *            加密使用的key
     */
    public static String getSignature(String data, String key) throws Exception {

        byte[] keyBytes = key.getBytes();
        // 根据给定的字节数组构造一个密钥。
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(data.getBytes());

        String hexBytes = byte2hex(rawHmac);
        return hexBytes;
    }

    private static String byte2hex(final byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式。
            stmp = (Integer.toHexString(b[n] & 0xFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}