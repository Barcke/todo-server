package com.barcke.service;

import com.barcke.dto.todo.MonthStatisticsResponse;
import com.barcke.dto.todo.TypeStatisticsResponse;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className StatisticsService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 统计服务接口
 **/
public interface StatisticsService {

    /**
     * 按月统计完成情况
     */
    MonthStatisticsResponse getCompletionStatsByMonth(String yearMonth);

    /**
     * 按月+类型统计
     */
    List<TypeStatisticsResponse> getTypeStatsByMonth(String yearMonth);

    /**
     * 计算完成率
     */
    Double getCompletionRate(String yearMonth);
}

