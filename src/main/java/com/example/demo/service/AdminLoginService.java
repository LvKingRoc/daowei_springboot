package com.example.demo.service;

import com.example.demo.common.ApiResponse;

/**
 * 管理员登录服务接口
 * 提供管理员身份验证相关的业务逻辑
 */
public interface AdminLoginService {
    
    /**
     * 管理员登录验证
     * 
     * @param username 管理员用户名
     * @param password 管理员密码
     * @return 登录结果的API响应，成功则包含管理员信息和token，失败则包含错误信息
     */
    ApiResponse login(String username, String password);
}