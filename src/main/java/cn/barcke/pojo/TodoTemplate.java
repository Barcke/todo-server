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
 * @className TodoTemplate
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 模板实体类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "todo_template")
public class TodoTemplate {

    /**
     * 模板ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_id", length = 64)
    private String templateId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /**
     * 模板名称
     */
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    /**
     * 模板说明
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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

    /**
     * 删除标志（false-正常，true-已删除）
     */
    @Column(name = "del_flag", nullable = false)
    @Builder.Default
    private Boolean delFlag = false;

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

