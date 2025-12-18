package com.barcke.service.impl;

import com.barcke.common.BarckeContext;
import com.barcke.common.CommonException;
import com.barcke.dao.TemplateTodoRepository;
import com.barcke.dao.TodoAttachmentRepository;
import com.barcke.dao.TodoRepository;
import com.barcke.dao.TodoTemplateRepository;
import com.barcke.dao.TodoTypeRepository;
import com.barcke.dto.todo.AttachmentResponse;
import com.barcke.dto.todo.TemplateCreateRequest;
import com.barcke.dto.todo.TemplateResponse;
import com.barcke.dto.todo.TemplateTodoResponse;
import com.barcke.dto.todo.TodoResponse;
import com.barcke.pojo.TodoAttachment;
import com.barcke.pojo.TemplateTodo;
import com.barcke.pojo.Todo;
import com.barcke.pojo.TodoTemplate;
import com.barcke.pojo.TodoType;
import com.barcke.service.TodoTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTemplateServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 模板服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class TodoTemplateServiceImpl implements TodoTemplateService {

    private final TodoTemplateRepository todoTemplateRepository;
    private final TemplateTodoRepository templateTodoRepository;
    private final TodoTypeRepository todoTypeRepository;
    private final TodoRepository todoRepository;
    private final TodoAttachmentRepository todoAttachmentRepository;

    @Override
    @Transactional
    public TemplateResponse createTemplate(TemplateCreateRequest request) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = TodoTemplate.builder()
                .userId(userId)
                .templateName(request.getTemplateName())
                .description(request.getDescription())
                .delFlag(false)
                .build();

        template = todoTemplateRepository.save(template);

        return convertToResponse(template);
    }

    @Override
    @Transactional
    public TemplateResponse updateTemplate(String templateId, TemplateCreateRequest request) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = todoTemplateRepository.findByUserIdAndTemplateIdAndDelFlagFalse(userId, templateId);
        if (template == null) {
            throw CommonException.toast("模板不存在");
        }

        if (request.getTemplateName() != null) {
            template.setTemplateName(request.getTemplateName());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }

        template = todoTemplateRepository.save(template);

        return convertToResponse(template);
    }

    @Override
    @Transactional
    public void deleteTemplate(String templateId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = todoTemplateRepository.findByUserIdAndTemplateIdAndDelFlagFalse(userId, templateId);
        if (template == null) {
            throw CommonException.toast("模板不存在");
        }

        template.setDelFlag(true);
        todoTemplateRepository.save(template);
    }

    @Override
    public List<TemplateResponse> getTemplateList() {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        List<TodoTemplate> templates = todoTemplateRepository.findByUserIdAndDelFlagFalseOrderByCreatedAtDesc(userId);
        return templates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TemplateResponse getTemplateWithTodos(String templateId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = todoTemplateRepository.findByUserIdAndTemplateIdAndDelFlagFalse(userId, templateId);
        if (template == null) {
            throw CommonException.toast("模板不存在");
        }

        return convertToResponse(template);
    }

    @Override
    @Transactional
    public TemplateTodoResponse addTemplateTodo(String templateId, String title, String typeId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = todoTemplateRepository.findByUserIdAndTemplateIdAndDelFlagFalse(userId, templateId);
        if (template == null) {
            throw CommonException.toast("模板不存在");
        }

        // 获取当前最大排序值
        List<TemplateTodo> existingTodos = templateTodoRepository.findByTemplateIdOrderBySortOrderAsc(templateId);
        int maxSortOrder = existingTodos.stream()
                .mapToInt(TemplateTodo::getSortOrder)
                .max()
                .orElse(-1);

        TemplateTodo templateTodo = TemplateTodo.builder()
                .templateId(templateId)
                .title(title)
                .typeId(typeId)
                .sortOrder(maxSortOrder + 1)
                .build();

        templateTodo = templateTodoRepository.save(templateTodo);

        return convertTemplateTodoToResponse(templateTodo);
    }

    @Override
    @Transactional
    public TemplateTodoResponse updateTemplateTodo(String templateId, String todoId, String title, String typeId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = todoTemplateRepository.findByUserIdAndTemplateIdAndDelFlagFalse(userId, templateId);
        if (template == null) {
            throw CommonException.toast("模板不存在");
        }

        TemplateTodo templateTodo = templateTodoRepository.findByTemplateIdAndId(templateId, todoId);
        if (templateTodo == null) {
            throw CommonException.toast("模板项不存在");
        }

        if (title != null) {
            templateTodo.setTitle(title);
        }
        if (typeId != null) {
            templateTodo.setTypeId(typeId);
        }

        templateTodo = templateTodoRepository.save(templateTodo);

        return convertTemplateTodoToResponse(templateTodo);
    }

    @Override
    @Transactional
    public void deleteTemplateTodo(String templateId, String todoId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = todoTemplateRepository.findByUserIdAndTemplateIdAndDelFlagFalse(userId, templateId);
        if (template == null) {
            throw CommonException.toast("模板不存在");
        }

        TemplateTodo templateTodo = templateTodoRepository.findByTemplateIdAndId(templateId, todoId);
        if (templateTodo == null) {
            throw CommonException.toast("模板项不存在");
        }

        templateTodoRepository.delete(templateTodo);
    }

    @Override
    @Transactional
    public List<TodoResponse> applyTemplate(String templateId, LocalDate date, LocalTime time) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoTemplate template = todoTemplateRepository.findByUserIdAndTemplateIdAndDelFlagFalse(userId, templateId);
        if (template == null) {
            throw CommonException.toast("模板不存在");
        }

        List<TemplateTodo> templateTodos = templateTodoRepository.findByTemplateIdOrderBySortOrderAsc(templateId);
        List<TodoResponse> createdTodos = new ArrayList<>();

        // 直接使用 TodoRepository 创建 Todo，以便设置 templateId
        for (TemplateTodo templateTodo : templateTodos) {
            Todo todo = Todo.builder()
                    .userId(userId)
                    .title(templateTodo.getTitle())
                    .date(date)
                    .time(time)
                    .typeId(templateTodo.getTypeId())
                    .status("pending")
                    .source("template")
                    .templateId(templateId)
                    .repeatType("none")
                    .delFlag(false)
                    .build();

            todo = todoRepository.save(todo);
            createdTodos.add(convertTodoToResponse(todo));
        }

        return createdTodos;
    }

    private TodoResponse convertTodoToResponse(Todo todo) {
        TodoType todoType = null;
        if (todo.getTypeId() != null) {
            todoType = todoTypeRepository.findById(todo.getTypeId()).orElse(null);
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
                .attachments(attachmentResponses)
                .createdAt(todo.getCreatedAt())
                .completedAt(todo.getCompletedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }

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

    private TemplateResponse convertToResponse(TodoTemplate template) {
        List<TemplateTodo> templateTodos = templateTodoRepository.findByTemplateIdOrderBySortOrderAsc(template.getTemplateId());
        List<TemplateTodoResponse> todoResponses = templateTodos.stream()
                .map(this::convertTemplateTodoToResponse)
                .collect(Collectors.toList());

        return TemplateResponse.builder()
                .templateId(template.getTemplateId())
                .templateName(template.getTemplateName())
                .description(template.getDescription())
                .todos(todoResponses)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }

    private TemplateTodoResponse convertTemplateTodoToResponse(TemplateTodo templateTodo) {
        TodoType todoType = null;
        if (templateTodo.getTypeId() != null) {
            todoType = todoTypeRepository.findById(templateTodo.getTypeId()).orElse(null);
        }

        return TemplateTodoResponse.builder()
                .id(templateTodo.getId())
                .title(templateTodo.getTitle())
                .typeId(templateTodo.getTypeId())
                .typeName(todoType != null ? todoType.getTypeName() : null)
                .typeIcon(todoType != null ? todoType.getIcon() : null)
                .sortOrder(templateTodo.getSortOrder())
                .build();
    }
}

