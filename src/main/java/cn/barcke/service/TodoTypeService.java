package cn.barcke.service;

import cn.barcke.dto.todo.TodoTypeRequest;
import cn.barcke.dto.todo.TodoTypeResponse;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTypeService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 类型服务接口
 **/
public interface TodoTypeService {

    /**
     * 创建类型
     */
    TodoTypeResponse createType(TodoTypeRequest request);

    /**
     * 更新类型
     */
    TodoTypeResponse updateType(String typeId, TodoTypeRequest request);

    /**
     * 删除类型（软删除）
     */
    void deleteType(String typeId);

    /**
     * 获取用户所有类型
     */
    List<TodoTypeResponse> getAllTypes();

    /**
     * 根据ID查询类型
     */
    TodoTypeResponse getTypeById(String typeId);
}

