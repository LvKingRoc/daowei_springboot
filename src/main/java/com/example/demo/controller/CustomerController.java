package com.example.demo.controller;

import com.example.demo.annotation.Log;
import com.example.demo.common.ApiResponse;
import com.example.demo.dto.DeleteResultDTO;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户管理控制器
 * 提供客户信息的增删改查等REST API接口
 * 处理客户相关的HTTP请求，包括获取客户列表、单个客户信息、创建客户、更新客户、删除客户以及获取客户的样板数量和订单数量
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    /**
     * 客户服务接口
     * 注入客户服务实现类，处理客户相关的业务逻辑
     */
    @Autowired
    private CustomerService customerService;

    /**
     * 根据ID获取单个客户信息
     * 
     * @param id 客户ID
     * @return 包含客户信息的响应体，如果客户不存在则返回错误信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable Long id) {
        // 调用服务查询客户信息
        Customer customer = customerService.getById(id);
        if (customer != null) {
            // 客户存在，返回客户信息
            return ResponseEntity.ok(ApiResponse.success(customer));
        } else {
            // 客户不存在，返回错误信息
            return ResponseEntity.ok(ApiResponse.error("客户不存在", 404));
        }
    }

    /**
     * 获取所有客户列表
     * 按公司名称排序返回所有客户信息
     * 
     * @return 包含客户列表的响应体
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCustomers() {
        // 获取按公司名称排序的客户列表
        List<Customer> customers = customerService.listByCompanyName();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * 创建新客户
     * 
     * @param customer 客户信息对象
     * @return 创建结果的响应体
     */
    @Log(module = "客户管理", action = "CREATE", description = "创建客户")
    @PostMapping
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.save(customer);
        return ResponseEntity.ok(ApiResponse.success("客户创建成功", savedCustomer));
    }

    /**
     * 更新现有客户信息
     * 
     * @param id 客户ID
     * @param customer 更新后的客户信息对象
     * @return 更新结果的响应体
     */
    @Log(module = "客户管理", action = "UPDATE", description = "更新客户", entityType = "customer", idParamIndex = 0)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        Customer updatedCustomer = customerService.update(customer);
        return ResponseEntity.ok(ApiResponse.success("客户更新成功", updatedCustomer));
    }

    /**
     * 删除指定客户
     * 
     * @param id 要删除的客户ID
     * @return 删除结果的响应体
     */
    @Log(module = "客户管理", action = "DELETE", description = "删除客户", entityType = "customer", idParamIndex = 0)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        DeleteResultDTO result = customerService.delete(id);
        String message = result.getDescription() != null 
            ? "客户删除成功，" + result.getDescription()
            : "客户删除成功";
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }
    
    /**
     * 获取指定客户的样板数量
     * 
     * @param id 客户ID
     * @return 包含样板数量的响应体
     */
    @GetMapping("/{id}/samples/count")
    public ResponseEntity<ApiResponse> getSampleCountByCustomerId(@PathVariable Long id) {
        // 获取指定客户的样板数量
        int count = customerService.getSampleCountByCustomerId(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    /**
     * 获取指定客户的订单数量
     * 
     * @param id 客户ID
     * @return 包含订单数量的响应体
     */
    @GetMapping("/{id}/orders/count")
    public ResponseEntity<ApiResponse> getOrderCountByCustomerId(@PathVariable Long id) {
        // 获取指定客户的订单数量
        int count = customerService.getOrderCountByCustomerId(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}