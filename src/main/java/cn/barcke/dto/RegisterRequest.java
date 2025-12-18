package cn.barcke.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className RegisterRequest
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户注册请求DTO
 **/
@Data
public class RegisterRequest {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;
}

