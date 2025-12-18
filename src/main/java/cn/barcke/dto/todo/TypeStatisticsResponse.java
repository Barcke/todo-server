package cn.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TypeStatisticsResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 类型统计响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeStatisticsResponse {
    /**
     * 月份（格式：yyyy-MM）
     */
    private String month;

    /**
     * 类型ID
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
     * 完成次数
     */
    private Integer completedCount;

    /**
     * 未完成次数
     */
    private Integer pendingCount;
}

