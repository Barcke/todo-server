package cn.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TemplateTodoResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 模板项响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateTodoResponse {
    /**
     * 模板Todo ID
     */
    private String id;

    /**
     * Todo 内容
     */
    private String title;

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
     * 排序
     */
    private Integer sortOrder;
}

