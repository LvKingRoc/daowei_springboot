package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.service.AdminLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 管理员登录服务实现类
 * 实现AdminLoginService接口，提供管理员身份验证相关的业务逻辑
 * 负责处理管理员登录请求，验证用户名和密码
 */
@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    /**
     * 管理员数据访问对象
     * 用于从数据库中查询管理员信息
     */
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登录验证实现
     * 根据用户名查询管理员，验证密码是否匹配
     *
     * @param username 管理员用户名
     * @param password 管理员密码
     * @return 登录结果的API响应，成功则包含管理员信息，失败则包含错误信息
     */
    @Override
    public ApiResponse login(String username, String password) {
        // 根据用户名查询管理员
        Admin admin = adminMapper.findByUsername(username);

        // 检查管理员是否存在
        if (admin == null) {
            return ApiResponse.error("用户名不存在");
        }

        // 验证密码（未加密，直接比对）
        if (!admin.getPassword().equals(password)) {
            return ApiResponse.error("密码错误");
        }

        // 登录成功，返回管理员信息
        return ApiResponse.success("登录成功", admin);
    }
}