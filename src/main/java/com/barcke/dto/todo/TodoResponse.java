package com.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponse {
    /**
     * Todo ID
     */
    private String id;

    /**
     * Todo 内容
     */
    private String title;

    /**
     * 备注/备忘
     */
    private String description;

    /**
     * 任务所属日期
     */
    private LocalDate date;

    /**
     * 时间（可选）
     */
    private LocalTime time;

    /**
     * 状态（pending/completed）
     */
    private String status;

    /**
     * Todo 类型ID
     */
    private String typeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型图标
     */
    private String typeIcon;

    /**
     * 来源（normal/template）
     */
    private String source;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 重复类型
     */
    private String repeatType;

    /**
     * 重复规则
     */
    private RepeatRule repeatRule;

    /**
     * 附件列表
     */
    private List<AttachmentResponse> attachments;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

