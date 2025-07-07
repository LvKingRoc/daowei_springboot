package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * 图片控制器 - 直接访问本地文件，不使用缓存
 */
@RestController
@RequestMapping("/sampleImage")
public class ImageController {

    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            // 构建完整的文件路径
            String filePath = uploadDir + fileName;
            File file = new File(filePath);
            
            // 检查文件是否存在
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // 创建文件资源
            Resource resource = new FileSystemResource(file);
            
            // 设置响应头，禁用缓存
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0L);
            
            // 根据文件扩展名设置Content-Type
            String contentType = getContentType(fileName);
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".bmp":
                return "image/bmp";
            case ".webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
} 