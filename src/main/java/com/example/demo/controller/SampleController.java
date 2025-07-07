package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Sample;
import com.example.demo.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 样品控制器
 */
@RestController
@RequestMapping("/api/samples")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    /**
     * 根据ID获取样品
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        Sample sample = sampleService.getById(id);
        if (sample != null) {
            return ResponseEntity.ok(ApiResponse.success(sample));
        } else {
            return ResponseEntity.ok(ApiResponse.error("样品不存在", 404));
        }
    }

    /**
     * 获取所有样品
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getSamples() {
        List<Sample> samples = sampleService.getSamples();
        return ResponseEntity.ok(ApiResponse.success(samples));
    }

    /**
     * 创建样品
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> create(
            @RequestPart(value = "sample", required = true) String sampleJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        ApiResponse response = sampleService.createSample(sampleJson, image);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 更新样品
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestPart(value = "sample", required = true) String sampleJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        ApiResponse response = sampleService.updateSample(id, sampleJson, image);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 删除样品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = sampleService.delete(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 修复空客户ID的样品
     */
    @PostMapping("/fix-null-customers")
    public ResponseEntity<ApiResponse> fixNullCustomers() {
        ApiResponse response = sampleService.fixNullCustomers();
        return ResponseEntity.ok(response);
    }

    /**
     * 获取样品关联的订单数量
     */
    @GetMapping("/{id}/orders/count")
    public ResponseEntity<ApiResponse> getOrderCount(@PathVariable Long id) {
        int count = sampleService.getOrderCount(id);
        return ResponseEntity.ok(ApiResponse.success("获取订单数量成功", count));
    }

    /**
     * 删除样品图片
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long id) {
        ApiResponse response = sampleService.deleteImage(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
}