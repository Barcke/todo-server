package cn.barcke.controller;

import cn.barcke.common.Result;
import cn.barcke.dto.todo.TemplateCreateRequest;
import cn.barcke.dto.todo.TemplateResponse;
import cn.barcke.dto.todo.TemplateTodoResponse;
import cn.barcke.dto.todo.TodoResponse;
import cn.barcke.service.TodoTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTemplateController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 模板控制器
 **/
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TodoTemplateController {

    private final TodoTemplateService todoTemplateService;

    /**
     * 创建模板
     */
    @PostMapping
    public Result<TemplateResponse> createTemplate(@Valid @RequestBody TemplateCreateRequest request) {
        TemplateResponse response = todoTemplateService.createTemplate(request);
        return Result.success("创建成功", response);
    }

    /**
     * 更新模板
     */
    @PutMapping("/{id}")
    public Result<TemplateResponse> updateTemplate(@PathVariable String id, @RequestBody TemplateCreateRequest request) {
        TemplateResponse response = todoTemplateService.updateTemplate(id, request);
        return Result.success("更新成功", response);
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteTemplate(@PathVariable String id) {
        todoTemplateService.deleteTemplate(id);
        return Result.success("删除成功");
    }

    /**
     * 获取模板列表
     */
    @GetMapping
    public Result<List<TemplateResponse>> getTemplateList() {
        List<TemplateResponse> responses = todoTemplateService.getTemplateList();
        return Result.success(responses);
    }

    /**
     * 获取模板详情（含所有项）
     */
    @GetMapping("/{id}")
    public Result<TemplateResponse> getTemplateWithTodos(@PathVariable String id) {
        TemplateResponse response = todoTemplateService.getTemplateWithTodos(id);
        return Result.success(response);
    }

    /**
     * 添加模板项
     */
    @PostMapping("/{id}/todos")
    public Result<TemplateTodoResponse> addTemplateTodo(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam(required = false) String typeId) {
        TemplateTodoResponse response = todoTemplateService.addTemplateTodo(id, title, typeId);
        return Result.success("添加成功", response);
    }

    /**
     * 更新模板项
     */
    @PutMapping("/{id}/todos/{todoId}")
    public Result<TemplateTodoResponse> updateTemplateTodo(
            @PathVariable String id,
            @PathVariable String todoId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String typeId) {
        TemplateTodoResponse response = todoTemplateService.updateTemplateTodo(id, todoId, title, typeId);
        return Result.success("更新成功", response);
    }

    /**
     * 删除模板项
     */
    @DeleteMapping("/{id}/todos/{todoId}")
    public Result<String> deleteTemplateTodo(@PathVariable String id, @PathVariable String todoId) {
        todoTemplateService.deleteTemplateTodo(id, todoId);
        return Result.success("删除成功");
    }

    /**
     * 应用模板生成 Todo
     */
    @PostMapping("/{id}/apply")
    public Result<List<TodoResponse>> applyTemplate(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        List<TodoResponse> responses = todoTemplateService.applyTemplate(id, date, time);
        return Result.success("应用模板成功", responses);
    }
}

