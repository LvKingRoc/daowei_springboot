package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            return ResponseEntity.ok(ApiResponse.success(order));
        } else {
            return ResponseEntity.ok(ApiResponse.error("订单不存在", 404));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listByCondition() {
        List<Order> orders = orderService.listByCondition();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Order order) {
        ApiResponse response = orderService.save(order);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        ApiResponse response = orderService.update(order);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = orderService.delete(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
}