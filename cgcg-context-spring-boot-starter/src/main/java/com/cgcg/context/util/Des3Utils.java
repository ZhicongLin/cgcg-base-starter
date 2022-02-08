package com.cgcg.context.util;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.SpringContextHolder;
import com.cgcg.context.enums.CharsetCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

/**
 * @author zhicong.lin
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Des3Utils {

    /**
     * 定义加密算法，DESede即3DES
     */
    private static final String ALGORITHM = "DESede";

    private static final String THREES_KEY = "123456";

    /**
     * md5操作字符串
     *
     * @param source 进行md5的字符串
     * @return md5后的字符串
     * @author zhicong.lin 2022/1/26
     */
    public static String md5Encoding(String source) throws NoSuchAlgorithmException {
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        byte[] input = source.getBytes();
        mdInst.update(input);
        byte[] output = mdInst.digest();

        int i;

        StringBuilder buf = new StringBuilder();

        for (byte b : output) {
            i = b;

            if (i < 0) {
                i += 256;
            }

            if (i < 16) {
                buf.append('0');
            }

            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    /**
     * 加密方法
     *
     * @param src 源数据的字节数组
     */
    public static String encryptMode(String src) {
        return encryptMode(src, THREES_KEY);
    }

    /**
     * 加密方法
     *
     * @param src 源数据的字节数组
     */
    public static String encryptMode(String src, String threesKey) {
        try {
            byte[] targetSrc = src.getBytes(CharsetCode.forUtf8());
            // 生成密钥
            SecretKey desKey = new SecretKeySpec(build3DesKey(threesKey), ALGORITHM);
            // 实例化Cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            return Base64Util.encode(cipher.doFinal(targetSrc));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * 解密函数
     *
     * @param src 密文的字节数组
     */
    public static String decryptMode(String src) {
        return decryptMode(src, THREES_KEY);
    }

    /**
     * 解密函数
     *
     * @param src 密文的字节数组
     */
    public static String decryptMode(String src, String threesKey) {
        try {
            byte[] targetSrc = Base64Util.decode(src);
            SecretKey desKey = new SecretKeySpec(build3DesKey(threesKey), ALGORITHM);
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, desKey);
            assert targetSrc != null;
            return new String(c1.doFinal(targetSrc), CharsetCode.forUtf8());
        } catch (Exception ignored) {  
        }
        return "";
    }

    /**
     * 生成MD5数据签名
     *
     * @param jsonStr 数据体的JSON格式的字符串,不能是JSON ARRAY类型字符串
     */
    @SuppressWarnings("unchecked")
    public static String getSign(String jsonStr) {
        // 判断需要生成的签名字符串是否为空
        if (jsonStr != null && jsonStr.trim().length() > 0) {
            // 将JSON格式字符串转换成TREEMAP进行属性KEY值升序排列
            TreeMap<String, Object> jsonMap = JSONObject.parseObject(jsonStr, TreeMap.class);
            // 瘵签名数据进午签明前格式接装key=value且用&连接
            if (jsonMap != null && jsonMap.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String key : jsonMap.keySet()) {
                    sb.append(key).append("=").append(jsonMap.get(key)).append("&");
                }
                String md5Sign = sb.substring(0, sb.length() - 1);
                // 进行MD5签名
                try {
                    return md5Encoding(md5Sign);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("数据签名发生异常 : " + e.getMessage()); 
                }
            }
        }
        return null;
    }

    /**
     * 对数据进行验签
     *
     * @param jsonData JSON格式的字符串
     * @param sign     原签名字符串
     * @throws NoSuchAlgorithmException
     */
    @SuppressWarnings("unchecked")
    public static boolean checkSign(String jsonData, String sign) throws NoSuchAlgorithmException {
        if (jsonData == null) {
            throw new RuntimeException("签名内容不能为空"); 
        }
        if (sign == null) {
            throw new RuntimeException("原签名内容不能为空"); 
        }
        TreeMap<String, Object> testMap = JSONObject.parseObject(jsonData, TreeMap.class);
        String checkData = testMap.get("data").toString();
        TreeMap<String, Object> tesCheckDataMap = JSONObject.parseObject(checkData, TreeMap.class);
        // 对签名的数据体做拼接
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : tesCheckDataMap.keySet()) {
            stringBuilder.append(key).append("=").append(tesCheckDataMap.get(key)).append("&");
        }
        String checkMd5Sign = stringBuilder.substring(0, stringBuilder.length() - 1);
        // 进行MD5加密，并与原签名进行比对
        String signStr = md5Encoding(checkMd5Sign);
        return StringUtils.equals(signStr, sign);
    }

    /**
     * 根据字符串生成密钥24位的字节数组
     *
     * @param keyStr
     * @return
     */
    public static byte[] build3DesKey(String keyStr) {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes(CharsetCode.forUtf8());
        System.arraycopy(temp, 0, key, 0, Math.min(key.length, temp.length));
        return key;
    }

    /**
     * 根据字符串生成密钥24位的字节数组
     *
     * @param keyStr
     * @return
     */
    public static String build3DesKeyToStr(String keyStr) {
        byte[] key = build3DesKey(keyStr);
        return new String(key);
    }

    /**
     * 生成一个DES密钥
     *
     * @return String
     */
    public static String getKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56);
            SecretKey generateKey = keyGenerator.generateKey();
            // 转变为字节数组
            byte[] encoded = generateKey.getEncoded();
            // 生成密钥字符串
            return Hex.encodeHexString(encoded);
        } catch (Exception e) {
            return "密钥生成错误.";
        }
    }

    /**
     * 获取配置系统DES3加密密钥
     *
     * @author zhicong.lin
     * @date 2019/6/27
     */
    public static String getSystemEncrypt(String envKey) {
        final String property = SpringContextHolder.getProperty(envKey);
        if (StringUtils.isBlank(property)) {
            final String des3Key = SpringContextHolder.getProperty("cgcg.format.des3");
            if (StringUtils.isNotBlank(des3Key)) {
                return des3Key;
            }
        } else {
            return property;
        }
        return null;
    }

}
