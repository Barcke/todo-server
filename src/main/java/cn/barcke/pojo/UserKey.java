package cn.barcke.pojo;

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
 * @className UserKey
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户密钥实体类，存储使用主密钥加密后的用户密钥
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "user_key", uniqueConstraints = {
    @UniqueConstraint(columnNames = "user_id")
})
public class UserKey {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 64)
    private String id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, unique = true, length = 64)
    private String userId;

    /**
     * 加密后的用户密钥（使用主密钥加密）
     */
    @Column(name = "encrypted_key", nullable = false, columnDefinition = "TEXT")
    private String encryptedKey;

    /**
     * 密钥版本号（用于密钥轮换）
     */
    @Column(name = "key_version", nullable = false)
    @Builder.Default
    private Integer keyVersion = 1;

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

