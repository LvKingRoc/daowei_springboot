package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.service.AdminLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public ApiResponse login(String username, String password) {
        // 查询用户
        Admin admin = adminMapper.findByUsername(username);

        if (admin == null) {
            return ApiResponse.error("用户名不存在");
        }

        // 验证密码（未加密，直接比对）
        if (!admin.getPassword().equals(password)) {
            return ApiResponse.error("密码错误");
        }

        // 登录成功
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("token", "admin_token_" + admin.getId()); // 模拟token
        adminData.put("user", admin);

        return ApiResponse.success("登录成功", adminData);
    }
}