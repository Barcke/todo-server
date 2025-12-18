package com.barcke.dto.todo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTypeRequest
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 类型请求DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoTypeRequest {

    /**
     * 类型名称
     */
    @NotBlank(message = "类型名称不能为空")
    private String typeName;

    /**
     * 图标（emoji字符）
     */
    private String icon;

    /**
     * 颜色（可选）
     */
    private String color;
}

