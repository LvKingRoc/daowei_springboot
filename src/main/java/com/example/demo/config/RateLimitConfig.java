package com.example.demo.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求频率限制过滤器
 * 基于IP地址的简单限流实现
 */
@Component
public class RateLimitConfig extends OncePerRequestFilter {

    // IP请求计数器：IP -> 请求次数
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    // IP请求时间戳：IP -> 窗口开始时间
    private final Map<String, Long> requestTimestamps = new ConcurrentHashMap<>();

    // 时间窗口（毫秒）
    private static final long TIME_WINDOW = 60000; // 1分钟
    // 每个时间窗口内最大请求数
    private static final int MAX_REQUESTS = 100;
    // 登录接口限制（更严格）
    private static final int MAX_LOGIN_REQUESTS = 10;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String path = request.getRequestURI();
        
        // 登录接口使用更严格的限制
        boolean isLoginRequest = path.contains("/login");
        int maxRequests = isLoginRequest ? MAX_LOGIN_REQUESTS : MAX_REQUESTS;
        
        // 添加IP后缀区分不同类型的请求
        String key = isLoginRequest ? clientIp + ":login" : clientIp;

        long currentTime = System.currentTimeMillis();
        
        // 清理过期的计数器
        requestTimestamps.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > TIME_WINDOW * 2);
        requestCounts.entrySet().removeIf(entry -> 
            !requestTimestamps.containsKey(entry.getKey()));

        // 检查是否需要重置计数器
        Long windowStart = requestTimestamps.get(key);
        if (windowStart == null || currentTime - windowStart > TIME_WINDOW) {
            requestTimestamps.put(key, currentTime);
            requestCounts.put(key, new AtomicInteger(0));
        }

        // 增加请求计数
        AtomicInteger count = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();

        // 检查是否超过限制
        if (currentCount > maxRequests) {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"请求过于频繁，请稍后再试\",\"code\":429}");
            return;
        }

        // 添加限流相关响应头
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, maxRequests - currentCount)));
        response.setHeader("X-RateLimit-Reset", String.valueOf((windowStart != null ? windowStart : currentTime) + TIME_WINDOW));

        filterChain.doFilter(request, response);
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果有多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
