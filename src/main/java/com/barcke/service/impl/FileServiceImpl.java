package com.barcke.service.impl;

import com.barcke.common.BarckeContext;
import com.barcke.common.CommonException;
import com.barcke.dao.TodoAttachmentRepository;
import com.barcke.dao.TodoRepository;
import com.barcke.dto.todo.FileUploadResponse;
import com.barcke.pojo.Todo;
import com.barcke.pojo.TodoAttachment;
import com.barcke.service.FileService;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className FileServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 文件服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final TodoAttachmentRepository todoAttachmentRepository;
    private final TodoRepository todoRepository;

    private static final String BASE_UPLOAD_PATH = "/tmp/calendar-uploads";
    private static final String FILE_ACCESS_URL_PREFIX = "/api/files/";

    @Override
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, String todoId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        if (file == null || file.isEmpty()) {
            throw CommonException.toast("文件不能为空");
        }

        try {
            // 验证 todoId（如果提供）
            if (todoId != null) {
                Todo todo = todoRepository.findByUserIdAndIdAndDelFlagFalse(userId, todoId);
                if (todo == null) {
                    throw CommonException.toast("Todo 不存在");
                }
            }

            // 生成文件存储路径
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fileName = file.getOriginalFilename();
            String extension = "";
            if (fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }

            String fileId = IdUtil.simpleUUID();
            String storedFileName = fileId + extension;
            String userDir = BASE_UPLOAD_PATH + "/" + userId + "/" + dateStr;
            Path uploadPath = Paths.get(userDir);

            // 创建目录
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件
            Path filePath = uploadPath.resolve(storedFileName);
            file.transferTo(filePath.toFile());

            // 确定文件类型
            String fileType = determineFileType(fileName, file.getContentType());

            // 构建文件访问URL
            String fileUrl = FILE_ACCESS_URL_PREFIX + fileId;

            // 保存附件信息
            TodoAttachment attachment = TodoAttachment.builder()
                    .attachmentId(fileId)
                    .todoId(todoId)
                    .type(fileType)
                    .url(fileUrl)
                    .previewUrl(fileType.equals("image") ? fileUrl : null)
                    .fileName(fileName)
                    .fileSize(file.getSize())
                    .filePath(filePath.toString())
                    .build();

            attachment = todoAttachmentRepository.save(attachment);

            return FileUploadResponse.builder()
                    .fileId(attachment.getAttachmentId())
                    .url(attachment.getUrl())
                    .fileName(attachment.getFileName())
                    .fileSize(attachment.getFileSize())
                    .type(attachment.getType())
                    .build();

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw CommonException.toast("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Resource getFileResource(String fileId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        // 查询附件信息（带安全校验）
        TodoAttachment attachment = todoAttachmentRepository.findByAttachmentIdAndUserId(fileId, userId);
        if (attachment == null) {
            throw CommonException.toast("文件不存在或无权限访问");
        }

        // 读取文件
        File file = new File(attachment.getFilePath());
        if (!file.exists()) {
            throw CommonException.toast("文件不存在");
        }

        return new FileSystemResource(file);
    }

    @Override
    public TodoAttachment getFileInfo(String fileId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoAttachment attachment = todoAttachmentRepository.findByAttachmentIdAndUserId(fileId, userId);
        if (attachment == null) {
            throw CommonException.toast("文件不存在或无权限访问");
        }

        return attachment;
    }

    @Override
    @Transactional
    public void deleteFile(String fileId) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw CommonException.toast("用户未登录");
        }

        TodoAttachment attachment = todoAttachmentRepository.findByAttachmentIdAndUserId(fileId, userId);
        if (attachment == null) {
            throw CommonException.toast("文件不存在或无权限访问");
        }

        // 删除物理文件
        try {
            File file = new File(attachment.getFilePath());
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        } catch (IOException e) {
            log.warn("删除物理文件失败: {}", attachment.getFilePath(), e);
        }

        // 删除数据库记录
        todoAttachmentRepository.delete(attachment);
    }

    /**
     * 确定文件类型
     */
    private String determineFileType(String fileName, String contentType) {
        if (fileName == null) {
            return "file";
        }

        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") ||
            lowerFileName.endsWith(".png") || lowerFileName.endsWith(".gif") ||
            lowerFileName.endsWith(".heic") || lowerFileName.endsWith(".webp")) {
            return "image";
        }

        if (lowerFileName.endsWith(".mp3") || lowerFileName.endsWith(".wav") ||
            lowerFileName.endsWith(".amr") || lowerFileName.endsWith(".m4a")) {
            return "voice";
        }

        return "file";
    }
}

