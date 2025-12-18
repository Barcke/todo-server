package com.barcke.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className FileUploadResponse
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 文件上传响应DTO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {
    /**
     * 文件ID（attachmentId）
     */
    private String fileId;

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（image/voice/file）
     */
    private String type;
}

