package com.example.demo.service.impl;

import com.example.demo.entity.Order;
import com.example.demo.exception.BusinessException;
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
        return orderMapper.listAll();
    }

    /**
     * 保存新订单
     * 使用事务确保数据一致性
     *
     * @param order 要保存的订单对象
     * @return 保存后的订单对象（包含生成的ID）
     */
    @Override
    @Transactional
    public Order save(Order order) {
        try {
            orderMapper.insert(order);
            return getById(order.getId());
        } catch (DuplicateKeyException e) {
            throw new BusinessException("订单号 '" + order.getOrderNumber() + "' 已存在，请使用其他订单号");
        }
    }

    /**
     * 更新现有订单信息
     * 使用事务确保数据一致性
     *
     * @param order 更新后的订单对象
     * @return 更新后的订单对象
     */
    @Override
    @Transactional
    public Order update(Order order) {
        if (order.getId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        Order existing = orderMapper.getById(order.getId());
        if (existing == null) {
            throw new BusinessException("订单不存在", 404);
        }
        int result = orderMapper.update(order);
        if (result <= 0) {
            throw new BusinessException("订单更新失败");
        }
        return getById(order.getId());
    }

    /**
     * 删除指定订单
     * 使用事务确保数据一致性
     *
     * @param id 要删除的订单ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Order existing = orderMapper.getById(id);
        if (existing == null) {
            throw new BusinessException("订单不存在", 404);
        }
        int result = orderMapper.delete(id);
        if (result <= 0) {
            throw new BusinessException("订单删除失败");
        }
    }
}