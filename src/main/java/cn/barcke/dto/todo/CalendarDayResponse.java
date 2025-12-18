package cn.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className CalendarDayResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 月视图日期单元格数据响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDayResponse {
    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 当日 Todo 总数
     */
    private Integer totalCount;

    /**
     * 已完成数量
     */
    private Integer completedCount;

    /**
     * 完成比例
     */
    private Double completionRate;

    /**
     * 当日完成的 Todo 类型图标列表
     */
    private List<String> completedTypeIcons;

    /**
     * 是否存在未完成 Todo
     */
    private Boolean hasUnfinished;
}

