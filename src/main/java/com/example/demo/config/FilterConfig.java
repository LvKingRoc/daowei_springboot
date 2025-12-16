package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置类
 * 注册限流过滤器和JWT认证过滤器
 */
@Configuration
public class FilterConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    /**
     * 注册限流过滤器（优先级最高）
     */
    @Bean
    public FilterRegistrationBean<RateLimitConfig> rateLimitFilterRegistration() {
        FilterRegistrationBean<RateLimitConfig> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitConfig);
        registration.addUrlPatterns("/api/*");
        registration.setName("rateLimitFilter");
        registration.setOrder(0);  // 最高优先级
        return registration;
    }

    /**
     * 注册JWT认证过滤器
     */
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtFilter);
        registration.addUrlPatterns("/api/*");
        registration.setName("jwtFilter");
        registration.setOrder(1);
        return registration;
    }
}
