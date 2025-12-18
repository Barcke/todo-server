package com.barcke.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className ResultEnum
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 错误码枚举
 **/
@Getter
@AllArgsConstructor
public enum ResultEnum {
    /**
     * Token错误
     */
    TOKEN_ERROR("TOKEN_ERROR", "Token无效或已过期"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR("PASSWORD_ERROR", "密码错误"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS("USERNAME_EXISTS", "用户名已存在"),

    /**
     * 用户名或密码格式不正确
     */
    INVALID_FORMAT("INVALID_FORMAT", "用户名或密码格式不正确"),

    /**
     * 用户已被冻结
     */
    USER_FROZEN("USER_FROZEN", "用户已被冻结"),

    /**
     * 未授权
     */
    UNAUTHORIZED("UNAUTHORIZED", "未授权"),

    /**
     * 参数错误
     */
    PARAM_ERROR("PARAM_ERROR", "参数错误");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误消息
     */
    private final String message;
}

