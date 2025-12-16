package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 在Controller方法上使用，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    String action() default "";

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 实体类型（用于UPDATE/DELETE时获取原数据）
     * 如 "customer", "sample", "order", "employee", "user"
     */
    String entityType() default "";

    /**
     * ID参数位置（从0开始，用于获取原数据）
     * 默认为0，表示第一个参数是ID
     */
    int idParamIndex() default 0;
}
