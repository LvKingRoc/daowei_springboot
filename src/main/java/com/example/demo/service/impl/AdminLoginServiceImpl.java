package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Admin;
import com.example.demo.entity.OperationLog;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.service.AdminLoginService;
import com.example.demo.service.OperationLogService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员登录服务实现类
 * 实现AdminLoginService接口，提供管理员身份验证相关的业务逻辑
 * 负责处理管理员登录请求，验证用户名和密码
 */
@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 管理员登录验证实现
     * 根据用户名查询管理员，验证密码是否匹配
     *
     * @param username 管理员用户名
     * @param password 管理员密码
     * @return 登录结果的API响应，成功则包含管理员信息和Token，失败则包含错误信息
     */
    @Override
    public ApiResponse login(String username, String password) {
        // 根据用户名查询管理员
        Admin admin = adminMapper.findByUsername(username);

        // 检查管理员是否存在
        if (admin == null) {
            return ApiResponse.error("用户名不存在");
        }

        // 验证密码
        if (!admin.getPassword().equals(password)) {
            return ApiResponse.error("密码错误");
        }

        // 更新 tokenVersion（单点登录：每次登录生成新版本号，旧 token 失效）
        Integer newTokenVersion = (admin.getTokenVersion() != null ? admin.getTokenVersion() : 0) + 1;
        adminMapper.updateTokenVersion(admin.getId(), newTokenVersion);

        // 生成 JWT Token（包含新版本号）
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin", newTokenVersion);

        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("name", admin.getName());
        data.put("token", token);
        data.put("role", "admin");

        // 记录登录日志
        saveLoginLog(admin.getId(), admin.getUsername(), "admin", true, "管理员登录成功");

        return ApiResponse.success("登录成功", data);
    }

    /**
     * 记录登录日志
     */
    private void saveLoginLog(Long userId, String username, String role, boolean success, String description) {
        try {
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setRole(role);
            log.setModule("系统登录");
            log.setAction("LOGIN");
            log.setDescription(description);
            log.setStatus(success ? 1 : 0);

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.setRequestUrl(request.getRequestURI());
                log.setRequestMethod(request.getMethod());
                log.setIpAddress(getIpAddress(request));
                log.setUserAgent(request.getHeader("User-Agent"));
            }

            operationLogService.save(log);
        } catch (Exception e) {
            // 日志保存失败不影响主业务
        }
    }

    /**
     * 获取真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}