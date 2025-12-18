package cn.barcke.service.impl;

import cn.barcke.common.BarckeContext;
import cn.barcke.common.CommonException;
import cn.barcke.dao.TodoTypeRepository;
import cn.barcke.dto.todo.TodoTypeRequest;
import cn.barcke.dto.todo.TodoTypeResponse;
import cn.barcke.pojo.TodoType;
import cn.barcke.service.TodoTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTypeServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 类型服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class TodoTypeServiceImpl implements TodoTypeService {

    private final TodoTypeRepository todoTypeRepository;

    @Override
    @Transactional
    public TodoTypeResponse createType(TodoTypeRequest request) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoType todoType = TodoType.builder()
                .userId(userId)
                .typeName(request.getTypeName())
                .icon(request.getIcon())
                .color(request.getColor())
                .delFlag(false)
                .build();

        todoType = todoTypeRepository.save(todoType);

        return convertToResponse(todoType);
    }

    @Override
    @Transactional
    public TodoTypeResponse updateType(String typeId, TodoTypeRequest request) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoType todoType = todoTypeRepository.findByUserIdAndTypeIdAndDelFlagFalse(userId, typeId);
        if (todoType == null) {
            throw CommonException.toast("类型不存在");
        }

        if (request.getTypeName() != null) {
            todoType.setTypeName(request.getTypeName());
        }
        if (request.getIcon() != null) {
            todoType.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            todoType.setColor(request.getColor());
        }

        todoType = todoTypeRepository.save(todoType);

        return convertToResponse(todoType);
    }

    @Override
    @Transactional
    public void deleteType(String typeId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoType todoType = todoTypeRepository.findByUserIdAndTypeIdAndDelFlagFalse(userId, typeId);
        if (todoType == null) {
            throw CommonException.toast("类型不存在");
        }

        todoType.setDelFlag(true);
        todoTypeRepository.save(todoType);
    }

    @Override
    public List<TodoTypeResponse> getAllTypes() {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        List<TodoType> types = todoTypeRepository.findByUserIdAndDelFlagFalse(userId);
        return types.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TodoTypeResponse getTypeById(String typeId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoType todoType = todoTypeRepository.findByUserIdAndTypeIdAndDelFlagFalse(userId, typeId);
        if (todoType == null) {
            throw CommonException.toast("类型不存在");
        }

        return convertToResponse(todoType);
    }

    private TodoTypeResponse convertToResponse(TodoType todoType) {
        return TodoTypeResponse.builder()
                .typeId(todoType.getTypeId())
                .typeName(todoType.getTypeName())
                .icon(todoType.getIcon())
                .color(todoType.getColor())
                .createdAt(todoType.getCreatedAt())
                .build();
    }
}

