package com.example.demo.service;

import com.example.demo.dto.DeleteResultDTO;
import com.example.demo.entity.Sample;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 样品服务接口
 * 提供样品信息管理相关的业务逻辑
 */
public interface SampleService {
    
    /**
     * 获取所有样品信息
     * 
     * @return 样品列表
     */
    List<Sample> getSamples();

    /**
     * 分页获取样品列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页样品数据
     */
    PageInfo<Sample> getByPage(int pageNum, int pageSize);

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
     * @return 删除结果DTO
     */
    DeleteResultDTO delete(Long id);

    /**
     * 修复数据库中客户ID为空的样品记录
     * 用于数据维护和修复
     * 
     * @return 修复的记录数
     */
    int fixNullCustomers();

    /**
     * 创建新样品，同时处理图片上传
     * 
     * @param sampleJson 样品信息的JSON字符串
     * @param image 样品图片文件（可选）
     * @return 创建后的样品对象
     */
    Sample createSample(String sampleJson, MultipartFile image);

    /**
     * 更新现有样品信息，同时处理图片上传
     * 
     * @param id 要更新的样品ID
     * @param sampleJson 更新后的样品信息JSON字符串
     * @param image 新的样品图片文件（可选）
     * @return 更新后的样品对象
     */
    Sample updateSample(Long id, String sampleJson, MultipartFile image);

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
     */
    void deleteImage(Long id);
}