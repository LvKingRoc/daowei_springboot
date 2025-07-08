package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Order;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单服务实现类
 * 实现OrderService接口，提供订单信息管理相关的业务逻辑
 * 负责处理订单的增删改查等操作
 */
@Service
public class OrderServiceImpl implements OrderService {

    /**
     * 订单数据访问对象
     * 用于对订单数据进行CRUD操作
     */
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 根据ID获取订单详细信息
     *
     * @param id 订单ID
     * @return 订单对象，如果不存在则返回null
     */
    @Override
    public Order getById(Long id) {
        // 调用数据访问对象根据ID查询订单
        return orderMapper.getById(id);
    }

    /**
     * 根据条件查询订单列表
     * 当前实现返回所有订单
     *
     * @return 符合条件的订单列表
     */
    @Override
    public List<Order> listByCondition() {
        // 调用数据访问对象查询所有订单
        return orderMapper.listAll();
    }

    /**
     * 保存新订单
     * 使用事务确保数据一致性
     *
     * @param order 要保存的订单对象
     * @return 保存结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse save(Order order) {
        try {
            // 插入订单记录
            orderMapper.insert(order);
            return ApiResponse.success("订单创建成功", order.getId());
        } catch (DuplicateKeyException e) {
            // 处理订单号重复异常
            return ApiResponse.error("订单号 '" + order.getOrderNumber() + "' 已存在，请使用其他订单号");
        } catch (Exception e) {
            // 处理其他异常
            return ApiResponse.error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 更新现有订单信息
     * 使用事务确保数据一致性
     *
     * @param order 更新后的订单对象
     * @return 更新结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse update(Order order) {
        try {
            // 更新订单记录
            int result = orderMapper.update(order);
            if (result > 0) {
                // 更新成功
                return ApiResponse.success("订单更新成功", order.getId());
            } else {
                // 更新失败，可能订单不存在
                return ApiResponse.error("订单更新失败，订单可能不存在");
            }
        } catch (Exception e) {
            // 处理异常
            return ApiResponse.error("更新订单失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定订单
     * 使用事务确保数据一致性
     *
     * @param id 要删除的订单ID
     * @return 删除结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse delete(Long id) {
        try {
            // 删除订单记录
            int result = orderMapper.delete(id);
            if (result > 0) {
                // 删除成功
                return ApiResponse.success("订单删除成功", "订单删除成功");
            } else {
                // 删除失败，可能订单不存在
                return ApiResponse.error("订单删除失败，订单可能不存在");
            }
        } catch (Exception e) {
            // 处理异常
            return ApiResponse.error("删除订单失败：" + e.getMessage());
        }
    }
}