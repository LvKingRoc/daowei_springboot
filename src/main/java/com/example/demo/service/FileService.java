package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * 文件处理服务接口
 */
public interface FileService {
    
    /**
     * 保存文件
     * @param file 文件
     * @param fileName 文件名
     * @return 文件路径
     * @throws IOException
     */
    String saveFile(MultipartFile file, String fileName) throws IOException;
    
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 生成文件名
     * @param originalFilename 原始文件名
     * @param id 实体ID
     * @return 新文件名
     */
    String generateFileName(String originalFilename, Long id);
} 