package com.example.demo.config;

import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT 认证过滤器
 * 拦截请求，验证 Token 有效性
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    // Token版本失效错误码（账号在其他设备登录）
    private static final int TOKEN_VERSION_INVALID = 4011;

    // 不需要认证的路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/admin/login",
            "/api/user/login",
            "/api/auth/verify",  // Token 校验接口
            "/api/notifications/subscribe",  // SSE推送（EventSource不支持自定义Header）
            "/sample/"  // 图片资源
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 放行OPTIONS请求（CORS预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 检查是否是排除路径
        boolean isExcluded = EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
        if (isExcluded) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取 Authorization 头
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "未提供认证Token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 验证 Token 基本有效性
            if (!jwtUtil.validateToken(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token无效或已过期");
                return;
            }

            // 获取 Token 中的信息
            Long userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            Integer tokenVersion = jwtUtil.getTokenVersion(token);

            // 验证 Token 版本号（单点登录检查）
            Integer dbTokenVersion = null;
            String operatorName = null;  // 操作人姓名
            if ("admin".equals(role)) {
                Admin admin = adminMapper.findById(userId);
                if (admin == null) {
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "用户不存在或已被删除");
                    return;
                }
                dbTokenVersion = admin.getTokenVersion();
                operatorName = admin.getName();
            } else {
                User user = userMapper.findById(userId);
                if (user == null) {
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "用户不存在或已被删除");
                    return;
                }
                dbTokenVersion = user.getTokenVersion();
                operatorName = user.getName();
            }

            // 版本号不匹配，说明账号在其他设备登录
            if (!dbTokenVersion.equals(tokenVersion)) {
                sendError(response, TOKEN_VERSION_INVALID, "账号已在其他设备登录，请重新登录");
                return;
            }

            // 将用户信息存入请求属性，供后续使用
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("name", operatorName);  // 操作人姓名
            request.setAttribute("role", role);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        error.put("code", status);
        error.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
