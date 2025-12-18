package cn.barcke.service.impl;

import cn.barcke.common.BarckeContext;
import cn.barcke.common.CommonException;
import cn.barcke.dao.TodoRepository;
import cn.barcke.dao.TodoTypeRepository;
import cn.barcke.dto.todo.MonthStatisticsResponse;
import cn.barcke.dto.todo.TypeStatisticsResponse;
import cn.barcke.pojo.Todo;
import cn.barcke.pojo.TodoType;
import cn.barcke.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className StatisticsServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 统计服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final TodoRepository todoRepository;
    private final TodoTypeRepository todoTypeRepository;

    @Override
    public MonthStatisticsResponse getCompletionStatsByMonth(String yearMonth) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        // 解析年月
        LocalDate month = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());

        // 查询该月的所有 Todo
        List<Todo> todos = todoRepository.findByUserIdAndDateBetweenAndDelFlagFalseOrderByDateAscTimeAsc(userId, startDate, endDate);

        int totalCount = todos.size();
        long completedCount = todos.stream()
                .filter(todo -> "completed".equals(todo.getStatus()))
                .count();

        double completionRate = totalCount > 0 ? (double) completedCount / totalCount : 0.0;

        return MonthStatisticsResponse.builder()
                .month(yearMonth)
                .totalCount(totalCount)
                .completedCount((int) completedCount)
                .completionRate(completionRate)
                .build();
    }

    @Override
    public List<TypeStatisticsResponse> getTypeStatsByMonth(String yearMonth) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        // 解析年月
        LocalDate month = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());

        // 查询该月所有 Todo（包括已完成和未完成）
        List<Todo> allTodos = todoRepository.findByUserIdAndDateBetweenAndDelFlagFalseOrderByDateAscTimeAsc(userId, startDate, endDate);

        // 按类型统计已完成和未完成的数量
        Map<String, Integer> completedCountMap = new HashMap<>();
        Map<String, Integer> pendingCountMap = new HashMap<>();
        
        for (Todo todo : allTodos) {
            if (todo.getTypeId() != null) {
                if ("completed".equals(todo.getStatus())) {
                    completedCountMap.put(todo.getTypeId(), 
                            completedCountMap.getOrDefault(todo.getTypeId(), 0) + 1);
                } else {
                    pendingCountMap.put(todo.getTypeId(), 
                            pendingCountMap.getOrDefault(todo.getTypeId(), 0) + 1);
                }
            }
        }

        // 获取所有类型信息
        List<TodoType> types = todoTypeRepository.findByUserIdAndDelFlagFalse(userId);
        Map<String, TodoType> typeMap = types.stream()
                .collect(Collectors.toMap(TodoType::getTypeId, type -> type));

        // 合并所有类型ID（包括有已完成和未完成的）
        Map<String, Boolean> allTypeIds = new HashMap<>();
        completedCountMap.keySet().forEach(typeId -> allTypeIds.put(typeId, true));
        pendingCountMap.keySet().forEach(typeId -> allTypeIds.put(typeId, true));

        // 构建响应
        return allTypeIds.keySet().stream()
                .map(typeId -> {
                    TodoType type = typeMap.get(typeId);
                    return TypeStatisticsResponse.builder()
                            .month(yearMonth)
                            .typeId(typeId)
                            .typeName(type != null ? type.getTypeName() : null)
                            .typeIcon(type != null ? type.getIcon() : null)
                            .completedCount(completedCountMap.getOrDefault(typeId, 0))
                            .pendingCount(pendingCountMap.getOrDefault(typeId, 0))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Double getCompletionRate(String yearMonth) {
        MonthStatisticsResponse stats = getCompletionStatsByMonth(yearMonth);
        return stats.getCompletionRate();
    }
}

