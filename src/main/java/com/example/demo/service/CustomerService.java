package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Customer;
import java.util.List;

public interface CustomerService {
    
    /**
     * 根据ID获取客户
     */
    Customer getById(Long id);

    /**
     * 获取所有客户（按公司名称排序）
     */
    List<Customer> listByCompanyName();

    /**
     * 保存客户
     */
    ApiResponse save(Customer customer);

    /**
     * 更新客户
     */
    ApiResponse update(Customer customer);

    /**
     * 删除客户
     */
    ApiResponse delete(Long id);
    
    /**
     * 获取客户的订单数量
     */
    int getOrderCountByCustomerId(Long customerId);
    
    /**
     * 获取客户的样板数量
     */
    int getSampleCountByCustomerId(Long customerId);
}