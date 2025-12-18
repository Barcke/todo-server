package cn.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TemplateResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 模板响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponse {
    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板说明
     */
    private String description;

    /**
     * 模板项列表
     */
    private List<TemplateTodoResponse> todos;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

