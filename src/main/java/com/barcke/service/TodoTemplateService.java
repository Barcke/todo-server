package com.barcke.service;

import com.barcke.dto.todo.TemplateCreateRequest;
import com.barcke.dto.todo.TemplateResponse;
import com.barcke.dto.todo.TemplateTodoResponse;
import com.barcke.dto.todo.TodoResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTemplateService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 模板服务接口
 **/
public interface TodoTemplateService {

    /**
     * 创建模板
     */
    TemplateResponse createTemplate(TemplateCreateRequest request);

    /**
     * 更新模板
     */
    TemplateResponse updateTemplate(String templateId, TemplateCreateRequest request);

    /**
     * 删除模板（软删除）
     */
    void deleteTemplate(String templateId);

    /**
     * 获取模板列表
     */
    List<TemplateResponse> getTemplateList();

    /**
     * 获取模板详情（含所有项）
     */
    TemplateResponse getTemplateWithTodos(String templateId);

    /**
     * 添加模板项
     */
    TemplateTodoResponse addTemplateTodo(String templateId, String title, String typeId);

    /**
     * 更新模板项
     */
    TemplateTodoResponse updateTemplateTodo(String templateId, String todoId, String title, String typeId);

    /**
     * 删除模板项
     */
    void deleteTemplateTodo(String templateId, String todoId);

    /**
     * 应用模板生成 Todo
     * @param templateId 模板ID
     * @param date 日期
     * @param time 时间（可选）
     * @return 生成的 Todo 列表
     */
    List<TodoResponse> applyTemplate(String templateId, LocalDate date, LocalTime time);
}

