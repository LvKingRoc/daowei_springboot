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
 * 图片控制器
 * 提供样品图片访问的REST API接口
 * 直接从服务器文件系统读取图片文件并返回，不使用缓存
 * 支持多种图片格式，如JPG、PNG、GIF等
 */
@RestController
@RequestMapping("/sampleImage")
public class ImageController {

    /**
     * 图片上传目录路径
     * 从application.properties中注入配置的上传目录路径
     */
    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * 获取指定文件名的图片资源
     * 根据文件名从文件系统中读取图片并返回
     * 
     * @param fileName 要获取的图片文件名，包含扩展名
     * @return 包含图片资源的HTTP响应，如果图片不存在则返回404
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            // 构建完整的文件路径
            String filePath = uploadDir + fileName;
            File file = new File(filePath);
            
            // 检查文件是否存在
            if (!file.exists()) {
                return ResponseEntity.notFound().build();  // 返回404 Not Found
            }
            
            // 创建文件资源
            Resource resource = new FileSystemResource(file);
            
            // 设置响应头，禁用缓存
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache, no-store, must-revalidate");  // 禁止缓存
            headers.setPragma("no-cache");  // 兼容HTTP 1.0
            headers.setExpires(0L);  // 设置过期时间为0
            
            // 根据文件扩展名设置Content-Type
            String contentType = getContentType(fileName);
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // 返回带有适当头信息的资源
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (Exception e) {
            // 处理异常情况，返回500错误
            return ResponseEntity.internalServerError().build();  // 返回500 Internal Server Error
        }
    }
    
    /**
     * 根据文件扩展名获取对应的MIME Content-Type
     * 用于正确设置HTTP响应的内容类型
     * 
     * @param fileName 文件名，用于提取文件扩展名
     * @return 对应的MIME类型字符串
     */
    private String getContentType(String fileName) {
        // 提取文件扩展名并转换为小写
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        // 根据扩展名返回对应的MIME类型
        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";  // JPEG图片格式
            case ".png":
                return "image/png";  // PNG图片格式
            case ".gif":
                return "image/gif";  // GIF图片格式
            case ".bmp":
                return "image/bmp";  // BMP图片格式
            case ".webp":
                return "image/webp";  // WebP图片格式
            default:
                return "application/octet-stream";  // 默认二进制流格式
        }
    }
} 