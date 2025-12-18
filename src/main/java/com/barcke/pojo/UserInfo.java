package com.barcke.pojo;

import com.barcke.annotation.EncryptField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UserInfo
 * @date 2025/12/16 19:58
 * @slogan: 源于生活 高于生活
 * @description: 用户信息实体类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "user_info", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username")
})
public class UserInfo {

    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", length = 64)
    private String userId;

    /**
     * 账号（用户名）
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 昵称
     */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /**
     * 邮箱
     */
    @EncryptField
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 手机号
     */
    @EncryptField
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 删除标志（false-正常，true-已删除/冻结）
     */
    @Column(name = "del_flag", nullable = false)
    @Builder.Default
    private Boolean delFlag = false;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
