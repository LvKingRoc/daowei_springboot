package com.example.demo.mapper;

import com.example.demo.entity.Dashboard;
import org.apache.ibatis.annotations.Mapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {
    // 获取总待收款金额
    BigDecimal getTotalPendingAmount();

    // 获取近30天订单数
    Integer getRecentOrderCount();

    // 获取总客户数
    Integer getTotalCustomerCount();

    // 获取总样品数
    Integer getTotalSampleCount();

    // 获取订单状态分布
    List<Map<String, Object>> getOrderStatusDistribution();

    // 获取待生产订单产品
    List<Dashboard.OrderProduct> getPendingProducts();

    // 获取生产中订单产品
    List<Dashboard.OrderProduct> getProducingProducts();

    // 获取待发货订单产品
    List<Dashboard.OrderProduct> getShippingProducts();

    // 获取员工通讯录
    List<Dashboard.Employee> getEmployees();
}