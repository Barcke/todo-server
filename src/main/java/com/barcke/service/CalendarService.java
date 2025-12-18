package com.barcke.service;

import com.barcke.dto.todo.CalendarDayResponse;
import com.barcke.dto.todo.TodoResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className CalendarService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 日历服务接口
 **/
public interface CalendarService {

    /**
     * 获取月视图数据（返回日期单元格数组）
     */
    List<CalendarDayResponse> getMonthViewData(LocalDate yearMonth);

    /**
     * 获取日视图数据
     */
    List<TodoResponse> getDayViewData(LocalDate date);

    /**
     * 获取周视图数据
     */
    List<TodoResponse> getWeekViewData(LocalDate weekStartDate);
}

