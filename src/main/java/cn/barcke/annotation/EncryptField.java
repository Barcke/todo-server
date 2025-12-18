package cn.barcke.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className EncryptField
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 标记需要加密的字段注解
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {
    /**
     * 是否启用加密（默认启用）
     */
    boolean enabled() default true;
}

