package cn.barcke.service.impl;

import cn.barcke.common.BarckeContext;
import cn.barcke.common.CommonException;
import cn.barcke.dao.TodoRepository;
import cn.barcke.dto.todo.CalendarDayResponse;
import cn.barcke.dto.todo.TodoResponse;
import cn.barcke.pojo.Todo;
import cn.barcke.service.CalendarService;
import cn.barcke.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className CalendarServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 日历服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final TodoRepository todoRepository;
    private final TodoService todoService;

    @Override
    public List<CalendarDayResponse> getMonthViewData(LocalDate yearMonth) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        // 计算月份的开始和结束日期
        LocalDate startDate = yearMonth.withDayOfMonth(1);
        LocalDate endDate = yearMonth.withDayOfMonth(yearMonth.lengthOfMonth());

        // 获取统计数据
        List<Object[]> statistics = todoRepository.getMonthViewStatistics(userId, startDate, endDate);

        // 获取所有日期范围内的 Todo
        List<Todo> todos = todoRepository.findByUserIdAndDateBetweenAndDelFlagFalseOrderByDateAscTimeAsc(userId, startDate, endDate);

        // 构建日期单元格响应
        List<CalendarDayResponse> responses = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            CalendarDayResponse dayResponse = buildDayResponse(currentDate, todos, statistics);
            responses.add(dayResponse);
            currentDate = currentDate.plusDays(1);
        }

        return responses;
    }

    @Override
    public List<TodoResponse> getDayViewData(LocalDate date) {
        return todoService.getTodosByDate(date);
    }

    @Override
    public List<TodoResponse> getWeekViewData(LocalDate weekStartDate) {
        // 计算周的开始和结束日期（周一到周日）
        LocalDate startDate = weekStartDate;
        if (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            // 如果不是周一，找到最近的周一
            int daysToSubtract = startDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
            startDate = startDate.minusDays(daysToSubtract);
        }
        LocalDate endDate = startDate.plusDays(6);

        return todoService.getTodosByDateRange(startDate, endDate);
    }

    /**
     * 构建日期单元格响应
     */
    private CalendarDayResponse buildDayResponse(LocalDate date, List<Todo> todos, List<Object[]> statistics) {
        // 过滤当天的 Todo
        List<Todo> dayTodos = todos.stream()
                .filter(todo -> todo.getDate().equals(date))
                .collect(Collectors.toList());

        int totalCount = dayTodos.size();
        long completedCount = dayTodos.stream()
                .filter(todo -> "completed".equals(todo.getStatus()))
                .count();

        // 计算完成比例
        double completionRate = totalCount > 0 ? (double) completedCount / totalCount : 0.0;

        // 获取已完成 Todo 的类型图标
        List<String> completedTypeIcons = todoRepository.getCompletedTypeIcons(
                BarckeContext.getUserId(), date);

        // 检查是否存在未完成的 Todo
        boolean hasUnfinished = dayTodos.stream()
                .anyMatch(todo -> !"completed".equals(todo.getStatus()));

        return CalendarDayResponse.builder()
                .date(date)
                .totalCount(totalCount)
                .completedCount((int) completedCount)
                .completionRate(completionRate)
                .completedTypeIcons(completedTypeIcons)
                .hasUnfinished(hasUnfinished)
                .build();
    }
}

