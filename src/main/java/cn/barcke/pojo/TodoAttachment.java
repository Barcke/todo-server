package cn.barcke.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TodoAttachment
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Todo 附件实体类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "todo_attachment")
public class TodoAttachment {

    /**
     * 附件ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "attachment_id", length = 64)
    private String attachmentId;

    /**
     * Todo ID
     */
    @Column(name = "todo_id", length = 64)
    private String todoId;

    /**
     * 附件类型（image/voice/file）
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /**
     * 文件访问URL
     */
    @Column(name = "url", nullable = false, length = 500)
    private String url;

    /**
     * 预览URL（图片用）
     */
    @Column(name = "preview_url", length = 500)
    private String previewUrl;

    /**
     * 文件名
     */
    @Column(name = "file_name", nullable = false, length = 200)
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 文件存储路径
     */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

