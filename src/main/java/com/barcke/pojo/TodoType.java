package com.barcke.pojo;

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
 * @className TodoType
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 类型实体类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "todo_type")
public class TodoType {

    /**
     * 类型ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "type_id", length = 64)
    private String typeId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /**
     * 类型名称
     */
    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    /**
     * 图标（emoji字符）
     */
    @Column(name = "icon", length = 10)
    private String icon;

    /**
     * 颜色（可选）
     */
    @Column(name = "color", length = 20)
    private String color;

    /**
     * 删除标志（false-正常，true-已删除）
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

