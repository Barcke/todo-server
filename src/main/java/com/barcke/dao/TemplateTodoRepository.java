package com.barcke.dao;

import com.barcke.pojo.TemplateTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TemplateTodoRepository
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 模板项数据访问层
 **/
@Repository
public interface TemplateTodoRepository extends JpaRepository<TemplateTodo, String> {

    /**
     * 根据模板ID查询所有模板项，按排序字段排序
     */
    List<TemplateTodo> findByTemplateIdOrderBySortOrderAsc(String templateId);

    /**
     * 根据模板ID和ID查询
     */
    TemplateTodo findByTemplateIdAndId(String templateId, String id);

    /**
     * 根据模板ID删除所有模板项
     */
    void deleteByTemplateId(String templateId);
}

