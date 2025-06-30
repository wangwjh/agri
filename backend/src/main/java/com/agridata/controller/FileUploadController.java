package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.model.AgriculturalAsset;
import com.agridata.service.AgriculturalAssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * 文件上传控制器
 * 支持大文件上传，包括农业数据资产相关的文件
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    // 从配置文件读取上传路径，如果没有配置则使用默认路径
    @Value("${app.upload.path:./uploads}")
    private String uploadPath;
    
    // 支持的文件类型
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        // 数据文件
        "csv", "xlsx", "xls", "json", "xml", "txt",
        // 文档文件
        "pdf", "doc", "docx",
        // 图片文件
        "png", "jpg", "jpeg", "gif", "bmp", "tiff", "webp",
        // 压缩文件
        "zip", "rar", "7z", "tar", "gz",
        // 视频文件
        "mp4", "avi", "mov", "mkv", "wmv",
        // 音频文件
        "mp3", "wav", "ogg", "flac", "aac"
    );

    @Autowired
    private AgriculturalAssetService agriculturalAssetService;

    /**
     * 单文件上传
     */
    @PostMapping("/single")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadSingleFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "assetId", required = false) String assetId,
            Authentication authentication) {
        
        try {
            // 验证用户登录
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录"));
            }
            
            String username = authentication.getName();
            logger.info("用户 {} 上传文件: {}, 大小: {} bytes", username, file.getOriginalFilename(), file.getSize());
            
            // 验证文件
            Map<String, Object> validationResult = validateFile(file);
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.badRequest().body(ApiResponse.error(validationResult.get("message").toString()));
            }
            
            // 保存文件
            Map<String, Object> saveResult = saveFile(file, username, assetId);
            if (!(Boolean) saveResult.get("success")) {
                return ResponseEntity.internalServerError().body(ApiResponse.error(saveResult.get("message").toString()));
            }
            
            return ResponseEntity.ok(ApiResponse.success("文件上传成功", saveResult));
            
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("文件上传失败: " + e.getMessage()));
        }
    }

    /**
     * 多文件上传
     */
    @PostMapping("/multiple")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "assetId", required = false) String assetId,
            Authentication authentication) {
        
        try {
            // 验证用户登录
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录"));
            }
            
            String username = authentication.getName();
            logger.info("用户 {} 批量上传 {} 个文件", username, files.length);
            
            List<Map<String, Object>> uploadResults = new ArrayList<>();
            List<String> successFiles = new ArrayList<>();
            List<String> failedFiles = new ArrayList<>();
            
            for (MultipartFile file : files) {
                try {
                    // 验证单个文件
                    Map<String, Object> validationResult = validateFile(file);
                    if (!(Boolean) validationResult.get("valid")) {
                        failedFiles.add(file.getOriginalFilename() + ": " + validationResult.get("message"));
                        continue;
                    }
                    
                    // 保存单个文件
                    Map<String, Object> saveResult = saveFile(file, username, assetId);
                    if ((Boolean) saveResult.get("success")) {
                        successFiles.add(file.getOriginalFilename());
                        uploadResults.add(saveResult);
                    } else {
                        failedFiles.add(file.getOriginalFilename() + ": " + saveResult.get("message"));
                    }
                    
                } catch (Exception e) {
                    failedFiles.add(file.getOriginalFilename() + ": " + e.getMessage());
                    logger.error("上传文件 {} 失败", file.getOriginalFilename(), e);
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalFiles", files.length);
            result.put("successCount", successFiles.size());
            result.put("failedCount", failedFiles.size());
            result.put("successFiles", successFiles);
            result.put("failedFiles", failedFiles);
            result.put("uploadResults", uploadResults);
            
            if (failedFiles.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("所有文件上传成功", result));
            } else if (successFiles.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("所有文件上传失败", result));
            } else {
                return ResponseEntity.ok(ApiResponse.success("部分文件上传成功", result));
            }
            
        } catch (Exception e) {
            logger.error("批量文件上传失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("批量文件上传失败: " + e.getMessage()));
        }
    }

    /**
     * 获取文件上传进度（用于大文件上传）
     */
    @GetMapping("/progress/{uploadId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUploadProgress(
            @PathVariable String uploadId,
            Authentication authentication) {
        
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录"));
            }
            
            // TODO: 实现上传进度查询逻辑
            Map<String, Object> progress = new HashMap<>();
            progress.put("uploadId", uploadId);
            progress.put("status", "completed");
            progress.put("progress", 100);
            
            return ResponseEntity.ok(ApiResponse.success("获取进度成功", progress));
            
        } catch (Exception e) {
            logger.error("获取上传进度失败", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取上传进度失败: " + e.getMessage()));
        }
    }

    /**
     * 验证文件
     */
    private Map<String, Object> validateFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            result.put("valid", false);
            result.put("message", "文件不能为空");
            return result;
        }
        
        // 检查文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "文件名不能为空");
            return result;
        }
        
        // 检查文件扩展名
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            result.put("valid", false);
            result.put("message", "不支持的文件类型: " + extension);
            return result;
        }
        
        // 检查文件大小（5GB限制）
        long maxSize = 5L * 1024 * 1024 * 1024; // 5GB
        if (file.getSize() > maxSize) {
            result.put("valid", false);
            result.put("message", "文件大小超过5GB限制");
            return result;
        }
        
        result.put("valid", true);
        return result;
    }

    /**
     * 保存文件到磁盘
     */
    private Map<String, Object> saveFile(MultipartFile file, String username, String assetId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建上传目录
            String userDir = username.replaceAll("[^a-zA-Z0-9]", "_");
            String dateDir = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Path uploadDir = Paths.get(uploadPath, userDir, dateDir);
            Files.createDirectories(uploadDir);
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String baseFilename = getFileNameWithoutExtension(originalFilename);
            String uniqueFilename = baseFilename + "_" + System.currentTimeMillis() + 
                                  (extension.isEmpty() ? "" : "." + extension);
            
            // 保存文件
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 构建返回结果
            result.put("success", true);
            result.put("originalName", originalFilename);
            result.put("savedName", uniqueFilename);
            result.put("size", file.getSize());
            result.put("contentType", file.getContentType());
            result.put("relativePath", Paths.get(userDir, dateDir, uniqueFilename).toString());
            result.put("absolutePath", filePath.toString());
            result.put("uploadTime", new Date());
            
            if (assetId != null) {
                result.put("assetId", assetId);
            }
            
            logger.info("文件保存成功: {} -> {}", originalFilename, filePath);
            
        } catch (IOException e) {
            logger.error("保存文件失败", e);
            result.put("success", false);
            result.put("message", "保存文件失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 获取不带扩展名的文件名
     */
    private String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename;
        }
        return filename.substring(0, lastDotIndex);
    }

    @GetMapping("/download/{assetId}/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String assetId,
            @PathVariable String filename,
            Authentication authentication) {
        
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(null);
            }
            
            // 获取资产信息
            AgriculturalAsset asset = agriculturalAssetService.getAssetById(assetId);
            if (asset == null) {
                logger.warn("资产不存在: {}", assetId);
                return ResponseEntity.notFound().build();
            }
            
            // 查找指定的附件文件
            AgriculturalAsset.AttachedFile targetFile = null;
            if (asset.getAttachedFiles() != null) {
                for (AgriculturalAsset.AttachedFile file : asset.getAttachedFiles()) {
                    if (filename.equals(file.getSavedName()) || filename.equals(file.getName())) {
                        targetFile = file;
                        break;
                    }
                }
            }
            
            if (targetFile == null) {
                logger.warn("文件不存在: {} in asset {}", filename, assetId);
                return ResponseEntity.notFound().build();
            }
            
            // 构建文件路径
            Path filePath = Paths.get(uploadPath, targetFile.getRelativePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // 设置响应头
                String encodedFilename = URLEncoder.encode(targetFile.getName(), StandardCharsets.UTF_8);
                
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            } else {
                logger.warn("文件不可读: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("下载文件失败", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
} 