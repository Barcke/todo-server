package cn.barcke.service.impl;

import cn.barcke.common.BarckeContext;
import cn.barcke.common.CommonException;
import cn.barcke.dao.TodoAttachmentRepository;
import cn.barcke.dao.TodoRepository;
import cn.barcke.dao.TodoTypeRepository;
import cn.barcke.dto.todo.AttachmentResponse;
import cn.barcke.dto.todo.RepeatRule;
import cn.barcke.dto.todo.TodoCreateRequest;
import cn.barcke.dto.todo.TodoResponse;
import cn.barcke.dto.todo.TodoUpdateRequest;
import cn.barcke.pojo.Todo;
import cn.barcke.pojo.TodoAttachment;
import cn.barcke.pojo.TodoType;
import cn.barcke.service.TodoService;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // 构建 Todo 实体
        Todo todo = Todo.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .date(request.getDate())
                .time(request.getTime())
                .typeId(request.getTypeId())
                .status("pending")
                .source("normal")
                .repeatType(request.getRepeatType() != null ? request.getRepeatType() : "none")
                .repeatRule(request.getRepeatRule() != null ? JSONUtil.toJsonStr(request.getRepeatRule()) : null)
                .delFlag(false)
                .build();

        todo = todoRepository.save(todo);

        // 关联附件
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            associateAttachments(todo.getId(), request.getAttachmentIds(), userId);
        }

        return convertToResponse(todo);
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

