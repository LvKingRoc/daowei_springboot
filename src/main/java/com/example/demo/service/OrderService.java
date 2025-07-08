package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 * 提供订单信息管理相关的业务逻辑
 * 包括订单的增删改查等操作
 */
public interface OrderService {
    
    /**
     * 根据ID获取订单详细信息
     * 
     * @param id 订单ID
     * @return 订单对象，如果不存在则返回null
     */
    Order getById(Long id);

    /**
     * 根据条件查询订单列表
     * 
     * @return 符合条件的订单列表
     */
    List<Order> listByCondition();

    /**
     * 保存新订单
     * 
     * @param order 要保存的订单对象
     * @return 保存结果的API响应
     */
    ApiResponse save(Order order);

    /**
     * 更新现有订单信息
     * 
     * @param order 更新后的订单对象
     * @return 更新结果的API响应
     */
    ApiResponse update(Order order);

    /**
     * 删除指定订单
     * 
     * @param id 要删除的订单ID
     * @return 删除结果的API响应
     */
    ApiResponse delete(Long id);
}