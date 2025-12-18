package cn.barcke.aspect;

import cn.barcke.annotation.EncryptField;
import cn.barcke.common.BarckeContext;
import cn.barcke.service.KmsService;
import cn.barcke.tool.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    /**
     * 拦截Repository的save方法，保存前加密
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.save(..))")
    public Object encryptBeforeSave(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!kmsEnabled) {
            return joinPoint.proceed();
        }

        Object entity = joinPoint.getArgs()[0];
        if (entity == null) {
            return joinPoint.proceed();
        }

        try {
            // 获取用户ID（从上下文或实体中获取）
            String userId = getUserIdFromEntity(entity);
            if (userId == null) {
                log.warn("无法获取用户ID，跳过加密");
                return joinPoint.proceed();
            }

            // 获取用户密钥
            byte[] userKey = kmsService.getOrGenerateUserKey(userId);
            if (userKey == null) {
                log.warn("无法获取用户密钥，跳过加密");
                return joinPoint.proceed();
            }

            // 加密实体字段
            encryptEntityFields(entity, userKey);

            // 继续执行保存操作
            Object result = joinPoint.proceed();

            // 保存后解密（恢复实体状态，避免影响后续操作）
            decryptEntityFields(entity, userKey);

            return result;
        } catch (Exception e) {
            log.error("加密失败", e);
            throw e;
        }
    }

    /**
     * 拦截Repository的findById方法，查询后解密
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.findById(..))")
    public Object decryptAfterFindById(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!kmsEnabled) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        if (result == null) {
            return result;
        }

        // Optional类型处理
        if (result instanceof Optional) {
            Optional<?> optional = (Optional<?>) result;
            if (optional.isPresent()) {
                Object entity = optional.get();
                decryptEntity(entity);
            }
        }

        return result;
    }

    /**
     * 拦截Repository的findAll方法，查询后解密
     */
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.findAll(..))")
    public Object decryptAfterFindAll(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!kmsEnabled) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        if (result instanceof Collection) {
            Collection<?> collection = (Collection<?>) result;
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
        if (!kmsEnabled) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        if (result == null) {
            return result;
        }

        // 处理单个实体
        if (isEntityType(result.getClass())) {
            decryptEntity(result);
        }
        // 处理集合
        else if (result instanceof Collection) {
            Collection<?> collection = (Collection<?>) result;
            for (Object entity : collection) {
                if (entity != null && isEntityType(entity.getClass())) {
                    decryptEntity(entity);
                }
            }
        }
        // 处理Optional
        else if (result instanceof Optional) {
            Optional<?> optional = (Optional<?>) result;
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
                if (value != null && value instanceof String) {
                    String plaintext = (String) value;
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
                if (value != null && value instanceof String) {
                    String ciphertext = (String) value;
                    if (!ciphertext.isEmpty()) {
                        try {
                            String plaintext = EncryptionUtil.decrypt(ciphertext, userKey);
                            field.set(entity, plaintext);
                        } catch (Exception e) {
                            // 如果解密失败，可能是未加密的数据（向后兼容）
                            log.debug("解密失败，可能是未加密数据: {}", field.getName());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("解密字段失败: {}", field.getName(), e);
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

        try {
            String userId = getUserIdFromEntity(entity);
            if (userId == null) {
                log.debug("无法获取用户ID，跳过解密");
                return;
            }

            byte[] userKey = kmsService.getOrGenerateUserKey(userId);
            if (userKey == null) {
                log.debug("无法获取用户密钥，跳过解密");
                return;
            }

            decryptEntityFields(entity, userKey);
        } catch (Exception e) {
            log.error("解密实体失败", e);
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
     */
    private String getUserIdFromEntity(Object entity) {
        // 优先从上下文获取
        try {
            String userId = BarckeContext.getUserId();
            if (userId != null) {
                return userId;
            }
        } catch (Exception e) {
            // 忽略
        }

        // 从实体字段中获取userId
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

