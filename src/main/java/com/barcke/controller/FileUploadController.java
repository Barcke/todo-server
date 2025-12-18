package com.barcke.controller;

import com.barcke.common.Result;
import com.barcke.dto.todo.FileUploadResponse;
import com.barcke.pojo.TodoAttachment;
import com.barcke.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className FileUploadController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 文件上传控制器
 **/
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String todoId) {
        FileUploadResponse response = fileService.uploadFile(file, todoId);
        return Result.success("上传成功", response);
    }

    /**
     * 文件读取（返回文件流）
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileId) {
        Resource resource = fileService.getFileResource(fileId);
        TodoAttachment attachment = fileService.getFileInfo(fileId);

        // 确定 Content-Type
        MediaType mediaType = determineMediaType(attachment.getType(), attachment.getFileName());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    /**
     * 确定媒体类型
     */
    private MediaType determineMediaType(String fileType, String fileName) {
        if ("image".equals(fileType)) {
            if (fileName != null) {
                String lowerFileName = fileName.toLowerCase();
                if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
                    return MediaType.IMAGE_JPEG;
                } else if (lowerFileName.endsWith(".png")) {
                    return MediaType.IMAGE_PNG;
                } else if (lowerFileName.endsWith(".gif")) {
                    return MediaType.IMAGE_GIF;
                } else if (lowerFileName.endsWith(".heic")) {
                    return MediaType.parseMediaType("image/heic");
                }
            }
            return MediaType.IMAGE_JPEG;
        } else if ("voice".equals(fileType)) {
            if (fileName != null) {
                String lowerFileName = fileName.toLowerCase();
                if (lowerFileName.endsWith(".mp3")) {
                    return MediaType.parseMediaType("audio/mpeg");
                } else if (lowerFileName.endsWith(".wav")) {
                    return MediaType.parseMediaType("audio/wav");
                } else if (lowerFileName.endsWith(".amr")) {
                    return MediaType.parseMediaType("audio/amr");
                }
            }
            return MediaType.parseMediaType("audio/mpeg");
        } else {
            if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                return MediaType.APPLICATION_PDF;
            }
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}

