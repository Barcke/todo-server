package cn.barcke.dto.todo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TemplateCreateRequest
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 创建模板请求DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateCreateRequest {

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /**
     * 模板说明
     */
    private String description;
}

