package cn.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className MonthStatisticsResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 月度统计响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthStatisticsResponse {
    /**
     * 月份（格式：yyyy-MM）
     */
    private String month;

    /**
     * 总任务数
     */
    private Integer totalCount;

    /**
     * 已完成数量
     */
    private Integer completedCount;

    /**
     * 完成率
     */
    private Double completionRate;
}

