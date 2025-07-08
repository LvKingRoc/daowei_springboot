package com.example.demo.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一API响应格式类
 * 用于规范化处理API返回的数据结构，包含请求处理结果、提示信息、返回数据和状态码
 * 提供了多种静态工厂方法，方便创建成功或错误的响应对象
 */
public class ApiResponse {
    /**
     * 请求是否成功的标志
     */
    private boolean success;
    
    /**
     * 响应的提示信息
     */
    private String message;
    
    /**
     * 响应的数据载荷
     */
    private Object data;
    
    /**
     * HTTP状态码
     */
    private Integer code;

    /**
     * 无参构造函数
     */
    public ApiResponse() {
    }

    /**
     * 完整参数的构造函数
     *
     * @param success 是否成功
     * @param message 提示信息
     * @param data 响应数据
     * @param code HTTP状态码
     */
    public ApiResponse(boolean success, String message, Object data, Integer code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    /**
     * 创建成功响应，仅包含数据
     *
     * @param data 响应数据
     * @return 成功的API响应对象
     */
    public static ApiResponse success(Object data) {
        return new ApiResponse(true, null, data, 200);
    }

    /**
     * 创建成功响应，包含消息和数据
     *
     * @param message 成功提示消息
     * @param data 响应数据
     * @return 成功的API响应对象
     */
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data, 200);
    }

    /**
     * 创建错误响应，仅包含错误消息，状态码默认为400
     *
     * @param message 错误提示消息
     * @return 错误的API响应对象
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null, 400);
    }

    /**
     * 创建错误响应，包含错误消息和指定状态码
     *
     * @param message 错误提示消息
     * @param code HTTP状态码
     * @return 错误的API响应对象
     */
    public static ApiResponse error(String message, Integer code) {
        return new ApiResponse(false, message, null, code);
    }

    /**
     * 创建错误响应，包含错误消息、数据和状态码
     *
     * @param message 错误提示消息
     * @param data 错误相关数据
     * @param code HTTP状态码
     * @return 错误的API响应对象
     */
    public static ApiResponse error(String message, Object data, Integer code) {
        return new ApiResponse(false, message, data, code);
    }

    // Getters and Setters
    /**
     * 获取请求是否成功
     *
     * @return 成功状态
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置请求是否成功
     *
     * @param success 成功状态
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     *
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 获取HTTP状态码
     *
     * @return HTTP状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置HTTP状态码
     *
     * @param code HTTP状态码
     */
    public void setCode(Integer code) {
        this.code = code;
    }
} 