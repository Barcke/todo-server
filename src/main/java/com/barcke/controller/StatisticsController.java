package com.barcke.controller;

import com.barcke.common.Result;
import com.barcke.dto.todo.MonthStatisticsResponse;
import com.barcke.dto.todo.TypeStatisticsResponse;
import com.barcke.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className StatisticsController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 统计控制器
 **/
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 月度统计
     */
    @GetMapping("/month")
    public Result<MonthStatisticsResponse> getMonthStatistics(@RequestParam String yearMonth) {
        MonthStatisticsResponse response = statisticsService.getCompletionStatsByMonth(yearMonth);
        return Result.success(response);
    }

    /**
     * 类型统计
     */
    @GetMapping("/type")
    public Result<List<TypeStatisticsResponse>> getTypeStatistics(@RequestParam String yearMonth) {
        List<TypeStatisticsResponse> responses = statisticsService.getTypeStatsByMonth(yearMonth);
        return Result.success(responses);
    }
}

