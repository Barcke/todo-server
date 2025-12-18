package com.barcke.service;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className KmsService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 密钥管理服务接口
 **/
public interface KmsService {

    /**
     * 获取或生成用户密钥
     * 如果用户密钥不存在，则生成新密钥并使用主密钥加密后存储
     *
     * @param userId 用户ID
     * @return 用户密钥（明文，32字节）
     */
    byte[] getOrGenerateUserKey(String userId);

    /**
     * 使用主密钥加密用户密钥
     *
     * @param userKey 用户密钥（明文）
     * @return 加密后的用户密钥（Base64编码）
     */
    String encryptUserKey(byte[] userKey);

    /**
     * 使用主密钥解密用户密钥
     *
     * @param encryptedKey 加密后的用户密钥（Base64编码）
     * @return 用户密钥（明文，32字节）
     */
    byte[] decryptUserKey(String encryptedKey);

    /**
     * 密钥轮换（生成新密钥并更新版本号）
     *
     * @param userId 用户ID
     * @return 新的用户密钥（明文，32字节）
     */
    byte[] rotateUserKey(String userId);
}

