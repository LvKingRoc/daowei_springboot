package com.example.demo.controller;

import com.example.demo.annotation.Log;
import com.example.demo.common.ApiResponse;
import com.example.demo.dto.DeleteResultDTO;
import com.example.demo.entity.Sample;
import com.example.demo.service.SampleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 样品管理控制器
 * 处理样品相关的HTTP请求，提供样品的增删改查等REST API接口
 * 支持样品信息和图片的上传、更新和删除操作
 * 提供样品数据修复和关联数据查询功能
 */
@RestController
@RequestMapping("/api/samples")
public class SampleController {

    /**
     * 样品服务接口
     * 处理样品相关的业务逻辑，包括样品的增删改查及图片处理
     */
    @Autowired
    private SampleService sampleService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 根据ID获取单个样品信息
     * 
     * @param id 样品ID
     * @return 包含样品信息的HTTP响应，如果样品不存在则返回错误信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        // 调用服务获取样品信息
        Sample sample = sampleService.getById(id);
        if (sample != null) {
            // 样品存在，返回样品信息
            return ResponseEntity.ok(ApiResponse.success(sample));
        } else {
            // 样品不存在，返回错误信息
            return ResponseEntity.ok(ApiResponse.error("样品不存在", 404));
        }
    }

    /**
     * 获取所有样品列表
     * 
     * @return 包含样品列表的HTTP响应
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getSamples() {
        // 调用服务获取所有样品
        List<Sample> samples = sampleService.getSamples();
        return ResponseEntity.ok(ApiResponse.success(samples));
    }

    /**
     * 分页获取样品列表
     * 
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认20）
     * @return 分页样品数据
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageInfo<Sample> pageInfo = sampleService.getByPage(pageNum, pageSize);
        return ResponseEntity.ok(ApiResponse.success(pageInfo));
    }

    /**
     * 创建新样品，同时支持上传样品图片
     * 使用multipart/form-data格式接收数据
     * 
     * @param sampleJson 样品信息的JSON字符串
     * @param image 样品图片文件（可选）
     * @return 创建结果的HTTP响应
     */
    @Log(module = "样品管理", action = "CREATE", description = "创建样品")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> create(
            @RequestPart(value = "sample", required = true) String sampleJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        Sample createdSample = sampleService.createSample(sampleJson, image);
        return ResponseEntity.ok(ApiResponse.success("样品创建成功", createdSample));
    }

    /**
     * 更新现有样品信息，同时支持更新样品图片
     * 使用multipart/form-data格式接收数据
     * 
     * @param id 要更新的样品ID
     * @param sampleJson 更新后的样品信息JSON字符串
     * @param image 新的样品图片文件（可选）
     * @return 更新结果的HTTP响应
     */
    @Log(module = "样品管理", action = "UPDATE", description = "更新样品", entityType = "sample", idParamIndex = 0)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestPart(value = "sample", required = true) String sampleJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        Sample updatedSample = sampleService.updateSample(id, sampleJson, image);
        
        // 推送样品更新通知（包含完整样品数据，供其他客户端实时同步）
        try {
            String sampleJsonStr = objectMapper.writeValueAsString(updatedSample);
            String message = String.format("{\"action\":\"update\",\"sample\":%s}", sampleJsonStr);
            NotificationController.broadcast("sample_sync", message);
        } catch (Exception e) {
            // 序列化失败不影响主流程
        }
        
        return ResponseEntity.ok(ApiResponse.success("样品更新成功", updatedSample));
    }

    /**
     * 删除指定样品
     * 
     * @param id 要删除的样品ID
     * @return 删除结果的HTTP响应
     */
    @Log(module = "样品管理", action = "DELETE", description = "删除样品", entityType = "sample", idParamIndex = 0)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        DeleteResultDTO result = sampleService.delete(id);
        String message = result.getDescription() != null 
            ? "样品删除成功，" + result.getDescription()
            : "样品删除成功";
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    /**
     * 修复数据库中客户ID为空的样品记录
     * 用于数据维护和修复
     * 
     * @return 修复操作结果的HTTP响应
     */
    @PostMapping("/fix-null-customers")
    public ResponseEntity<ApiResponse> fixNullCustomers() {
        int fixedCount = sampleService.fixNullCustomers();
        return ResponseEntity.ok(ApiResponse.success("修复完成，共修复 " + fixedCount + " 个样品", fixedCount));
    }

    /**
     * 获取指定样品关联的订单数量
     * 
     * @param id 样品ID
     * @return 包含订单数量的HTTP响应
     */
    @GetMapping("/{id}/orders/count")
    public ResponseEntity<ApiResponse> getOrderCount(@PathVariable Long id) {
        int count = sampleService.getOrderCount(id);
        return ResponseEntity.ok(ApiResponse.success("获取订单数量成功", count));
    }

    /**
     * 删除指定样品的图片
     * 
     * @param id 样品ID
     * @return 删除图片操作结果的HTTP响应
     */
    @Log(module = "样品管理", action = "UPDATE", description = "删除样品图片", entityType = "sample", idParamIndex = 0)
    @DeleteMapping("/{id}/image")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long id) {
        sampleService.deleteImage(id);
        return ResponseEntity.ok(ApiResponse.success("图片删除成功", null));
    }
}