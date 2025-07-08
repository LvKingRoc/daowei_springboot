package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Customer;
import java.util.List;

/**
 * 客户服务接口
 * 提供客户信息管理相关的业务逻辑
 * 包括客户的增删改查及关联数据的获取
 */
public interface CustomerService {
    
    /**
     * 根据ID获取客户详细信息
     * 
     * @param id 客户ID
     * @return 客户对象，如果不存在则返回null
     */
    Customer getById(Long id);

    /**
     * 获取所有客户（按公司名称排序）
     * 
     * @return 排序后的客户列表
     */
    List<Customer> listByCompanyName();

    /**
     * 保存新客户信息
     * 
     * @param customer 要保存的客户对象
     * @return 保存结果的API响应
     */
    ApiResponse save(Customer customer);

    /**
     * 更新现有客户信息
     * 
     * @param customer 更新后的客户对象
     * @return 更新结果的API响应
     */
    ApiResponse update(Customer customer);

    /**
     * 删除指定客户
     * 
     * @param id 要删除的客户ID
     * @return 删除结果的API响应
     */
    ApiResponse delete(Long id);
    
    /**
     * 获取指定客户的订单数量
     * 
     * @param customerId 客户ID
     * @return 该客户的订单数量
     */
    int getOrderCountByCustomerId(Long customerId);
    
    /**
     * 获取指定客户的样板数量
     * 
     * @param customerId 客户ID
     * @return 该客户的样板数量
     */
    int getSampleCountByCustomerId(Long customerId);
}