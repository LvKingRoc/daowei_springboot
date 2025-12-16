package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理 Token 校验和刷新
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 校验 Token 是否有效
     * 如果有效，返回用户角色信息，并刷新 Token
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.ok(ApiResponse.error("Token不存在"));
            }
            
            String token = authHeader.substring(7);
            
            // 验证 Token 基本有效性
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.ok(ApiResponse.error("Token无效或已过期"));
            }
            
            // 获取用户信息
            String role = jwtUtil.getRole(token);
            Long userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            Integer tokenVersion = jwtUtil.getTokenVersion(token);
            
            // 验证 Token 版本号（单点登录检查）
            Integer dbTokenVersion = null;
            if ("admin".equals(role)) {
                Admin admin = adminMapper.findById(userId);
                if (admin == null) {
                    return ResponseEntity.ok(ApiResponse.error("用户不存在", 4012));
                }
                dbTokenVersion = admin.getTokenVersion();
            } else {
                User user = userMapper.findById(userId);
                if (user == null) {
                    return ResponseEntity.ok(ApiResponse.error("用户不存在", 4012));
                }
                dbTokenVersion = user.getTokenVersion();
            }
            
            // 版本号不匹配，说明账号在其他设备登录
            if (dbTokenVersion != null && !dbTokenVersion.equals(tokenVersion)) {
                return ResponseEntity.ok(ApiResponse.error("账号已在其他设备登录，请重新登录", 4011));
            }
            
            // 刷新 Token（滑动过期）
            String newToken = jwtUtil.refreshToken(token);
            
            Map<String, Object> data = new HashMap<>();
            data.put("role", role);
            data.put("userId", userId);
            data.put("username", username);
            data.put("newToken", newToken);
            
            return ResponseEntity.ok(ApiResponse.success("Token有效", data));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Token校验失败: " + e.getMessage()));
        }
    }
}
