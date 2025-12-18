package cn.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTypeResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 类型响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoTypeResponse {
    /**
     * 类型ID
     */
    private String typeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 图标（emoji字符）
     */
    private String icon;

    /**
     * 颜色（可选）
     */
    private String color;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

