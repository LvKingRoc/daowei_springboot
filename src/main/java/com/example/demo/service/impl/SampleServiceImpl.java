package com.example.demo.service.impl;

import com.example.demo.dto.DeleteResultDTO;
import com.example.demo.entity.OperationLog;
import com.example.demo.entity.Sample;
import com.example.demo.entity.Order;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.SampleMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.OperationLogService;
import com.example.demo.service.SampleService;
import com.example.demo.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 样品服务实现类
 * 实现SampleService接口，提供样品信息管理相关的业务逻辑
 * 负责处理样品的增删改查、图片处理以及关联数据的处理
 */
@Service
public class SampleServiceImpl implements SampleService {

    /**
     * 允许上传的图片类型白名单
     */
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp"
    ));
    
    /**
     * 允许的图片文件扩展名
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp", "bmp"
    ));

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

    @Autowired
    private OperationLogService operationLogService;

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
     * 分页获取样品列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页样品数据
     */
    @Override
    public PageInfo<Sample> getByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Sample> list = sampleMapper.selectAll();
        return new PageInfo<>(list);
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
     * @return 删除结果DTO
     */
    @Override
    @Transactional
    public DeleteResultDTO delete(Long id) {
        // 获取样品信息（用于日志）
        Sample existingSample = getById(id);
        if (existingSample == null) {
            throw new BusinessException("样品不存在", 404);
        }

        // 先删除关联的订单，并记录日志
        List<Order> relatedOrders = orderMapper.listBySampleId(id);
        for (Order order : relatedOrders) {
            // 记录连带删除订单的日志
            saveCascadeDeleteLog(order, existingSample);
            orderMapper.delete(order.getId());
        }

        // 删除样品的图片文件
        if (existingSample.getImage() != null) {
            fileService.deleteFile(existingSample.getImage());
        }

        // 删除样品记录
        sampleMapper.delete(id);

        // 返回结果，包含连带删除的订单数量
        String description = relatedOrders.isEmpty()
                ? null
                : "连带删除" + relatedOrders.size() + "个关联订单";
        return DeleteResultDTO.of(id, relatedOrders.size(), description);
    }

    /**
     * 记录连带删除订单的日志
     */
    private void saveCascadeDeleteLog(Order order, Sample sample) {
        try {
            OperationLog log = new OperationLog();
            log.setModule("订单管理");
            log.setAction("CASCADE_DELETE");
            log.setDescription("删除样品[" + (sample != null ? sample.getAlias() : "ID:" + order.getSampleId()) + "]时连带删除订单");
            log.setStatus(1);

            // 记录被删除的订单数据
            try {
                String oldDataStr = objectMapper.writeValueAsString(order);
                if (oldDataStr.length() > 5000) {
                    oldDataStr = oldDataStr.substring(0, 5000) + "...";
                }
                log.setOldData(oldDataStr);
            } catch (Exception e) {
                log.setOldData("序列化失败");
            }

            // 获取当前用户信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Long userId = (Long) request.getAttribute("userId");
                String username = (String) request.getAttribute("username");
                String role = (String) request.getAttribute("role");

                log.setUserId(userId);
                log.setUsername(username);
                log.setRole(role);
                log.setRequestUrl(request.getRequestURI());
                log.setRequestMethod(request.getMethod());
                log.setIpAddress(getIpAddress(request));
                log.setUserAgent(request.getHeader("User-Agent"));
            }

            operationLogService.save(log);
        } catch (Exception e) {
            // 日志保存失败不影响主业务
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 修复数据库中客户ID为空的样品记录
     *
     * @return 修复的记录数
     */
    @Override
    @Transactional
    public int fixNullCustomers() {
        List<Sample> nullCustomerSamples = sampleMapper.findByNullCustomerId();
        int fixedCount = 0;
        for (Sample sample : nullCustomerSamples) {
            sample.setCustomerId(1L);
            fixedCount += sampleMapper.updateCustomerId(sample.getId(), sample.getCustomerId());
        }
        return fixedCount;
    }

    /**
     * 创建新样品，同时处理图片上传
     *
     * @return 创建后的样品对象
     */
    @Override
    @Transactional
    public Sample createSample(String sampleJson, MultipartFile image) {
        try {
            Sample sample = objectMapper.readValue(sampleJson, Sample.class);
            
            if (image != null && !image.isEmpty()) {
                String validationError = validateImageFile(image);
                if (validationError != null) {
                    throw new BusinessException(validationError);
                }
            }
            
            Long id = save(sample);
            
            if (image != null && !image.isEmpty()) {
                String fileName = fileService.generateFileName(image.getOriginalFilename(), id);
                String imagePath = fileService.saveFile(image, fileName);
                sample.setId(id);
                sample.setImage(imagePath);
                update(sample);
            }
            
            return getById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("创建样品失败：" + e.getMessage(), e);
        }
    }

    /**
     * 更新现有样品信息，同时处理图片上传
     *
     * @return 更新后的样品对象
     */
    @Override
    @Transactional
    public Sample updateSample(Long id, String sampleJson, MultipartFile image) {
        try {
            Sample sample = objectMapper.readValue(sampleJson, Sample.class);
            sample.setId(id);
            
            if (image != null && !image.isEmpty()) {
                String validationError = validateImageFile(image);
                if (validationError != null) {
                    throw new BusinessException(validationError);
                }
                
                Sample existingSample = getById(id);
                if (existingSample != null && existingSample.getImage() != null) {
                    fileService.deleteFile(existingSample.getImage());
                }
                
                String fileName = fileService.generateFileName(image.getOriginalFilename(), id);
                String imagePath = fileService.saveFile(image, fileName);
                sample.setImage(imagePath);
            } else if (image != null && image.isEmpty()) {
                Sample existingSample = getById(id);
                if (existingSample != null && existingSample.getImage() != null) {
                    fileService.deleteFile(existingSample.getImage());
                }
                sample.setImage(null);
            }
            
            boolean result = update(sample);
            if (!result) {
                throw new BusinessException("样品更新失败");
            }
            return getById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("更新样品失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取指定样品关联的订单数量
     */
    @Override
    public int getOrderCount(Long sampleId) {
        List<Order> orders = orderMapper.listBySampleId(sampleId);
        return orders != null ? orders.size() : 0;
    }

    /**
     * 删除指定样品的图片
     */
    @Override
    @Transactional
    public void deleteImage(Long id) {
        Sample existingSample = getById(id);
        if (existingSample == null) {
            throw new BusinessException("样品不存在", 404);
        }
        
        if (existingSample.getImage() == null) {
            throw new BusinessException("样品没有图片");
        }
        
        boolean deleted = fileService.deleteFile(existingSample.getImage());
        if (!deleted) {
            throw new BusinessException("删除图片文件失败");
        }
        
        existingSample.setImage(null);
        boolean updated = update(existingSample);
        if (!updated) {
            throw new BusinessException("更新数据库失败");
        }
    }

    /**
     * 验证上传的图片文件
     */
    private String validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return "不支持的图片格式，仅支持: JPG, PNG, GIF, WebP, BMP";
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return "不支持的文件扩展名，仅支持: jpg, jpeg, png, gif, webp, bmp";
            }
        }
        
        return null;
    }
}