package cn.barcke.common;

import lombok.Data;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className Result
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 统一响应结果类
 **/
@Data
public class Result<T> {
    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    private Result() {
    }

    private Result(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data);
    }

    /**
     * 成功响应（仅消息）
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(true, message, null);
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(false, message, null);
    }

    /**
     * 失败响应（带消息和数据）
     */
    public static <T> Result<T> fail(String message, T data) {
        return new Result<>(false, message, data);
    }
}

