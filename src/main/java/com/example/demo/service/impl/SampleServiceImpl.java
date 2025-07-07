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
 */
@Service
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleMapper sampleMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileService fileService;

    @Override
    public List<Sample> getSamples() {
        return sampleMapper.selectAll();
    }

    @Override
    public Sample getById(Long id) {
        return sampleMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long save(Sample sample) {
        sampleMapper.insert(sample);
        return sample.getId();
    }

    @Override
    @Transactional
    public boolean update(Sample sample) {
        return sampleMapper.update(sample) > 0;
    }

    @Override
    @Transactional
    public ApiResponse delete(Long id) {
        try {
            // 先删除关联的订单
            List<Order> relatedOrders = orderMapper.listBySampleId(id);
            for (Order order : relatedOrders) {
                orderMapper.delete(order.getId());
            }
            
            // 删除样品及其图片
            Sample existingSample = getById(id);
            if (existingSample != null && existingSample.getImage() != null) {
                fileService.deleteFile(existingSample.getImage());
            }
            
            sampleMapper.delete(id);
            return ApiResponse.success("样品删除成功", id);
        } catch (Exception e) {
            return ApiResponse.error("删除样品失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse fixNullCustomers() {
        try {
            List<Sample> nullCustomerSamples = sampleMapper.findByNullCustomerId();
            int fixedCount = 0;
            for (Sample sample : nullCustomerSamples) {
                sample.setCustomerId(1L);
                fixedCount += sampleMapper.updateCustomerId(sample.getId(), sample.getCustomerId());
            }
            return ApiResponse.success("修复完成，共修复 " + fixedCount + " 个样品", fixedCount);
        } catch (Exception e) {
            return ApiResponse.error("修复失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse createSample(String sampleJson, MultipartFile image) {
        try {
            Sample sample = objectMapper.readValue(sampleJson, Sample.class);
            Long id = save(sample);
            
            if (image != null && !image.isEmpty()) {
                String fileName = fileService.generateFileName(image.getOriginalFilename(), id);
                String imagePath = fileService.saveFile(image, fileName);
                sample.setId(id);
                sample.setImage(imagePath);
                update(sample);
            }
            
            return ApiResponse.success("样品创建成功", id);
        } catch (Exception e) {
            return ApiResponse.error("创建样品失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse updateSample(Long id, String sampleJson, MultipartFile image) {
        try {
            Sample sample = objectMapper.readValue(sampleJson, Sample.class);
            sample.setId(id);
            
            if (image != null && !image.isEmpty()) {
                // 删除旧图片
                Sample existingSample = getById(id);
                if (existingSample != null && existingSample.getImage() != null) {
                    fileService.deleteFile(existingSample.getImage());
                }
                
                // 保存新图片
                String fileName = fileService.generateFileName(image.getOriginalFilename(), id);
                String imagePath = fileService.saveFile(image, fileName);
                sample.setImage(imagePath);
            } else if (image != null && image.isEmpty()) {
                // 删除图片
                Sample existingSample = getById(id);
                if (existingSample != null && existingSample.getImage() != null) {
                    fileService.deleteFile(existingSample.getImage());
                }
                sample.setImage(null);
            }
            
            boolean result = update(sample);
            return result ? ApiResponse.success("样品更新成功", id) : ApiResponse.error("样品更新失败");
        } catch (Exception e) {
            return ApiResponse.error("更新样品失败：" + e.getMessage());
        }
    }

    @Override
    public int getOrderCount(Long sampleId) {
        List<Order> orders = orderMapper.listBySampleId(sampleId);
        return orders != null ? orders.size() : 0;
    }

    @Override
    @Transactional
    public ApiResponse deleteImage(Long id) {
        try {
            Sample existingSample = getById(id);
            if (existingSample == null) {
                return ApiResponse.error("样品不存在", 404);
            }
            
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
            
            return updated ? ApiResponse.success("图片删除成功", null) : ApiResponse.error("更新数据库失败");
        } catch (Exception e) {
            return ApiResponse.error("删除图片失败：" + e.getMessage());
        }
    }
}