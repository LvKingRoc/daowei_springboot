package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 处理订单相关的HTTP请求
 * 提供订单的增删改查等REST API接口
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    /**
     * 订单服务接口
     * 处理订单相关的业务逻辑
     */
    @Autowired
    private OrderService orderService;

    /**
     * 根据ID获取单个订单
     * 
     * @param id 订单ID
     * @return 包含订单信息的HTTP响应，如果订单不存在则返回错误信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            return ResponseEntity.ok(ApiResponse.success(order));
        } else {
            return ResponseEntity.ok(ApiResponse.error("订单不存在", 404));
        }
    }

    /**
     * 获取订单列表
     * 根据条件查询订单
     * 
     * @return 包含订单列表的HTTP响应
     */
    @GetMapping
    public ResponseEntity<ApiResponse> listByCondition() {
        List<Order> orders = orderService.listByCondition();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    /**
     * 创建新订单
     * 
     * @param order 订单信息对象
     * @return 创建结果的HTTP响应，包含成功信息或错误信息
     */
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Order order) {
        ApiResponse response = orderService.save(order);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 更新订单信息
     * 
     * @param id 订单ID
     * @param order 更新后的订单信息对象
     * @return 更新结果的HTTP响应，包含成功信息或错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        ApiResponse response = orderService.update(order);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 删除订单
     * 
     * @param id 要删除的订单ID
     * @return 删除结果的HTTP响应，包含成功信息或错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = orderService.delete(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
}