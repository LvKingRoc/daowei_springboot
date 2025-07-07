package com.example.demo.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一API响应格式
 */
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private Integer code;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, Object data, Integer code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(true, null, data, 200);
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data, 200);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null, 400);
    }

    public static ApiResponse error(String message, Integer code) {
        return new ApiResponse(false, message, null, code);
    }

    public static ApiResponse error(String message, Object data, Integer code) {
        return new ApiResponse(false, message, data, code);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
} 