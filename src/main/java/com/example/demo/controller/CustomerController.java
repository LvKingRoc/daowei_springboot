package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;

    /**
     * 获取单个客户
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer != null) {
            return ResponseEntity.ok(ApiResponse.success(customer));
        } else {
            return ResponseEntity.ok(ApiResponse.error("客户不存在", 404));
        }
    }

    /**
     * 查询客户列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCustomers() {
        List<Customer> customers = customerService.listByCompanyName();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * 创建客户
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody Customer customer) {
        ApiResponse response = customerService.save(customer);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        ApiResponse response = customerService.update(customer);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        ApiResponse response = customerService.delete(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 获取客户的样板数量
     */
    @GetMapping("/{id}/samples/count")
    public ResponseEntity<ApiResponse> getSampleCountByCustomerId(@PathVariable Long id) {
        int count = customerService.getSampleCountByCustomerId(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    /**
     * 获取客户的订单数量
     */
    @GetMapping("/{id}/orders/count")
    public ResponseEntity<ApiResponse> getOrderCountByCustomerId(@PathVariable Long id) {
        int count = customerService.getOrderCountByCustomerId(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}