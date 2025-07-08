package com.example.demo.service.impl;

import com.example.demo.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件处理服务实现类
 * 实现FileService接口，提供文件的上传、删除等操作
 * 负责系统中所有与文件处理相关的业务逻辑
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * 文件上传目录路径
     * 从application.properties中注入配置的上传目录路径
     */
    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * 保存上传的文件到服务器
     * 将MultipartFile对象保存到指定的文件系统路径
     *
     * @param file 上传的文件对象
     * @param fileName 保存的文件名
     * @return 保存后的文件访问URL路径
     * @throws IOException 文件读写过程中可能发生的异常
     */
    @Override
    public String saveFile(MultipartFile file, String fileName) throws IOException {
        // 创建目标文件对象
        File dest = new File(uploadDir + fileName);
        // 将上传的文件保存到目标位置
        file.transferTo(dest);
        // 返回文件的访问URL路径
        return "/sampleImage/" + fileName;
    }

    /**
     * 删除服务器上的文件
     * 根据文件路径删除对应的物理文件
     *
     * @param filePath 要删除的文件路径（URL路径）
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(String filePath) {
        // 检查文件路径是否有效
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        // 将URL路径转换为物理文件路径
        String physicalPath = uploadDir + filePath.replace("/sampleImage/", "");
        // 创建文件对象
        File file = new File(physicalPath);
        // 检查文件是否存在并尝试删除
        return file.exists() && file.delete();
    }

    /**
     * 根据原始文件名和实体ID生成唯一的文件名
     * 使用实体ID作为文件名前缀，保留原文件扩展名
     *
     * @param originalFilename 原始文件名
     * @param id 关联的实体ID
     * @return 生成的新文件名
     */
    @Override
    public String generateFileName(String originalFilename, Long id) {
        // 提取文件扩展名，如果原始文件名为空则默认使用.jpg
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        // 使用实体ID加扩展名作为新文件名
        return id + extension;
    }
} 