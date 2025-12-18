package cn.barcke.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UserResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

