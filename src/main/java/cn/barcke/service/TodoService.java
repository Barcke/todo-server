package cn.barcke.service;

import cn.barcke.dto.todo.TodoCreateRequest;
import cn.barcke.dto.todo.TodoResponse;
import cn.barcke.dto.todo.TodoUpdateRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 服务接口
 **/
public interface TodoService {

    /**
     * 创建 Todo
     */
    TodoResponse createTodo(TodoCreateRequest request);

    /**
     * 更新 Todo
     */
    TodoResponse updateTodo(String id, TodoUpdateRequest request);

    /**
     * 删除 Todo（软删除）
     */
    void deleteTodo(String id);

    /**
     * 标记完成
     */
    TodoResponse completeTodo(String id);

    /**
     * 根据ID查询 Todo
     */
    TodoResponse getTodoById(String id);

    /**
     * 按日期查询 Todo 列表
     */
    List<TodoResponse> getTodosByDate(LocalDate date);

    /**
     * 按日期范围查询 Todo 列表
     */
    List<TodoResponse> getTodosByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 按状态查询 Todo 列表
     */
    List<TodoResponse> getTodosByStatus(String status);

    /**
     * 按日期范围和状态查询 Todo 列表
     */
    List<TodoResponse> getTodosByDateRangeAndStatus(LocalDate startDate, LocalDate endDate, String status);
}

