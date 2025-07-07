package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Order;

import java.util.List;

public interface OrderService {
    Order getById(Long id);

    List<Order> listByCondition();

    ApiResponse save(Order order);

    ApiResponse update(Order order);

    ApiResponse delete(Long id);
}