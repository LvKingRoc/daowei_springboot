package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class Dashboard {
    // 顶部卡片数据
    private Double totalPendingAmount;
    private Integer recentOrderCount;
    private Integer totalCustomerCount;
    private Integer totalSampleCount;

    // 订单状态分布（不包含颜色信息）
    private List<Map<String, Object>> statusDistribution;

    // 订单产品列表
    private List<OrderProduct> pendingProducts;
    private List<OrderProduct> producingProducts;
    private List<OrderProduct> shippingProducts;

    // 员工通讯录
    private List<Employee> employees;

    @Data
    public static class OrderProduct {
        private String orderNumber;
        private String model;
        private String color;
        private Integer quantity;
        private String customer;
    }

    @Data
    public static class Employee {
        private String name;
        private String phone;
    }
}