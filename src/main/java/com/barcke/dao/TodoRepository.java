package com.barcke.dao;

import com.barcke.pojo.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoRepository
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 数据访问层
 **/
@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {

    /**
     * 根据用户ID和日期查询
     */
    List<Todo> findByUserIdAndDateAndDelFlagFalseOrderByTimeAsc(String userId, LocalDate date);

    /**
     * 根据用户ID和日期范围查询
     */
    List<Todo> findByUserIdAndDateBetweenAndDelFlagFalseOrderByDateAscTimeAsc(String userId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据用户ID和状态查询
     */
    List<Todo> findByUserIdAndStatusAndDelFlagFalseOrderByDateAscTimeAsc(String userId, String status);

    /**
     * 根据用户ID和日期范围及状态查询
     */
    List<Todo> findByUserIdAndDateBetweenAndStatusAndDelFlagFalseOrderByDateAscTimeAsc(String userId, LocalDate startDate, LocalDate endDate, String status);

    /**
     * 根据用户ID查询
     */
    List<Todo> findByUserIdAndDelFlagFalseOrderByDateAscTimeAsc(String userId);

    /**
     * 根据用户ID和ID查询
     */
    Todo findByUserIdAndIdAndDelFlagFalse(String userId, String id);

    /**
     * 月视图数据聚合查询
     */
    @Query("SELECT t.date as date, COUNT(t) as totalCount, " +
           "SUM(CASE WHEN t.status = 'completed' THEN 1 ELSE 0 END) as completedCount " +
           "FROM Todo t " +
           "WHERE t.userId = :userId AND t.date BETWEEN :startDate AND :endDate AND t.delFlag = false " +
           "GROUP BY t.date")
    List<Object[]> getMonthViewStatistics(@Param("userId") String userId, 
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);

    /**
     * 查询已完成Todo的类型图标
     */
    @Query("SELECT DISTINCT tt.icon FROM Todo t " +
           "LEFT JOIN TodoType tt ON t.typeId = tt.typeId " +
           "WHERE t.userId = :userId AND t.date = :date AND t.status = 'completed' AND t.delFlag = false AND tt.icon IS NOT NULL")
    List<String> getCompletedTypeIcons(@Param("userId") String userId, @Param("date") LocalDate date);
}

