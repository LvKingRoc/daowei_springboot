package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.User;

import java.util.List;

/**
 * 用户登录与管理服务接口
 * 提供用户登录验证、用户信息管理相关的业务逻辑
 * 包括用户的身份验证、查询、添加、更新和删除等操作
 */
public interface UserLoginService {
    
    /**
     * 用户登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录结果的API响应，成功则包含用户信息和token，失败则包含错误信息
     */
    ApiResponse login(String username, String password);
    
    /**
     * 查询所有用户信息
     * 
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 添加新用户
     * 
     * @param user 要添加的用户对象
     * @return 添加结果的API响应
     */
    ApiResponse insertUser(User user);
    
    /**
     * 更新现有用户信息
     * 
     * @param user 更新后的用户对象
     * @return 更新结果的API响应
     */
    ApiResponse updateUser(User user);
    
    /**
     * 删除指定用户
     * 
     * @param id 要删除的用户ID
     * @return 删除结果的API响应
     */
    ApiResponse deleteUser(Long id);
}