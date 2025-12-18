package cn.barcke.dao;

import cn.barcke.pojo.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTypeRepository
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 类型数据访问层
 **/
@Repository
public interface TodoTypeRepository extends JpaRepository<TodoType, String> {

    /**
     * 根据用户ID查询所有未删除的类型
     */
    List<TodoType> findByUserIdAndDelFlagFalse(String userId);

    /**
     * 根据用户ID和类型ID查询
     */
    TodoType findByUserIdAndTypeIdAndDelFlagFalse(String userId, String typeId);
}

