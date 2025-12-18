package cn.barcke.common;

import lombok.Getter;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className CommonException
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 自定义异常类
 **/
@Getter
public class CommonException extends RuntimeException {
    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误消息
     */
    private final String message;

    public CommonException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    public CommonException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CommonException(String message) {
        super(message);
        this.code = "COMMON_ERROR";
        this.message = message;
    }

    /**
     * 快速创建异常（带消息）
     */
    public static CommonException toast(String message) {
        return new CommonException(message);
    }
}

