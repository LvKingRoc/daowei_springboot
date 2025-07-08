package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Sample;
import com.example.demo.entity.Order;
import com.example.demo.mapper.SampleMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.SampleService;
import com.example.demo.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 样品服务实现类
 * 实现SampleService接口，提供样品信息管理相关的业务逻辑
 * 负责处理样品的增删改查、图片处理以及关联数据的处理
 */
@Service
public class SampleServiceImpl implements SampleService {

    /**
     * 样品数据访问对象
     * 用于对样品数据进行CRUD操作
     */
    @Autowired
    private SampleMapper sampleMapper;

    /**
     * 订单数据访问对象
     * 用于处理与样品关联的订单数据
     */
    @Autowired
    private OrderMapper orderMapper;

    /**
     * JSON对象映射器
     * 用于JSON字符串与Java对象之间的转换
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 文件服务
     * 用于处理样品图片的保存和删除
     */
    @Autowired
    private FileService fileService;

    /**
     * 获取所有样品信息
     *
     * @return 样品列表
     */
    @Override
    public List<Sample> getSamples() {
        // 查询并返回所有样品记录
        return sampleMapper.selectAll();
    }

    /**
     * 根据ID获取样品详细信息
     *
     * @param id 样品ID
     * @return 样品对象，如果不存在则返回null
     */
    @Override
    public Sample getById(Long id) {
        // 根据ID查询样品
        return sampleMapper.selectById(id);
    }

    /**
     * 保存样品基础信息（不包含图片处理）
     *
     * @param sample 要保存的样品对象
     * @return 保存成功后的样品ID
     */
    @Override
    @Transactional
    public Long save(Sample sample) {
        // 插入样品记录并返回生成的ID
        sampleMapper.insert(sample);
        return sample.getId();
    }

    /**
     * 更新样品基础信息（不包含图片处理）
     *
     * @param sample 更新后的样品对象
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public boolean update(Sample sample) {
        // 更新样品记录，返回影响的行数大于0表示更新成功
        return sampleMapper.update(sample) > 0;
    }

    /**
     * 删除指定样品及其关联资源
     * 包括关联的订单和图片文件
     *
     * @param id 要删除的样品ID
     * @return 删除结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse delete(Long id) {
        try {
            // 先删除关联的订单
            List<Order> relatedOrders = orderMapper.listBySampleId(id);
            for (Order order : relatedOrders) {
                orderMapper.delete(order.getId());
            }
            
            // 删除样品的图片文件
            Sample existingSample = getById(id);
            if (existingSample != null && existingSample.getImage() != null) {
                fileService.deleteFile(existingSample.getImage());
            }
            
            // 删除样品记录
            sampleMapper.delete(id);
            return ApiResponse.success("样品删除成功", id);
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("删除样品失败：" + e.getMessage());
        }
    }

    /**
     * 修复数据库中客户ID为空的样品记录
     * 将所有客户ID为null的样品关联到ID为1的客户
     *
     * @return 修复操作结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse fixNullCustomers() {
        try {
            // 查询所有客户ID为null的样品
            List<Sample> nullCustomerSamples = sampleMapper.findByNullCustomerId();
            int fixedCount = 0;
            
            // 为每个样品设置默认客户ID（1）
            for (Sample sample : nullCustomerSamples) {
                sample.setCustomerId(1L);
                fixedCount += sampleMapper.updateCustomerId(sample.getId(), sample.getCustomerId());
            }
            
            return ApiResponse.success("修复完成，共修复 " + fixedCount + " 个样品", fixedCount);
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("修复失败：" + e.getMessage());
        }
    }

    /**
     * 创建新样品，同时处理图片上传
     *
     * @param sampleJson 样品信息的JSON字符串
     * @param image 样品图片文件（可选）
     * @return 创建结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse createSample(String sampleJson, MultipartFile image) {
        try {
            // 解析JSON字符串为样品对象
            Sample sample = objectMapper.readValue(sampleJson, Sample.class);
            // 保存样品基本信息
            Long id = save(sample);
            
            // 处理图片上传（如果有）
            if (image != null && !image.isEmpty()) {
                // 生成文件名
                String fileName = fileService.generateFileName(image.getOriginalFilename(), id);
                // 保存图片文件并获取访问路径
                String imagePath = fileService.saveFile(image, fileName);
                // 更新样品记录，关联图片路径
                sample.setId(id);
                sample.setImage(imagePath);
                update(sample);
            }
            
            return ApiResponse.success("样品创建成功", id);
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("创建样品失败：" + e.getMessage());
        }
    }

    /**
     * 更新现有样品信息，同时处理图片上传
     *
     * @param id 要更新的样品ID
     * @param sampleJson 更新后的样品信息JSON字符串
     * @param image 新的样品图片文件（可选）
     * @return 更新结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse updateSample(Long id, String sampleJson, MultipartFile image) {
        try {
            // 解析JSON字符串为样品对象
            Sample sample = objectMapper.readValue(sampleJson, Sample.class);
            sample.setId(id);
            
            if (image != null && !image.isEmpty()) {
                // 有新图片上传，先删除旧图片
                Sample existingSample = getById(id);
                if (existingSample != null && existingSample.getImage() != null) {
                    fileService.deleteFile(existingSample.getImage());
                }
                
                // 保存新图片
                String fileName = fileService.generateFileName(image.getOriginalFilename(), id);
                String imagePath = fileService.saveFile(image, fileName);
                sample.setImage(imagePath);
            } else if (image != null && image.isEmpty()) {
                // 特殊情况：提交了空图片，表示删除现有图片
                Sample existingSample = getById(id);
                if (existingSample != null && existingSample.getImage() != null) {
                    fileService.deleteFile(existingSample.getImage());
                }
                sample.setImage(null);
            }
            
            // 更新样品记录
            boolean result = update(sample);
            return result ? 
                ApiResponse.success("样品更新成功", id) : 
                ApiResponse.error("样品更新失败");
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("更新样品失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定样品关联的订单数量
     *
     * @param sampleId 样品ID
     * @return 该样品的订单数量
     */
    @Override
    public int getOrderCount(Long sampleId) {
        // 查询样品关联的所有订单
        List<Order> orders = orderMapper.listBySampleId(sampleId);
        // 返回订单数量
        return orders != null ? orders.size() : 0;
    }

    /**
     * 删除指定样品的图片
     *
     * @param id 样品ID
     * @return 删除图片操作结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse deleteImage(Long id) {
        try {
            // 查询样品信息
            Sample existingSample = getById(id);
            if (existingSample == null) {
                return ApiResponse.error("样品不存在", 404);
            }
            
            // 检查样品是否有图片
            if (existingSample.getImage() == null) {
                return ApiResponse.error("样品没有图片");
            }
            
            // 删除物理文件
            boolean deleted = fileService.deleteFile(existingSample.getImage());
            if (!deleted) {
                return ApiResponse.error("删除图片文件失败");
            }
            
            // 更新数据库中的图片路径为null
            existingSample.setImage(null);
            boolean updated = update(existingSample);
            
            return updated ? 
                ApiResponse.success("图片删除成功", null) : 
                ApiResponse.error("更新数据库失败");
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("删除图片失败：" + e.getMessage());
        }
    }
}