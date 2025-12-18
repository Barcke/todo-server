package cn.barcke.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className LoginRequest
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户登录请求DTO
 **/
@Data
public class LoginRequest {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

