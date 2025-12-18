package com.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className AttachmentResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 附件响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentResponse {
    /**
     * 附件ID
     */
    private String attachmentId;

    /**
     * 附件类型（image/voice/file）
     */
    private String type;

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 预览URL（图片用）
     */
    private String previewUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

