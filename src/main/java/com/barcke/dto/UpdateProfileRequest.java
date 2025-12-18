package com.barcke.dto;

import lombok.Data;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UpdateProfileRequest
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 更新用户信息请求DTO
 **/
@Data
public class UpdateProfileRequest {
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;
}

