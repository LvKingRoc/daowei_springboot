package com.example.demo.service;

import com.example.demo.entity.Dashboard;

/**
 * 仪表盘服务接口
 * 提供系统概览数据相关的业务逻辑
 * 用于获取系统统计信息
 */
public interface DashboardService {
    
    /**
     * 获取系统仪表盘数据
     * 包括客户数量、样品数量、订单数量等系统概览数据
     * 
     * @return 仪表盘数据对象
     */
    Dashboard getDashboard();
}