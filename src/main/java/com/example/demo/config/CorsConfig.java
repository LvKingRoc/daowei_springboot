package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 用于解决前后端分离项目中的跨域请求问题
 * 实现WebMvcConfigurer接口，覆盖其中的addCorsMappings方法以自定义CORS策略
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 配置全局的CORS(跨域资源共享)规则
     * 
     * @param registry CorsRegistry对象，用于注册CORS配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 对所有路径应用CORS配置
                .allowedOriginPatterns("*")  // 允许所有来源的跨域请求（使用模式匹配替代allowedOrigins）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP请求方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true);  // 允许发送认证信息（cookies等）
    }
}