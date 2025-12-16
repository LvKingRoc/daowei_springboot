package com.example.demo.controller;

import com.example.demo.annotation.Log;
import com.example.demo.common.ApiResponse;
import com.example.demo.entity.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户登录与管理控制器
 * 处理用户登录、查询、添加、更新和删除等操作
 * 提供用户相关的REST API接口
 */
@RestController
@RequestMapping("/api/user")
public class UserLoginController {

    /**
     * 用户登录服务接口
     * 处理用户登录和用户管理的业务逻辑
     */
    @Autowired
    private UserLoginService userLoginService;

    /**
     * 用户登录接口
     * 
     * @param request 包含用户名和密码的登录请求对象
     * @return 登录结果的HTTP响应，成功返回200 OK，失败返回400 Bad Request
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        // 调用登录服务进行身份验证
        ApiResponse response = userLoginService.login(request.getUsername(), request.getPassword());
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : // 登录成功返回200 OK
            ResponseEntity.badRequest().body(response); // 登录失败返回400 Bad Request
    }

    /**
     * 查询所有用户信息
     * 
     * @return 包含所有用户列表的HTTP响应
     */
    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse> findAll() {
        // 调用服务获取所有用户
        List<User> users = userLoginService.findAll();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * 添加新用户
     * 
     * @param user 用户信息对象
     * @return 添加结果的HTTP响应，包含成功信息或错误信息
     */
    @Log(module = "用户管理", action = "CREATE", description = "添加用户")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addUser(@RequestBody User user) {
        // 调用服务添加用户
        ApiResponse response = userLoginService.insertUser(user);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : // 添加成功返回200 OK
            ResponseEntity.badRequest().body(response); // 添加失败返回400 Bad Request
    }

    /**
     * 更新用户信息
     * 
     * @param user 更新后的用户信息对象
     * @return 更新结果的HTTP响应，包含成功信息或错误信息
     */
    @Log(module = "用户管理", action = "UPDATE", description = "更新用户")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody User user) {
        // 调用服务更新用户
        ApiResponse response = userLoginService.updateUser(user);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : // 更新成功返回200 OK
            ResponseEntity.badRequest().body(response); // 更新失败返回400 Bad Request
    }

    /**
     * 删除用户
     * 
     * @param id 要删除的用户ID
     * @return 删除结果的HTTP响应，包含成功信息或错误信息
     */
    @Log(module = "用户管理", action = "DELETE", description = "删除用户", entityType = "user", idParamIndex = 0)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        // 调用服务删除用户
        ApiResponse response = userLoginService.deleteUser(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : // 删除成功返回200 OK
            ResponseEntity.badRequest().body(response); // 删除失败返回400 Bad Request
    }
}