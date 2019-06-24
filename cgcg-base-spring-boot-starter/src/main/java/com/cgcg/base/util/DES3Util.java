package com.cgcg.base.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

public class DES3Util {

    // 定义加密算法，DESede即3DES
    private static final String Algorithm = "DESede";
    //    private static final String THREEDES_KEY = "Fwxq1v0Fucxd1FathVKZYsrK";
    private static final String THREEDES_KEY = "20b6252cabfd3415";

    // MD5
    public static String MD5Encoding(String source) throws NoSuchAlgorithmException {
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        byte[] input = source.getBytes();
        mdInst.update(input);
        byte[] output = mdInst.digest();

        int i = 0;

        StringBuilder buf = new StringBuilder();

        for (int offset = 0; offset < output.length; offset++) {
            i = output[offset];

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
     * @return
     */
    public static String encryptMode(String src) {
        try {
            String key = THREEDES_KEY;
            byte[] targetSrc = src.getBytes("utf-8");
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(build3DesKey(key), Algorithm);
            // 实例化Cipher
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            return Base64Util.encode(cipher.doFinal(targetSrc));
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 解密函数
     *
     * @param src 密文的字节数组
     * @return
     */
    public static String decryptMode(String src) {
        try {
            String key = THREEDES_KEY;
            byte[] targetSrc = Base64Util.decode(src);
            SecretKey deskey = new SecretKeySpec(build3DesKey(key), Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return new String(c1.doFinal(targetSrc), Charset.forName("UTF-8"));
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 生成MD5数据签名
     *
     * @param jsonStr 数据体的JSON格式的字符串,不能是JSONARRAY类型字符串
     * @return
     * @throws NoSuchAlgorithmException
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
                    return MD5Encoding(md5Sign);
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
     * @param JsonData JSON格式的字符串
     * @param sign     原签名字符串
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public synchronized static boolean checkSign(String JsonData, String sign)
            throws UnsupportedEncodingException, Exception {
        if (JsonData == null) {
            throw new RuntimeException("签名内容不能为空");
        }
        if (sign == null) {
            throw new RuntimeException("原签名内容不能为空");
        }
        TreeMap<String, Object> testMap = JSONObject.parseObject(JsonData, TreeMap.class);
        String checkData = testMap.get("data").toString();
        TreeMap<String, Object> tescheckDataMap = JSONObject.parseObject(checkData, TreeMap.class);
        // 对签名的数据体做拼接
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : tescheckDataMap.keySet()) {
            stringBuilder.append(key).append("=").append(tescheckDataMap.get(key)).append("&");
        }
        String checkMd5Sign = stringBuilder.substring(0, stringBuilder.length() - 1);
        // 进行MD5加密，并与原签名进行比对
        String signStr = MD5Encoding(checkMd5Sign);
        return StringUtils.equals(signStr, sign);
    }

    /**
     * 根据字符串生成密钥24位的字节数组
     *
     * @param keyStr
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes("UTF-8");

        if (key.length > temp.length) {
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }

    /**
     * 根据字符串生成密钥24位的字节数组
     *
     * @param keyStr
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String build3DesKeyToStr(String keyStr) throws UnsupportedEncodingException {
        byte[] key = build3DesKey(keyStr);
        return new String(key);
    }

    //生成一个DES密钥
    public static String getKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56); // 生成一个Key
            SecretKey generateKey = keyGenerator.generateKey();
            // 转变为字节数组
            byte[] encoded = generateKey.getEncoded();
            // 生成密钥字符串
            String encodeHexString = Hex.encodeHexString(encoded);
            return encodeHexString;
        } catch (Exception e) {
            e.printStackTrace();
            return "密钥生成错误.";
        }
    }

    /**
     * @param
     */
    public static void main1(String[] args) {
        // String str =
        // "nlnncUis7rVqFo33g+b8uhDIhVw1JjTbLPK8Ftmfnvg0a9sDKaYjR5fD3P0CriEYUkLV9QYuaXknY1A7lsYo7ed2eoSnNIEfwPx1BAi3gdCsqAsjOshM7I/3IypYUg0KmptWNIs8khezwBwdMIbDTgCxEO74oM2pDS20K8junRQ=";
        // String data =
        //  String data = "{\"codeType\":\"8\",\"phoneNum\":\"13152644039\"}";
        // "{\"productpId\":\"2132132121321312\",\"deviceId\":\"21312312\"}";
        //String data = "{\"homeType\":\"7\"}";
        String data = "{\"inviteCode\":\"\",\"password\":\"\",\"deviceId\":\"00000000\",\"phoneType\":\"iPhone\",\"phoneNum\":\"17502181535\",\"source\":\"1\",\"verifyCode\":\"8702\",\"channel\":\"1\"}";
        // String data = "{\"productpId\":\"4\",\"deviceId\":\"32423432\"}";
        //	String data = "{\"articleId\":\"21321\",\"token\":\"dc5eb73ff73b78e45ce7970bce7cb509\",\"praiseType\":\"1\"}";
        // String data =
        // "{\"isRecommend\":\"2\",\"prefectureType\":\"0001\",\"productType\":\"0001\",\"prefectureType\":\"0001\",\"userType\":\"0001\",\"sort\":\"0001\"}";
        //String data ={\"isPush\":\"1\"};
        String sign = getSign(data);
        String str = "{\"data\":" + data + ",\"sign\":\"" + sign + "\"}";
		/*System.out.println(str);
		System.out.println(encryptMode(str, ConstantUtils.THREEDES_KEY));*/

        System.out.println(decryptMode("KEMsBy4mrZnJptDH8EgZZ6Rwxf7jpTy6zBUSDe2XGABi6CmQG9VUJjwGoRjUjurSXIAP/vXtgmXzrKLZ1rCWb8K7YJU8ntlyPhFugpleMTLrGlzXR76jCHuVYw9VPLc0Elo5sRIqxVpIQi02vy6YH6z5XOppkUa4gxNhdzBW5suuPJwzTm4wJ1VdvjnarGvEjq7+2P7dhnj1gFSucYS8xzexmj29sxIgC0eG3y/pDYq2WciqTsb51mpWKFxAsf/lIpZl8Hr1MtsNF/rsHJhnXIhRAl3EaazpT1ErMC0Rgal842mEJXiNY8VfNWIKAJH+xbuurWDnWrsuC57Y3uufNZYls5iu9cFD"));
    }

    public static void main(String[] args) {
        String key = getKey();
        System.out.println(key);
//		System.out.println(DES3Util.decryptMode("KEMsBy4mrZnJptDH8EgZZ34jpqFhEsIfSCqN8ppEyI1lpOniGyXwe0BwUT6RgnEgcxnnsVm+3DHzrKLZ1rCWb8K7YJU8ntlyPhFugpleMTLlOmfmXieLPll9/sONqAcwr58JfEm//zQP92zvwwMQeyW/jCooqpjynADEhIcyOfg/wPCJqs/vRDWNuJsEdo3R+Dm/UpbyM3iU6nDy4v74ji1GEZyekM+yEljbkgUpLEWCotfr1tGKR44VoC7qtt0zF/vNzDZjmMjLKg26ndOY4/pf5cGXuuNwvvPisQCtuYpcPqYX1dQeONVRhcBAxSGJvu5kbio9LUGG3A2aRMO44tqssVH/Et/z7aPOxboFa2ccWTleqU4cLQ=="));
    }
}
