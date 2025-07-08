package com.example.demo.controller;

import com.example.demo.entity.Dashboard;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制器
 * 处理仪表盘数据相关的请求
 * 提供获取系统概览数据的API接口
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    /**
     * 仪表盘服务接口
     * 注入DashboardService用于获取仪表盘数据
     */
    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘数据
     * 该方法返回系统的统计信息，如客户数量、订单数量、样板数量等
     * 
     * @return 包含统计数据的Dashboard对象
     */
    @GetMapping
    public Dashboard getDashboard() {
        // 调用服务获取仪表盘数据
        return dashboardService.getDashboard();
    }
}