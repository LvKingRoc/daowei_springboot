package com.example.demo.exception;

/**
 * 业务异常类
 * 用于封装业务逻辑中的异常情况，包含错误消息和错误码
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final int code;
    
    /**
     * 构造函数 - 仅消息
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    /**
     * 构造函数 - 消息和错误码
     * @param message 错误消息
     * @param code 错误码
     */
    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造函数 - 消息和原因
     * @param message 错误消息
     * @param cause 原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }
    
    /**
     * 构造函数 - 消息、错误码和原因
     * @param message 错误消息
     * @param code 错误码
     * @param cause 原因
     */
    public BusinessException(String message, int code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
