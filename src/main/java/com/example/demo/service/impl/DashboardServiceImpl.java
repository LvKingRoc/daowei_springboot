package com.example.demo.service.impl;

import com.example.demo.entity.Dashboard;
import com.example.demo.mapper.DashboardMapper;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 * 实现DashboardService接口，提供系统概览数据相关的业务逻辑
 * 负责整合各类统计数据，为前端展示提供数据支持
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    /**
     * 仪表盘数据访问对象
     * 用于从数据库中查询各类统计数据
     */
    @Autowired
    private DashboardMapper dashboardMapper;

    /**
     * 获取系统仪表盘数据
     * 整合各类统计数据，包括待处理金额、近期订单数量、客户数量、样品数量、
     * 订单状态分布、待处理产品、生产中产品、待发货产品以及员工通讯录
     *
     * @return 包含全部仪表盘数据的Dashboard对象
     */
    @Override
    public Dashboard getDashboard() {
        Dashboard dashboard = new Dashboard();

        // 获取顶部数据卡片信息
        dashboard.setTotalPendingAmount(dashboardMapper.getTotalPendingAmount().doubleValue());  // 待处理金额
        dashboard.setRecentOrderCount(dashboardMapper.getRecentOrderCount());  // 近期订单数量
        dashboard.setTotalCustomerCount(dashboardMapper.getTotalCustomerCount());  // 总客户数量
        dashboard.setTotalSampleCount(dashboardMapper.getTotalSampleCount());  // 总样品数量

        // 获取订单状态分布数据并转换状态名称为中文
        List<Map<String, Object>> statusDistribution = dashboardMapper.getOrderStatusDistribution();
        List<Map<String, Object>> formattedStatus = statusDistribution.stream()
            .map(item -> {
                Map<String, Object> newItem = new HashMap<>();
                newItem.put("name", convertStatusName(item.get("name").toString()));  // 转换状态名称
                newItem.put("value", item.get("value"));  // 保持值不变
                return newItem;
            })
            .collect(Collectors.toList());
        dashboard.setStatusDistribution(formattedStatus);

        // 获取各类状态的产品列表
        dashboard.setPendingProducts(dashboardMapper.getPendingProducts());  // 待生产产品
        dashboard.setProducingProducts(dashboardMapper.getProducingProducts());  // 生产中产品
        dashboard.setShippingProducts(dashboardMapper.getShippingProducts());  // 待发货产品

        // 获取员工通讯录
        dashboard.setEmployees(dashboardMapper.getEmployees());

        return dashboard;
    }

    /**
     * 订单状态名称转换
     * 将英文状态名称转换为中文，方便前端展示
     *
     * @param status 英文状态名称
     * @return 转换后的中文状态名称
     */
    private String convertStatusName(String status) {
        switch (status.toUpperCase()) {
            case "PENDING": return "待生产";
            case "IN_PROGRESS":
            case "PRODUCING": return "生产中";
            case "SHIPPED":
            case "READY_TO_SHIP": return "待发货";
            case "AWAITING_PAYMENT": return "待收款";
            case "COMPLETED": return "已完成";
            default: return status;  // 未知状态返回原状态名
        }
    }
}