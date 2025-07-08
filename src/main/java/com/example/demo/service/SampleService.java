package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Sample;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

/**
 * 样品服务接口
 * 提供样品信息管理相关的业务逻辑
 * 包括样品的增删改查、图片处理以及关联数据的处理
 */
public interface SampleService {
    
    /**
     * 获取所有样品信息
     * 
     * @return 样品列表
     */
    List<Sample> getSamples();

    /**
     * 根据ID获取样品详细信息
     * 
     * @param id 样品ID
     * @return 样品对象，如果不存在则返回null
     */
    Sample getById(Long id);

    /**
     * 保存样品基础信息（不包含图片处理）
     * 
     * @param sample 要保存的样品对象
     * @return 保存成功后的样品ID
     */
    Long save(Sample sample);

    /**
     * 更新样品基础信息（不包含图片处理）
     * 
     * @param sample 更新后的样品对象
     * @return 是否更新成功
     */
    boolean update(Sample sample);

    /**
     * 删除指定样品及其关联资源
     * 
     * @param id 要删除的样品ID
     * @return 删除结果的API响应
     */
    ApiResponse delete(Long id);

    /**
     * 修复数据库中客户ID为空的样品记录
     * 用于数据维护和修复
     * 
     * @return 修复操作结果的API响应
     */
    ApiResponse fixNullCustomers();

    /**
     * 创建新样品，同时处理图片上传
     * 
     * @param sampleJson 样品信息的JSON字符串
     * @param image 样品图片文件（可选）
     * @return 创建结果的API响应
     */
    ApiResponse createSample(String sampleJson, MultipartFile image);

    /**
     * 更新现有样品信息，同时处理图片上传
     * 
     * @param id 要更新的样品ID
     * @param sampleJson 更新后的样品信息JSON字符串
     * @param image 新的样品图片文件（可选）
     * @return 更新结果的API响应
     */
    ApiResponse updateSample(Long id, String sampleJson, MultipartFile image);

    /**
     * 获取指定样品关联的订单数量
     * 
     * @param sampleId 样品ID
     * @return 该样品的订单数量
     */
    int getOrderCount(Long sampleId);

    /**
     * 删除指定样品的图片
     * 
     * @param id 样品ID
     * @return 删除图片操作结果的API响应
     */
    ApiResponse deleteImage(Long id);
}