package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * SpringBoot应用程序的入口类
 * 使用@SpringBootApplication注解标记该类为应用程序的启动类
 * 该注解是@Configuration、@EnableAutoConfiguration和@ComponentScan的组合注解
 */
@SpringBootApplication
@EnableAsync  // 启用异步支持，用于日志异步写入
public class Application {
    /**
     * 应用程序的主方法，是程序的入口点
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动SpringBoot应用，传入当前类作为主配置类和命令行参数
        SpringApplication.run(Application.class, args);
    }
}