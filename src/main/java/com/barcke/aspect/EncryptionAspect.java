package com.barcke.aspect;

import com.barcke.annotation.EncryptField;
import com.barcke.common.BarckeContext;
import com.barcke.service.KmsService;
import com.barcke.tool.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className EncryptionAspect
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 加密切面，拦截JPA Repository的保存和查询操作
 **/
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EncryptionAspect {

    private final KmsService kmsService;

    @Value("${kms.enabled:true}")
    private boolean kmsEnabled;

    // 缓存实体类的加密字段信息，提高性能
    private final Map<Class<?>, List<Field>> encryptedFieldsCache = new HashMap<>();

    // 使用ThreadLocal标记当前线程是否正在处理解密，避免循环拦截
    private static final ThreadLocal<Boolean> DECRYPTING_FLAG = ThreadLocal.withInitial(() -> false);

    /**
     * 拦截Repository的save方法，保存前加密
     * 注意：解密操作在事务提交后执行，确保数据库保存的是加密数据
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.save(..))")
    public Object encryptBeforeSave(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!kmsEnabled || DECRYPTING_FLAG.get()) {
            return joinPoint.proceed();
        }

        Object entity = joinPoint.getArgs()[0];
        if (entity == null) {
            return joinPoint.proceed();
        }

        try {
            // 设置标志，避免在获取密钥时触发循环查询
            DECRYPTING_FLAG.set(true);
            
            // 获取用户ID（从上下文或实体中获取）
            String userId = getUserIdFromEntity(entity);
            if (userId == null) {
                log.warn("无法获取用户ID，跳过加密");
                return joinPoint.proceed();
            }

            // 获取用户密钥（此时标志已设置，kmsService内部的查询不会被拦截）
            byte[] userKey = kmsService.getOrGenerateUserKey(userId);
            if (userKey == null) {
                log.warn("无法获取用户密钥，跳过加密");
                return joinPoint.proceed();
            }

            // 清除标志，允许后续的保存操作正常进行
            DECRYPTING_FLAG.set(false);

            // 加密实体字段
            encryptEntityFields(entity, userKey);

            // 继续执行保存操作（此时数据是加密的）
            Object result = joinPoint.proceed();

            // 检查是否在事务中
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                // 在事务中：注册事务同步回调，在事务提交后解密
                // 这样可以确保数据库保存的是加密数据
                final Object finalEntity = result;
                final byte[] finalUserKey = userKey;
                TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            // 事务提交后解密，恢复实体状态
                            try {
                                decryptEntityFields(finalEntity, finalUserKey);
                            } catch (Exception e) {
                                log.error("事务提交后解密失败", e);
                            }
                        }
                    }
                );
            } else {
                // 不在事务中：立即解密（向后兼容）
                decryptEntityFields(result, userKey);
            }

            return result;
        } catch (Exception e) {
            log.error("加密失败", e);
            throw e;
        } finally {
            // 确保标志被清除
            DECRYPTING_FLAG.set(false);
        }
    }

    /**
     * 拦截Repository的findById方法，查询后解密
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.findById(..))")
    public Object decryptAfterFindById(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!kmsEnabled || DECRYPTING_FLAG.get()) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        if (result == null) {
            return null;
        }

        // Optional类型处理
        if (result instanceof Optional<?> optional) {
            optional.ifPresent(this::decryptEntity);
        }

        return result;
    }

    /**
     * 拦截Repository的findAll方法，查询后解密
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.findAll(..))")
    public Object decryptAfterFindAll(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!kmsEnabled || DECRYPTING_FLAG.get()) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        if (result instanceof Collection<?> collection) {
            for (Object entity : collection) {
                decryptEntity(entity);
            }
        }

        return result;
    }

    /**
     * 拦截Repository的自定义查询方法（findBy*），查询后解密
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.find*(..))")
    public Object decryptAfterFind(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!kmsEnabled || DECRYPTING_FLAG.get()) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        if (result == null) {
            return null;
        }

        // 处理单个实体
        if (isEntityType(result.getClass())) {
            decryptEntity(result);
        }
        // 处理集合
        else if (result instanceof Collection<?> collection) {
            for (Object entity : collection) {
                if (entity != null && isEntityType(entity.getClass())) {
                    decryptEntity(entity);
                }
            }
        }
        // 处理Optional
        else if (result instanceof Optional<?> optional) {
            if (optional.isPresent()) {
                Object entity = optional.get();
                if (isEntityType(entity.getClass())) {
                    decryptEntity(entity);
                }
            }
        }

        return result;
    }

    /**
     * 加密实体字段
     */
    private void encryptEntityFields(Object entity, byte[] userKey) {
        List<Field> encryptedFields = getEncryptedFields(entity.getClass());
        for (Field field : encryptedFields) {
            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof String plaintext) {
                    if (!plaintext.isEmpty()) {
                        String ciphertext = EncryptionUtil.encrypt(plaintext, userKey);
                        field.set(entity, ciphertext);
                    }
                }
            } catch (Exception e) {
                log.error("加密字段失败: {}", field.getName(), e);
            }
        }
    }

    /**
     * 解密实体字段
     */
    private void decryptEntityFields(Object entity, byte[] userKey) {
        List<Field> encryptedFields = getEncryptedFields(entity.getClass());
        for (Field field : encryptedFields) {
            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof String ciphertext) {
                    if (!ciphertext.isEmpty()) {
                        try {
                            String plaintext = EncryptionUtil.decrypt(ciphertext, userKey);
                            field.set(entity, plaintext);
                        } catch (IllegalArgumentException e) {
                            // 数据格式错误（可能是未加密的数据或损坏的数据）
                            // 记录警告但不抛出异常，保持向后兼容
                            log.warn("解密字段失败 - 数据格式错误: 实体={}, 字段={}, 错误={}, 数据长度={}", 
                                entity.getClass().getSimpleName(), 
                                field.getName(), 
                                e.getMessage(),
                                ciphertext.length());
                            // 不修改字段值，保持原样
                        } catch (Exception e) {
                            // 其他解密错误（密钥错误、数据损坏等）
                            log.error("解密字段失败: 实体={}, 字段={}, 错误类型={}, 错误消息={}", 
                                entity.getClass().getSimpleName(),
                                field.getName(),
                                e.getClass().getSimpleName(),
                                e.getMessage(), 
                                e);
                            // 不修改字段值，保持原样
                        }
                    }
                }
            } catch (Exception e) {
                log.error("访问字段失败: 实体={}, 字段={}", 
                    entity.getClass().getSimpleName(), 
                    field.getName(), 
                    e);
            }
        }
    }

    /**
     * 解密实体（自动获取用户密钥）
     */
    private void decryptEntity(Object entity) {
        if (entity == null) {
            return;
        }

        // 如果已经在解密过程中，直接返回，避免循环
        if (DECRYPTING_FLAG.get()) {
            return;
        }

        // 设置解密标志，避免循环拦截
        // 注意：标志应该在调用 getUserIdFromEntity 之前设置，因为访问实体字段可能触发懒加载
        DECRYPTING_FLAG.set(true);

        try {
            String userId = getUserIdFromEntity(entity);
            if (userId == null) {
                log.debug("无法获取用户ID，跳过解密");
                return;
            }

            // 获取用户密钥（此时标志已设置，kmsService内部的查询不会被拦截）
            byte[] userKey = kmsService.getOrGenerateUserKey(userId);
            if (userKey == null) {
                log.debug("无法获取用户密钥，跳过解密");
                return;
            }

            decryptEntityFields(entity, userKey);
        } catch (Exception e) {
            log.error("解密实体失败", e);
        } finally {
            // 清除标志
            DECRYPTING_FLAG.set(false);
        }
    }

    /**
     * 获取实体类中需要加密的字段
     */
    private List<Field> getEncryptedFields(Class<?> clazz) {
        return encryptedFieldsCache.computeIfAbsent(clazz, k -> {
            List<Field> fields = new ArrayList<>();
            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(EncryptField.class)) {
                        EncryptField encryptField = field.getAnnotation(EncryptField.class);
                        if (encryptField.enabled()) {
                            fields.add(field);
                        }
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            return fields;
        });
    }

    /**
     * 从实体中获取用户ID
     * 注意：此方法在 DECRYPTING_FLAG 已设置的情况下调用，避免触发循环查询
     */
    private String getUserIdFromEntity(Object entity) {
        // 优先从上下文获取（不会触发数据库查询）
        try {
            String userId = BarckeContext.getUserId();
            if (userId != null) {
                return userId;
            }
        } catch (Exception e) {
            // 忽略
        }

        // 从实体字段中获取userId
        // 注意：访问字段时可能触发JPA懒加载，但由于已设置DECRYPTING_FLAG，不会再次拦截
        // 使用反射直接访问字段，避免触发懒加载
        try {
            Field userIdField = findField(entity.getClass(), "userId");
            if (userIdField != null) {
                userIdField.setAccessible(true);
                Object userId = userIdField.get(entity);
                if (userId != null) {
                    return userId.toString();
                }
            }
        } catch (Exception e) {
            log.debug("无法从实体获取userId", e);
        }

        return null;
    }

    /**
     * 查找字段（包括父类）
     */
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 判断是否为实体类型（简单判断，避免拦截非实体类）
     */
    private boolean isEntityType(Class<?> clazz) {
        return clazz.isAnnotationPresent(jakarta.persistence.Entity.class);
    }
}

