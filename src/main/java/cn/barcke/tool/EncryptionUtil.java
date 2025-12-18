package cn.barcke.tool;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className EncryptionUtil
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: AES-256-GCM加密工具类
 **/
@Slf4j
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // GCM推荐IV长度为12字节
    private static final int GCM_TAG_LENGTH = 16; // GCM认证标签长度为16字节
    private static final int KEY_LENGTH = 32; // AES-256密钥长度为32字节

    /**
     * 生成AES-256密钥
     */
    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e) {
            log.error("生成密钥失败", e);
            throw new RuntimeException("生成密钥失败", e);
        }
    }

    /**
     * 加密数据
     *
     * @param plaintext 明文
     * @param key       密钥（32字节）
     * @return Base64编码的密文（格式：IV + 密文 + 认证标签）
     */
    public static String encrypt(String plaintext, byte[] key) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }

        try {
            // 验证密钥长度
            if (key.length != KEY_LENGTH) {
                throw new IllegalArgumentException("密钥长度必须为32字节（AES-256）");
            }

            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // 创建密钥规范
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);

            // 创建GCM参数规范
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

            // 初始化加密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

            // 执行加密
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // 组合IV和密文（IV + 密文）
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedBytes);

            // Base64编码返回
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("加密失败", e);
            throw new RuntimeException("加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解密数据
     *
     * @param ciphertext Base64编码的密文（格式：IV + 密文 + 认证标签）
     * @param key        密钥（32字节）
     * @return 明文
     */
    public static String decrypt(String ciphertext, byte[] key) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return ciphertext;
        }

        try {
            // 验证密钥长度
            if (key.length != KEY_LENGTH) {
                throw new IllegalArgumentException("密钥长度必须为32字节（AES-256）");
            }

            // Base64解码
            byte[] encryptedData = Base64.getDecoder().decode(ciphertext);

            // 提取IV和密文
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] encryptedBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedBytes);

            // 创建密钥规范
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);

            // 创建GCM参数规范
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

            // 初始化解密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

            // 执行解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密失败", e);
            throw new RuntimeException("解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从Base64字符串转换为字节数组
     */
    public static byte[] keyFromBase64(String base64Key) {
        return Base64.getDecoder().decode(base64Key);
    }

    /**
     * 将字节数组转换为Base64字符串
     */
    public static String keyToBase64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }
}

