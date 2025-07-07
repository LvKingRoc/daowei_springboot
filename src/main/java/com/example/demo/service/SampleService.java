package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Sample;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SampleService {
    
    /**
     * 获取所有样品
     */
    List<Sample> getSamples();

    /**
     * 根据ID获取样品
     */
    Sample getById(Long id);

    /**
     * 保存样品（基础方法）
     */
    Long save(Sample sample);

    /**
     * 更新样品（基础方法）
     */
    boolean update(Sample sample);

    /**
     * 删除样品
     */
    ApiResponse delete(Long id);

    /**
     * 修复空客户ID的样品
     */
    ApiResponse fixNullCustomers();

    /**
     * 创建样品（包含图片）
     */
    ApiResponse createSample(String sampleJson, MultipartFile image);

    /**
     * 更新样品（包含图片）
     */
    ApiResponse updateSample(Long id, String sampleJson, MultipartFile image);

    /**
     * 获取样品关联的订单数量
     */
    int getOrderCount(Long sampleId);

    /**
     * 删除样品图片
     */
    ApiResponse deleteImage(Long id);
}