package com.barcke.controller;

import com.barcke.common.Result;
import com.barcke.dto.todo.CalendarDayResponse;
import com.barcke.dto.todo.TodoResponse;
import com.barcke.service.CalendarService;
import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className CalendarController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 日历控制器
 **/
@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * 获取月视图数据（返回日期单元格数组）
     */
    @GetMapping("/month")
    public Result<List<CalendarDayResponse>> getMonthViewData(@RequestParam("yearMonth") String yearMonthStr) {
        // 解析字符串为LocalDate（假设格式为yyyy-MM）
        LocalDate yearMonth = LocalDateTimeUtil.parse(yearMonthStr + "-01", "yyyy-MM-dd").toLocalDate();
        List<CalendarDayResponse> responses = calendarService.getMonthViewData(yearMonth);
        return Result.success(responses);
    }

    /**
     * 获取日视图数据
     */
    @GetMapping("/day")
    public Result<List<TodoResponse>> getDayViewData(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDateTimeUtil.parseDate(dateStr);
        List<TodoResponse> responses = calendarService.getDayViewData(date);
        return Result.success(responses);
    }

    /**
     * 获取周视图数据
     */
    @GetMapping("/week")
    public Result<List<TodoResponse>> getWeekViewData(@RequestParam("weekStartDate") String weekStartDateStr) {
        LocalDate weekStartDate = LocalDateTimeUtil.parseDate(weekStartDateStr);
        List<TodoResponse> responses = calendarService.getWeekViewData(weekStartDate);
        return Result.success(responses);
    }
}

