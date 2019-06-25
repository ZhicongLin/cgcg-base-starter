package com.cgcg.base.util;

import java.security.MessageDigest;
import java.util.Map;

/**
 * MD5加密
* @author xujinbang
* @Date 2019-5-10
*/
public class MD5Util {

	 /**
     * MD5加密
     * @param b
     * @return
     */
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };


    public static String createMD5Sign(String key, Map<String,String> paramMap) {
        StringBuffer sb = new StringBuffer();
        for(String k : paramMap.keySet()) {
            String v = paramMap.get(k);
            sb.append(k + "=" + v + "&");
        }
        String params= sb.append("key=" + key).substring(0);
        String sign = MD5Util.MD5Encode(params, "utf8");
        return sign.toUpperCase();
    }
}
