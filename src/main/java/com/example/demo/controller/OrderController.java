package com.example.demo.controller;

import com.example.demo.annotation.Log;
import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private ObjectMapper objectMapper;

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
     * @return 创建结果的HTTP响应
     */
    @Log(module = "订单管理", action = "CREATE", description = "创建订单")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Order order) {
        Order savedOrder = orderService.save(order);
        return ResponseEntity.ok(ApiResponse.success("订单创建成功", savedOrder));
    }

    /**
     * 更新订单信息
     * 
     * @param id 订单ID
     * @param order 更新后的订单信息对象
     * @return 更新结果的HTTP响应
     */
    @Log(module = "订单管理", action = "UPDATE", description = "更新订单", entityType = "order", idParamIndex = 0)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody Order order) {
        // 获取原订单状态
        Order oldOrder = orderService.getById(id);
        String oldStatus = oldOrder != null ? oldOrder.getStatus() : null;
        
        order.setId(id);
        Order updatedOrder = orderService.update(order);
        
        // 推送订单更新通知（包含完整订单数据，供其他客户端实时同步）
        try {
            String orderJson = objectMapper.writeValueAsString(updatedOrder);
            String message = String.format(
                "{\"action\":\"update\",\"oldStatus\":\"%s\",\"order\":%s}",
                oldStatus != null ? oldStatus : "",
                orderJson
            );
            NotificationController.broadcast("order_sync", message);
        } catch (Exception e) {
            // 序列化失败不影响主流程
        }
        
        return ResponseEntity.ok(ApiResponse.success("订单更新成功", updatedOrder));
    }

    /**
     * 删除订单
     * 
     * @param id 要删除的订单ID
     * @return 删除结果的HTTP响应
     */
    @Log(module = "订单管理", action = "DELETE", description = "删除订单", entityType = "order", idParamIndex = 0)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("订单删除成功", id));
    }
}