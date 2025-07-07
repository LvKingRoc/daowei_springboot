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

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public Dashboard getDashboard() {
        Dashboard dashboard = new Dashboard();

        // 顶部数据卡片
        dashboard.setTotalPendingAmount(dashboardMapper.getTotalPendingAmount().doubleValue());
        dashboard.setRecentOrderCount(dashboardMapper.getRecentOrderCount());
        dashboard.setTotalCustomerCount(dashboardMapper.getTotalCustomerCount());
        dashboard.setTotalSampleCount(dashboardMapper.getTotalSampleCount());

        // 订单状态分布 - 仅转换状态名称，不设置颜色
        List<Map<String, Object>> statusDistribution = dashboardMapper.getOrderStatusDistribution();
        List<Map<String, Object>> formattedStatus = statusDistribution.stream()
            .map(item -> {
                Map<String, Object> newItem = new HashMap<>();
                newItem.put("name", convertStatusName(item.get("name").toString()));
                newItem.put("value", item.get("value"));
                return newItem;
            })
            .collect(Collectors.toList());
        dashboard.setStatusDistribution(formattedStatus);

        // 订单产品列表
        dashboard.setPendingProducts(dashboardMapper.getPendingProducts());
        dashboard.setProducingProducts(dashboardMapper.getProducingProducts());
        dashboard.setShippingProducts(dashboardMapper.getShippingProducts());

        // 员工通讯录
        dashboard.setEmployees(dashboardMapper.getEmployees());

        return dashboard;
    }

    // 状态名称转换
    private String convertStatusName(String status) {
        switch (status.toUpperCase()) {
            case "PENDING": return "待生产";
            case "IN_PROGRESS":
            case "PRODUCING": return "生产中";
            case "SHIPPED":
            case "READY_TO_SHIP": return "待发货";
            case "AWAITING_PAYMENT": return "待收款";
            case "COMPLETED": return "已完成";
            default: return status;
        }
    }
}