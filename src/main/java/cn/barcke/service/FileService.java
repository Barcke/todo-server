package cn.barcke.service;

import cn.barcke.dto.todo.FileUploadResponse;
import cn.barcke.pojo.TodoAttachment;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className FileService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 文件服务接口
 **/
public interface FileService {

    /**
     * 上传文件
     */
    FileUploadResponse uploadFile(MultipartFile file, String todoId);

    /**
     * 获取文件资源（用于读取）
     */
    Resource getFileResource(String fileId);

    /**
     * 获取文件信息
     */
    TodoAttachment getFileInfo(String fileId);

    /**
     * 删除文件
     */
    void deleteFile(String fileId);
}

