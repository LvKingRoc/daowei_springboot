package com.example.demo.entity;

import java.util.Date;

/**
 * 操作日志实体类
 */
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }
    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }
    public String getRequestParams() { return requestParams; }
    public void setRequestParams(String requestParams) { this.requestParams = requestParams; }
    public String getOldData() { return oldData; }
    public void setOldData(String oldData) { this.oldData = oldData; }
    public String getNewData() { return newData; }
    public void setNewData(String newData) { this.newData = newData; }
    public String getResponseData() { return responseData; }
    public void setResponseData(String responseData) { this.responseData = responseData; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
