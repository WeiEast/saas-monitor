package com.treefinance.saas.monitor.common.utils;


import com.treefinance.toolkit.util.Base64Codec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class AESUtils {

    /**
     * 解密数据
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrytData(byte[] data, String key) throws Exception {
        // 1.aes解密
        data = Helper.decrypt(data, key);
        return new String(data);
    }

    /**
     * 解密base64数据
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrytDataWithBase64AsString(String data, String key) throws Exception {
        byte[] bytes = Base64Codec.decode(data);
        return decrytData(bytes, key);
    }

    /**
     * 数据加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptData(byte[] data, String key) throws Exception {
        byte[] bytes = Helper.encrypt(data, key);
        return Base64Codec.encode(bytes);
    }

    /**
     * 数据加密 base64
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrytDataWithBase64AsString(String data, String key) throws Exception {
        byte[] bytes = Helper.encrypt(data.getBytes(), key);
        return Base64Codec.encode(bytes);
    }

    /**
     * 加解密辅助类
     */
    private static class Helper {
        private static final String ALGORITHM = "AES";
        private static final int KEY_SIZE = 128;
        private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
        private static final byte[] IV = {0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35,
                0x30, 0x36, 0x30, 0x37,
                0x30, 0x38};


        private static final String TEST_TXT = "Hello, world;---Hello, world;---";

        public static void main(String[] args) throws Exception {
            String keyString = generateKeyString();

            // 加密串转成SecretKeySpec对象
            SecretKeySpec secretKeySpec = getSecretKey(keyString);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            // 初始化加密
            cipher.init(1, secretKeySpec, new IvParameterSpec(IV));

            // 加密
            byte[] data = cipher.doFinal(TEST_TXT.getBytes());
            String encryptedText = Base64.getEncoder().encodeToString(data);
            System.out.println(encryptedText);

            // 初始化解密
            cipher.init(2, secretKeySpec, new IvParameterSpec(IV));

            // 解密
            data = Base64.getDecoder().decode(encryptedText);
            data = cipher.doFinal(data);
            System.out.println(new String(data));
        }

        /**
         * 加密数据
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        private static byte[] decrypt(byte[] data, String key) throws Exception {
            SecretKeySpec secretKeySpec = getSecretKey(key);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV));
            return cipher.doFinal(data);
        }

        /**
         * 解密数据
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        private static byte[] encrypt(byte[] data, String key) throws Exception {
            SecretKeySpec secretKeySpec = getSecretKey(key);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV));
            return cipher.doFinal(data);
        }

        private static SecretKeySpec getSecretKey(String keyString) {
            return new SecretKeySpec(Base64.getDecoder().decode(keyString),
                    ALGORITHM);
        }

        private static String generateKeyString() throws NoSuchAlgorithmException {
            SecretKey secretKey = generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }

        private static SecretKey generateKey() throws NoSuchAlgorithmException {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE);
            return keyGenerator.generateKey();
        }
    }

}
