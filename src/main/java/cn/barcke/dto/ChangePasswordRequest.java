package cn.barcke.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className ChangePasswordRequest
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 修改密码请求DTO
 **/
@Data
public class ChangePasswordRequest {
    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

