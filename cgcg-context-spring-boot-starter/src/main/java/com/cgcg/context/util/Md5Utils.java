package com.cgcg.context.util;

import com.cgcg.context.enums.CharsetCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author zhicong.lin
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Md5Utils {

    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * MD5加密
     *
     * @param bytes
     * @return
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder resultSb = new StringBuilder();
        for (byte b : bytes) {
            resultSb.append(byteToHexString(b));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    public static String md5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return resultString;
    }

    public static String createMd5Sign(String key, Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder();
        for (String k : paramMap.keySet()) {
            String v = paramMap.get(k);
            sb.append(k).append("=").append(v).append("&");
        }
        String params = sb.append("key=").append(key).substring(0);
        String sign = Md5Utils.md5Encode(params, "utf8");
        return sign.toUpperCase();
    }

    /**
     * 对一个文件求他的md5值
     *
     * @param f 要求md5值的文件
     * @return md5串
     */
    public static String md5(File f) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }

            return new String(Hex.encodeHex(md.digest()));
        } catch (IOException e) {
            log.error("md5 file " + f.getAbsolutePath() + " failed:" + e.getMessage());
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * 求一个字符串的md5值
     *
     * @param target 字符串
     * @return md5 value
     */
    public static String md5(String target) {
        return DigestUtils.md5Hex(target);
    }

    /***
     * 根据字符串生成 32位的 MD5 码
     *
     * @author tmc.sun 2012-11-05
     * @param str
     *            待生成 MD5码的字符串
     * @return 根据字符串生成的 MD5码
     */
    public static String stringToMd5(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        MessageDigest md5;
        StringBuilder value = new StringBuilder();

        try {
            md5 = MessageDigest.getInstance("MD5");

            byte[] md5Bytes = md5.digest(str.getBytes(CharsetCode.forUtf8()));

            for (byte md5Byte : md5Bytes) {
                int val = (md5Byte) & 0xff;
                if (val < 16) {
                    value.append("0");
                }
                value.append(Integer.toHexString(val));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return "";
        }

        return value.toString();

    }

    // 哈希加密S

    /**
     * 将源字符串使用MD5加密为字节数组
     *
     * @param source
     * @return
     */
    public static byte[] encode2bytes(String source) {
        byte[] result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(source.getBytes(CharsetCode.forUtf8()));
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 将源字符串使用MD5加密为32位16进制数
     *
     * @param source
     * @return
     */
    public static String encode2hex(String source) {
        byte[] data = encode2bytes(source);

        StringBuilder hexString = new StringBuilder();
        for (byte datum : data) {
            String hex = Integer.toHexString(0xff & datum);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * 验证字符串是否匹配
     *
     * @param unknown 待验证的字符串
     * @param okHex   使用MD5加密过的16进制字符串
     * @return 匹配返回true，不匹配返回false
     */
    public static boolean validate(String unknown, String okHex) {
        return okHex.equals(encode2hex(unknown));
    }

}
