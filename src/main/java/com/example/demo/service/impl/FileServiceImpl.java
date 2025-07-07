package com.example.demo.service.impl;

import com.example.demo.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件处理服务实现类
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file, String fileName) throws IOException {
        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);
        return "/sampleImage/" + fileName;
    }

    @Override
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        String physicalPath = uploadDir + filePath.replace("/sampleImage/", "");
        File file = new File(physicalPath);
        return file.exists() && file.delete();
    }

    @Override
    public String generateFileName(String originalFilename, Long id) {
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        return id + extension;
    }
} 