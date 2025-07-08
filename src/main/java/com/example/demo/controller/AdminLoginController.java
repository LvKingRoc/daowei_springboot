package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.LoginRequest;
import com.example.demo.service.AdminLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员登录控制器
 * 处理管理员登录相关的HTTP请求
 * 提供登录验证等功能
 */
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    /**
     * 管理员登录服务
     * 负责处理管理员登录的业务逻辑
     */
    @Autowired
    private AdminLoginService adminLoginService;

    /**
     * 处理管理员登录请求
     * 
     * @param request 包含用户名和密码的登录请求对象
     * @return 登录结果的HTTP响应，成功返回200 OK，失败返回400 Bad Request
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        // 调用登录服务进行验证
        ApiResponse response = adminLoginService.login(request.getUsername(), request.getPassword());
        // 根据登录结果返回不同的HTTP状态码
        return response.isSuccess() ? 
            ResponseEntity.ok(response) :  // 登录成功返回200 OK
            ResponseEntity.badRequest().body(response);  // 登录失败返回400 Bad Request
    }
}