package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 操作日志实体类
 */
@Data
public class OperationLog {
    private Long id;
    private Long userId;
    private String operatorName;  // 操作人姓名
    private String role;
    private String module;
    private String action;
    private String description;
    private String method;
    private String requestUrl;
    private String requestMethod;
    private String requestParams;
    private String oldData;      // 修改前数据
    private String newData;      // 修改后数据
    private String responseData;
    private String ipAddress;
    private String userAgent;
    private Integer status;  // 1成功 0失败
    private String errorMsg;
    private Long duration;  // 执行时长(毫秒)
    private Date createTime;
}
