package com.barcke.dao;

import com.barcke.pojo.TodoTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoTemplateRepository
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 模板数据访问层
 **/
@Repository
public interface TodoTemplateRepository extends JpaRepository<TodoTemplate, String> {

    /**
     * 根据用户ID查询所有未删除的模板
     */
    List<TodoTemplate> findByUserIdAndDelFlagFalseOrderByCreatedAtDesc(String userId);

    /**
     * 根据用户ID和模板ID查询
     */
    TodoTemplate findByUserIdAndTemplateIdAndDelFlagFalse(String userId, String templateId);
}

