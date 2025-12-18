package com.barcke.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className LoginResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 登录响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserResponse user;
}

