package com.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoUpdateRequest
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 更新 Todo 请求DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoUpdateRequest {

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
     * Todo 类型ID
     */
    private String typeId;

    /**
     * 重复类型（none/daily/weekly/monthly）
     */
    private String repeatType;

    /**
     * 重复规则
     */
    private RepeatRule repeatRule;

    /**
     * 附件ID列表
     */
    private List<String> attachmentIds;
}

