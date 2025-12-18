package cn.barcke.dao;

import cn.barcke.pojo.TodoAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoAttachmentRepository
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 附件数据访问层
 **/
@Repository
public interface TodoAttachmentRepository extends JpaRepository<TodoAttachment, String> {

    /**
     * 根据Todo ID查询所有附件
     */
    List<TodoAttachment> findByTodoId(String todoId);

    /**
     * 根据附件ID查询（用于文件读取时的安全校验）
     */
    @Query("SELECT ta FROM TodoAttachment ta " +
           "LEFT JOIN Todo t ON ta.todoId = t.id " +
           "WHERE ta.attachmentId = :attachmentId AND t.userId = :userId")
    TodoAttachment findByAttachmentIdAndUserId(@Param("attachmentId") String attachmentId, @Param("userId") String userId);
}

