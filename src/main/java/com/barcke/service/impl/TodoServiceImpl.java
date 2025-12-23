package com.barcke.service.impl;

import com.barcke.common.BarckeContext;
import com.barcke.common.CommonException;
import com.barcke.dao.TodoAttachmentRepository;
import com.barcke.dao.TodoRepository;
import com.barcke.dao.TodoTypeRepository;
import com.barcke.dto.todo.AttachmentResponse;
import com.barcke.dto.todo.RepeatRule;
import com.barcke.dto.todo.TodoCreateRequest;
import com.barcke.dto.todo.TodoResponse;
import com.barcke.dto.todo.TodoUpdateRequest;
import com.barcke.pojo.Todo;
import com.barcke.pojo.TodoAttachment;
import com.barcke.pojo.TodoType;
import com.barcke.service.TodoService;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoTypeRepository todoTypeRepository;
    private final TodoAttachmentRepository todoAttachmentRepository;

    @Override
    @Transactional
    public TodoResponse createTodo(TodoCreateRequest request) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        String repeatType = request.getRepeatType() != null ? request.getRepeatType() : "none";
        RepeatRule repeatRule = request.getRepeatRule();

        // 构建第一个 Todo 实体
        Todo firstTodo = Todo.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .date(request.getDate())
                .time(request.getTime())
                .typeId(request.getTypeId())
                .status("pending")
                .source("normal")
                .repeatType(repeatType)
                .repeatRule(repeatRule != null ? JSONUtil.toJsonStr(repeatRule) : null)
                .delFlag(false)
                .build();

        firstTodo = todoRepository.save(firstTodo);

        // 关联附件到第一个 Todo
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            associateAttachments(firstTodo.getId(), request.getAttachmentIds(), userId);
        }

        // 如果设置了重复规则，生成重复的 Todo
        if (!"none".equals(repeatType) && repeatRule != null) {
            List<LocalDate> repeatDates = generateRepeatDates(request.getDate(), repeatType, repeatRule);
            if (!repeatDates.isEmpty()) {
                List<Todo> repeatTodos = new ArrayList<>();
                for (LocalDate date : repeatDates) {
                    Todo repeatTodo = Todo.builder()
                            .userId(userId)
                            .title(request.getTitle())
                            .description(request.getDescription())
                            .date(date)
                            .time(request.getTime())
                            .typeId(request.getTypeId())
                            .status("pending")
                            .source("normal")
                            .repeatType(repeatType)
                            .repeatRule(JSONUtil.toJsonStr(repeatRule))
                            .delFlag(false)
                            .build();
                    repeatTodos.add(repeatTodo);
                }
                // 批量保存重复的 Todo
                todoRepository.saveAll(repeatTodos);
            }
        }

        return convertToResponse(firstTodo);
    }

    @Override
    @Transactional
    public TodoResponse updateTodo(String id, TodoUpdateRequest request) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        Todo todo = todoRepository.findByUserIdAndIdAndDelFlagFalse(userId, id);
        if (todo == null) {
            throw CommonException.toast("Todo 不存在");
        }

        // 更新字段
        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getDate() != null) {
            todo.setDate(request.getDate());
        }
        if (request.getTime() != null) {
            todo.setTime(request.getTime());
        }
        if (request.getTypeId() != null) {
            todo.setTypeId(request.getTypeId());
        }
        if (request.getRepeatType() != null) {
            todo.setRepeatType(request.getRepeatType());
        }
        if (request.getRepeatRule() != null) {
            todo.setRepeatRule(JSONUtil.toJsonStr(request.getRepeatRule()));
        }

        todo = todoRepository.save(todo);

        // 更新附件关联
        if (request.getAttachmentIds() != null) {
            // 先取消现有关联
            List<TodoAttachment> existingAttachments = todoAttachmentRepository.findByTodoId(id);
            for (TodoAttachment attachment : existingAttachments) {
                attachment.setTodoId(null);
                todoAttachmentRepository.save(attachment);
            }
            // 关联新附件
            if (!request.getAttachmentIds().isEmpty()) {
                associateAttachments(todo.getId(), request.getAttachmentIds(), userId);
            }
        }

        return convertToResponse(todo);
    }

    @Override
    @Transactional
    public void deleteTodo(String id) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        Todo todo = todoRepository.findByUserIdAndIdAndDelFlagFalse(userId, id);
        if (todo == null) {
            throw CommonException.toast("Todo 不存在");
        }

        todo.setDelFlag(true);
        todoRepository.save(todo);
    }

    @Override
    @Transactional
    public TodoResponse completeTodo(String id) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        Todo todo = todoRepository.findByUserIdAndIdAndDelFlagFalse(userId, id);
        if (todo == null) {
            throw CommonException.toast("Todo 不存在");
        }

        todo.setStatus("completed");
        todo.setCompletedAt(LocalDateTime.now());
        todo = todoRepository.save(todo);

        return convertToResponse(todo);
    }

    @Override
    public TodoResponse getTodoById(String id) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        Todo todo = todoRepository.findByUserIdAndIdAndDelFlagFalse(userId, id);
        if (todo == null) {
            throw CommonException.toast("Todo 不存在");
        }

        return convertToResponse(todo);
    }

    @Override
    public List<TodoResponse> getTodosByDate(LocalDate date) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        List<Todo> todos = todoRepository.findByUserIdAndDateAndDelFlagFalseOrderByTimeAsc(userId, date);
        return todos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoResponse> getTodosByDateRange(LocalDate startDate, LocalDate endDate) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        List<Todo> todos = todoRepository.findByUserIdAndDateBetweenAndDelFlagFalseOrderByDateAscTimeAsc(userId, startDate, endDate);
        return todos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoResponse> getTodosByStatus(String status) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        List<Todo> todos = todoRepository.findByUserIdAndStatusAndDelFlagFalseOrderByDateAscTimeAsc(userId, status);
        return todos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoResponse> getTodosByDateRangeAndStatus(LocalDate startDate, LocalDate endDate, String status) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        List<Todo> todos = todoRepository.findByUserIdAndDateBetweenAndStatusAndDelFlagFalseOrderByDateAscTimeAsc(userId, startDate, endDate, status);
        return todos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据重复规则生成重复日期列表
     * 
     * @param startDate 开始日期
     * @param repeatType 重复类型（daily/weekly/monthly）
     * @param repeatRule 重复规则
     * @return 重复日期列表
     */
    private List<LocalDate> generateRepeatDates(LocalDate startDate, String repeatType, RepeatRule repeatRule) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate endDate = startDate.plusYears(1); // 默认生成未来一年的重复日期

        switch (repeatType) {
            case "daily":
                // 每天重复，生成未来365天
                LocalDate currentDate = startDate.plusDays(1);
                while (!currentDate.isAfter(endDate)) {
                    dates.add(currentDate);
                    currentDate = currentDate.plusDays(1);
                }
                break;

            case "weekly":
                // 按周重复，根据 days 数组（周几）生成
                if (repeatRule.getDays() != null && !repeatRule.getDays().isEmpty()) {
                    // 从开始日期之后的第一周开始，生成未来52周
                    for (int week = 1; week <= 52; week++) {
                        LocalDate weekStart = startDate.plusWeeks(week);
                        // 获取该周的第一天（周一）
                        LocalDate mondayOfWeek = weekStart.with(DayOfWeek.MONDAY);
                        for (Integer dayOfWeek : repeatRule.getDays()) {
                            // dayOfWeek: 1=周一, 7=周日
                            // Java DayOfWeek: 1=周一, 7=周日
                            DayOfWeek targetDayOfWeek = DayOfWeek.of(dayOfWeek);
                            LocalDate targetDate = mondayOfWeek.with(targetDayOfWeek);
                            // 确保日期在开始日期之后且在结束日期之前
                            if (targetDate.isAfter(startDate) && !targetDate.isAfter(endDate)) {
                                dates.add(targetDate);
                            }
                        }
                    }
                } else {
                    // 如果没有指定周几，默认每周同一天
                    LocalDate currentWeekDate = startDate.plusWeeks(1);
                    while (!currentWeekDate.isAfter(endDate)) {
                        dates.add(currentWeekDate);
                        currentWeekDate = currentWeekDate.plusWeeks(1);
                    }
                }
                break;

            case "monthly":
                // 按月重复，根据 days 数组（每月几号）生成
                if (repeatRule.getDays() != null && !repeatRule.getDays().isEmpty()) {
                    // 从开始日期之后的第一个月开始，生成未来12个月
                    for (int month = 1; month <= 12; month++) {
                        LocalDate targetMonth = startDate.plusMonths(month);
                        // 获取该月的第一天
                        LocalDate firstDayOfMonth = targetMonth.withDayOfMonth(1);
                        for (Integer dayOfMonth : repeatRule.getDays()) {
                            try {
                                LocalDate targetDate = firstDayOfMonth.withDayOfMonth(dayOfMonth);
                                // 确保日期在开始日期之后且在结束日期之前
                                if (targetDate.isAfter(startDate) && !targetDate.isAfter(endDate)) {
                                    dates.add(targetDate);
                                }
                            } catch (Exception e) {
                                // 如果该月没有这一天（如2月30日），跳过
                                log.debug("跳过无效日期: {}月{}日", firstDayOfMonth.getMonthValue(), dayOfMonth);
                            }
                        }
                    }
                } else {
                    // 如果没有指定日期，默认每月同一天
                    int dayOfMonth = startDate.getDayOfMonth();
                    LocalDate currentMonthDate = startDate.plusMonths(1);
                    while (!currentMonthDate.isAfter(endDate)) {
                        try {
                            LocalDate targetDate = currentMonthDate.withDayOfMonth(dayOfMonth);
                            dates.add(targetDate);
                            currentMonthDate = currentMonthDate.plusMonths(1);
                        } catch (Exception e) {
                            // 如果该月没有这一天，使用该月最后一天
                            LocalDate lastDayOfMonth = currentMonthDate.withDayOfMonth(currentMonthDate.lengthOfMonth());
                            dates.add(lastDayOfMonth);
                            currentMonthDate = currentMonthDate.plusMonths(1);
                        }
                    }
                }
                break;

            default:
                log.warn("未知的重复类型: {}", repeatType);
                break;
        }

        // 去重并排序
        return dates.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 关联附件到 Todo
     */
    private void associateAttachments(String todoId, List<String> attachmentIds, String userId) {
        for (String attachmentId : attachmentIds) {
            TodoAttachment attachment = todoAttachmentRepository.findById(attachmentId).orElse(null);
            if (attachment != null) {
                // 验证附件属于当前用户（通过关联的 Todo 验证）
                if (attachment.getTodoId() != null) {
                    Todo todo = todoRepository.findByUserIdAndIdAndDelFlagFalse(userId, attachment.getTodoId());
                    if (todo == null) {
                        continue; // 跳过不属于当前用户的附件
                    }
                }
                attachment.setTodoId(todoId);
                todoAttachmentRepository.save(attachment);
            }
        }
    }

    /**
     * 转换为响应DTO
     */
    private TodoResponse convertToResponse(Todo todo) {
        TodoType todoType = null;
        if (todo.getTypeId() != null) {
            todoType = todoTypeRepository.findById(todo.getTypeId()).orElse(null);
        }

        // 解析重复规则
        RepeatRule repeatRule = null;
        if (todo.getRepeatRule() != null) {
            try {
                repeatRule = JSONUtil.toBean(todo.getRepeatRule(), RepeatRule.class);
            } catch (Exception e) {
                log.warn("解析重复规则失败: {}", todo.getRepeatRule(), e);
            }
        }

        // 查询附件
        List<TodoAttachment> attachments = todoAttachmentRepository.findByTodoId(todo.getId());
        List<AttachmentResponse> attachmentResponses = attachments.stream()
                .map(this::convertAttachmentToResponse)
                .collect(Collectors.toList());

        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .date(todo.getDate())
                .time(todo.getTime())
                .status(todo.getStatus())
                .typeId(todo.getTypeId())
                .typeName(todoType != null ? todoType.getTypeName() : null)
                .typeIcon(todoType != null ? todoType.getIcon() : null)
                .source(todo.getSource())
                .templateId(todo.getTemplateId())
                .repeatType(todo.getRepeatType())
                .repeatRule(repeatRule)
                .attachments(attachmentResponses)
                .createdAt(todo.getCreatedAt())
                .completedAt(todo.getCompletedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }

    /**
     * 转换附件为响应DTO
     */
    private AttachmentResponse convertAttachmentToResponse(TodoAttachment attachment) {
        return AttachmentResponse.builder()
                .attachmentId(attachment.getAttachmentId())
                .type(attachment.getType())
                .url(attachment.getUrl())
                .previewUrl(attachment.getPreviewUrl())
                .fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}

