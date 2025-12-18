package com.barcke.service.impl;

import com.barcke.dao.UserKeyRepository;
import com.barcke.pojo.UserKey;
import com.barcke.service.KmsService;
import com.barcke.tool.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className KmsServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 密钥管理服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class KmsServiceImpl implements KmsService {

    private final UserKeyRepository userKeyRepository;

    @Value("${kms.masterKey}")
    private String masterKeyBase64;

    @Value("${kms.enabled:true}")
    private boolean kmsEnabled;

    /**
     * 获取主密钥（字节数组）
     */
    private byte[] getMasterKey() {
        return EncryptionUtil.keyFromBase64(masterKeyBase64);
    }

    @Override
    @Transactional
    public byte[] getOrGenerateUserKey(String userId) {
        if (!kmsEnabled) {
            log.warn("KMS未启用，返回空密钥");
            return null;
        }

        // 查询用户密钥
        Optional<UserKey> userKeyOptional = userKeyRepository.findByUserId(userId);

        if (userKeyOptional.isPresent()) {
            // 密钥存在，解密后返回
            UserKey userKey = userKeyOptional.get();
            return decryptUserKey(userKey.getEncryptedKey());
        } else {
            // 密钥不存在，生成新密钥
            log.info("为用户 {} 生成新密钥", userId);
            byte[] newUserKey = EncryptionUtil.generateKey();
            String encryptedKey = encryptUserKey(newUserKey);

            // 保存加密后的密钥
            UserKey userKey = UserKey.builder()
                    .userId(userId)
                    .encryptedKey(encryptedKey)
                    .keyVersion(1)
                    .build();

            userKeyRepository.save(userKey);
            return newUserKey;
        }
    }

    @Override
    public String encryptUserKey(byte[] userKey) {
        if (!kmsEnabled) {
            return EncryptionUtil.keyToBase64(userKey);
        }

        // 将用户密钥转换为Base64字符串，然后使用主密钥加密
        String userKeyBase64 = EncryptionUtil.keyToBase64(userKey);
        byte[] masterKey = getMasterKey();
        return EncryptionUtil.encrypt(userKeyBase64, masterKey);
    }

    @Override
    public byte[] decryptUserKey(String encryptedKey) {
        if (!kmsEnabled) {
            return EncryptionUtil.keyFromBase64(encryptedKey);
        }

        // 使用主密钥解密
        byte[] masterKey = getMasterKey();
        String userKeyBase64 = EncryptionUtil.decrypt(encryptedKey, masterKey);
        return EncryptionUtil.keyFromBase64(userKeyBase64);
    }

    @Override
    @Transactional
    public byte[] rotateUserKey(String userId) {
        if (!kmsEnabled) {
            log.warn("KMS未启用，无法轮换密钥");
            return null;
        }

        // 生成新密钥
        byte[] newUserKey = EncryptionUtil.generateKey();
        String encryptedKey = encryptUserKey(newUserKey);

        // 查询现有密钥
        Optional<UserKey> userKeyOptional = userKeyRepository.findByUserId(userId);

        if (userKeyOptional.isPresent()) {
            // 更新密钥和版本号
            UserKey userKey = userKeyOptional.get();
            userKey.setEncryptedKey(encryptedKey);
            userKey.setKeyVersion(userKey.getKeyVersion() + 1);
            userKeyRepository.save(userKey);
            log.info("用户 {} 密钥已轮换，新版本: {}", userId, userKey.getKeyVersion());
        } else {
            // 如果密钥不存在，创建新密钥
            UserKey userKey = UserKey.builder()
                    .userId(userId)
                    .encryptedKey(encryptedKey)
                    .keyVersion(1)
                    .build();
            userKeyRepository.save(userKey);
            log.info("为用户 {} 创建新密钥", userId);
        }

        return newUserKey;
    }
}

