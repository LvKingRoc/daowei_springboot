package com.example.demo.entity;

import java.util.List;
import java.util.Map;

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

    public Double getTotalPendingAmount() { return totalPendingAmount; }
    public void setTotalPendingAmount(Double totalPendingAmount) { this.totalPendingAmount = totalPendingAmount; }
    public Integer getRecentOrderCount() { return recentOrderCount; }
    public void setRecentOrderCount(Integer recentOrderCount) { this.recentOrderCount = recentOrderCount; }
    public Integer getTotalCustomerCount() { return totalCustomerCount; }
    public void setTotalCustomerCount(Integer totalCustomerCount) { this.totalCustomerCount = totalCustomerCount; }
    public Integer getTotalSampleCount() { return totalSampleCount; }
    public void setTotalSampleCount(Integer totalSampleCount) { this.totalSampleCount = totalSampleCount; }
    public List<Map<String, Object>> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(List<Map<String, Object>> statusDistribution) { this.statusDistribution = statusDistribution; }
    public List<OrderProduct> getPendingProducts() { return pendingProducts; }
    public void setPendingProducts(List<OrderProduct> pendingProducts) { this.pendingProducts = pendingProducts; }
    public List<OrderProduct> getProducingProducts() { return producingProducts; }
    public void setProducingProducts(List<OrderProduct> producingProducts) { this.producingProducts = producingProducts; }
    public List<OrderProduct> getShippingProducts() { return shippingProducts; }
    public void setShippingProducts(List<OrderProduct> shippingProducts) { this.shippingProducts = shippingProducts; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public static class OrderProduct {
        private String orderNumber;
        private String model;
        private String color;
        private Integer quantity;
        private String customer;

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getCustomer() { return customer; }
        public void setCustomer(String customer) { this.customer = customer; }
    }

    public static class Employee {
        private String name;
        private String phone;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}