package cn.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className RepeatRule
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 重复规则对象
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepeatRule {
    /**
     * 重复类型：daily/weekly/monthly
     */
    private String type;

    /**
     * 按周重复：周几的数组（1=周一，7=周日）
     * 按月重复：每月的几号数组（1-31）
     */
    private List<Integer> days;
}

