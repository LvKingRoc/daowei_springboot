package com.example.demo.exception;

import com.example.demo.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理Controller层抛出的异常，返回标准化的API响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return ResponseEntity.ok(ApiResponse.error(e.getMessage(), e.getCode()));
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse("参数校验失败");
        logger.warn("参数校验异常: {}", message);
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }
    
    /**
     * 处理唯一键冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse> handleDuplicateKeyException(DuplicateKeyException e) {
        logger.warn("唯一键冲突: {}", e.getMessage());
        return ResponseEntity.ok(ApiResponse.error("数据已存在，请检查是否重复"));
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        logger.error("系统异常", e);
        return ResponseEntity.status(500).body(ApiResponse.error("系统繁忙，请稍后重试"));
    }
}
