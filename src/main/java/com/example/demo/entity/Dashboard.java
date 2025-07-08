package com.example.demo.entity;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class Dashboard {
    private Double totalPendingAmount;
    private Integer recentOrderCount;
    private Integer totalCustomerCount;
    private Integer totalSampleCount;

    private List<Map<String, Object>> statusDistribution;

    private List<OrderProduct> pendingProducts;
    private List<OrderProduct> producingProducts;
    private List<OrderProduct> shippingProducts;

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