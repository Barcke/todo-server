package cn.barcke.controller;

import cn.barcke.common.Result;
import cn.barcke.dto.todo.TodoCreateRequest;
import cn.barcke.dto.todo.TodoResponse;
import cn.barcke.dto.todo.TodoUpdateRequest;
import cn.barcke.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 控制器
 **/
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * 创建 Todo
     */
    @PostMapping
    public Result<TodoResponse> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        TodoResponse response = todoService.createTodo(request);
        return Result.success("创建成功", response);
    }

    /**
     * 更新 Todo
     */
    @PutMapping("/{id}")
    public Result<TodoResponse> updateTodo(@PathVariable String id, @RequestBody TodoUpdateRequest request) {
        TodoResponse response = todoService.updateTodo(id, request);
        return Result.success("更新成功", response);
    }

    /**
     * 删除 Todo
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteTodo(@PathVariable String id) {
        todoService.deleteTodo(id);
        return Result.success("删除成功");
    }

    /**
     * 标记完成
     */
    @PostMapping("/{id}/complete")
    public Result<TodoResponse> completeTodo(@PathVariable String id) {
        TodoResponse response = todoService.completeTodo(id);
        return Result.success("标记完成成功", response);
    }

    /**
     * 获取 Todo 详情
     */
    @GetMapping("/{id}")
    public Result<TodoResponse> getTodoById(@PathVariable String id) {
        TodoResponse response = todoService.getTodoById(id);
        return Result.success(response);
    }

    /**
     * 查询 Todo 列表
     */
    @GetMapping
    public Result<List<TodoResponse>> getTodos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {
        List<TodoResponse> responses;
        if (startDate != null && endDate != null) {
            if (status != null) {
                responses = todoService.getTodosByDateRangeAndStatus(startDate, endDate, status);
            } else {
                responses = todoService.getTodosByDateRange(startDate, endDate);
            }
        } else if (status != null) {
            responses = todoService.getTodosByStatus(status);
        } else {
            // 默认查询当天的
            responses = todoService.getTodosByDate(LocalDate.now());
        }
        return Result.success(responses);
    }
}

