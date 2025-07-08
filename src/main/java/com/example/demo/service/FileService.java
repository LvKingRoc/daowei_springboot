package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * 文件处理服务接口
 * 提供文件的上传、删除等操作
 * 负责系统中所有与文件处理相关的业务逻辑
 */
public interface FileService {
    
    /**
     * 保存上传的文件到服务器
     * 
     * @param file 上传的文件对象
     * @param fileName 保存的文件名
     * @return 保存后的文件访问路径
     * @throws IOException 文件操作异常
     */
    String saveFile(MultipartFile file, String fileName) throws IOException;
    
    /**
     * 删除服务器上的文件
     * 
     * @param filePath 要删除的文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 根据原始文件名和实体ID生成唯一的文件名
     * 防止文件名冲突和安全问题
     * 
     * @param originalFilename 原始文件名
     * @param id 关联的实体ID
     * @return 生成的新文件名
     */
    String generateFileName(String originalFilename, Long id);
} 