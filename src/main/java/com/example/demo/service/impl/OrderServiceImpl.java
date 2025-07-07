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

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order getById(Long id) {
        return orderMapper.getById(id);
    }

    @Override
    public List<Order> listByCondition() {
        return orderMapper.listAll();
    }

    @Override
    @Transactional
    public ApiResponse save(Order order) {
        try {
            orderMapper.insert(order);
            return ApiResponse.success("订单创建成功", order.getId());
        } catch (DuplicateKeyException e) {
            return ApiResponse.error("订单号 '" + order.getOrderNumber() + "' 已存在，请使用其他订单号");
        } catch (Exception e) {
            return ApiResponse.error("创建订单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse update(Order order) {
        try {
            int result = orderMapper.update(order);
            if (result > 0) {
                return ApiResponse.success("订单更新成功", order.getId());
            } else {
                return ApiResponse.error("订单更新失败，订单可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新订单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse delete(Long id) {
        try {
            int result = orderMapper.delete(id);
            if (result > 0) {
                return ApiResponse.success("订单删除成功", "订单删除成功");
            } else {
                return ApiResponse.error("订单删除失败，订单可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("删除订单失败：" + e.getMessage());
        }
    }
}