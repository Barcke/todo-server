package cn.barcke.pojo;

import cn.barcke.annotation.EncryptField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className Todo
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 实体类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "todo")
public class Todo {

    /**
     * Todo ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 64)
    private String id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /**
     * Todo 内容
     */
    @EncryptField
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 备注/备忘（支持富文本/语音转文字）
     */
    @EncryptField
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 任务所属日期
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * 时间（可选，仅日/周视图需要）
     */
    @Column(name = "time")
    private LocalTime time;

    /**
     * 状态（pending/completed）
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "pending";

    /**
     * Todo 类型ID
     */
    @Column(name = "type_id", length = 64)
    private String typeId;

    /**
     * 来源（normal/template）
     */
    @Column(name = "source", length = 20)
    @Builder.Default
    private String source = "normal";

    /**
     * 模板ID（可选，来源模板）
     */
    @Column(name = "template_id", length = 64)
    private String templateId;

    /**
     * 重复类型（none/daily/weekly/monthly）
     */
    @Column(name = "repeat_type", length = 20)
    @Builder.Default
    private String repeatType = "none";

    /**
     * 重复规则（JSON字符串，存储重复规则，如周几、每月几号）
     */
    @Column(name = "repeat_rule", columnDefinition = "TEXT")
    private String repeatRule;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 完成时间
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

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

