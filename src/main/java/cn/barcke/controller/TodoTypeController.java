package cn.barcke.controller;

import cn.barcke.common.Result;
import cn.barcke.dto.todo.TodoTypeRequest;
import cn.barcke.dto.todo.TodoTypeResponse;
import cn.barcke.service.TodoTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTypeController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 类型控制器
 **/
@RestController
@RequestMapping("/todo-types")
@RequiredArgsConstructor
public class TodoTypeController {

    private final TodoTypeService todoTypeService;

    /**
     * 创建类型
     */
    @PostMapping
    public Result<TodoTypeResponse> createType(@Valid @RequestBody TodoTypeRequest request) {
        TodoTypeResponse response = todoTypeService.createType(request);
        return Result.success("创建成功", response);
    }

    /**
     * 更新类型
     */
    @PutMapping("/{id}")
    public Result<TodoTypeResponse> updateType(@PathVariable String id, @RequestBody TodoTypeRequest request) {
        TodoTypeResponse response = todoTypeService.updateType(id, request);
        return Result.success("更新成功", response);
    }

    /**
     * 删除类型
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteType(@PathVariable String id) {
        todoTypeService.deleteType(id);
        return Result.success("删除成功");
    }

    /**
     * 获取用户所有类型
     */
    @GetMapping
    public Result<List<TodoTypeResponse>> getAllTypes() {
        List<TodoTypeResponse> responses = todoTypeService.getAllTypes();
        return Result.success(responses);
    }

    /**
     * 根据ID查询类型
     */
    @GetMapping("/{id}")
    public Result<TodoTypeResponse> getTypeById(@PathVariable String id) {
        TodoTypeResponse response = todoTypeService.getTypeById(id);
        return Result.success(response);
    }
}

