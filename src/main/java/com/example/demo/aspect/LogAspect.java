package com.example.demo.aspect;

import com.example.demo.annotation.Log;
import com.example.demo.entity.OperationLog;
import com.example.demo.mapper.*;
import com.example.demo.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志AOP切面
 * 自动记录被@Log注解标记的方法的操作日志
 * 支持记录修改前后的数据对比
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ObjectMapper objectMapper;

    // 注入各个Mapper用于获取修改前的数据
    @Autowired(required = false)
    private CustomerMapper customerMapper;

    @Autowired(required = false)
    private SampleMapper sampleMapper;

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Autowired(required = false)
    private EmployeeMapper employeeMapper;

    @Autowired(required = false)
    private UserMapper userMapper;

    /**
     * 定义切点：所有被@Log注解标记的方法
     */
    @Pointcut("@annotation(com.example.demo.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 环绕通知：记录方法执行前后的信息
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        OperationLog operationLog = new OperationLog();
        Object result = null;
        Object oldData = null;
        Object newData = null;
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        String action = logAnnotation.action();
        String entityType = logAnnotation.entityType();
        int idParamIndex = logAnnotation.idParamIndex();
        
        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 从请求属性中获取用户信息（JWT过滤器设置的）
                Long userId = (Long) request.getAttribute("userId");
                String operatorName = (String) request.getAttribute("name");  // 操作人姓名
                String role = (String) request.getAttribute("role");
                
                operationLog.setUserId(userId);
                operationLog.setOperatorName(operatorName);
                operationLog.setRole(role);
                operationLog.setRequestUrl(request.getRequestURI());
                operationLog.setRequestMethod(request.getMethod());
                operationLog.setIpAddress(getIpAddress(request));
                operationLog.setUserAgent(request.getHeader("User-Agent"));
                
                // 获取请求参数（过滤不可序列化的对象）
                Object[] args = point.getArgs();
                try {
                    if (args != null && args.length > 0) {
                        Object[] filteredArgs = filterNonSerializableArgs(args);
                        String params = objectMapper.writeValueAsString(filteredArgs);
                        if (params.length() > 2000) {
                            params = params.substring(0, 2000) + "...";
                        }
                        operationLog.setRequestParams(params);
                    }
                } catch (Exception e) {
                    operationLog.setRequestParams("参数解析失败");
                }
                
                // UPDATE/DELETE 操作：获取修改前的数据
                if (("UPDATE".equals(action) || "DELETE".equals(action)) 
                        && entityType != null && !entityType.isEmpty()
                        && args != null && args.length > idParamIndex) {
                    try {
                        Object idParam = args[idParamIndex];
                        Long entityId = null;
                        if (idParam instanceof Long) {
                            entityId = (Long) idParam;
                        } else if (idParam instanceof Integer) {
                            entityId = ((Integer) idParam).longValue();
                        } else if (idParam instanceof String) {
                            entityId = Long.parseLong((String) idParam);
                        }
                        
                        if (entityId != null) {
                            oldData = getEntityById(entityType, entityId);
                            if (oldData != null) {
                                String oldDataStr = objectMapper.writeValueAsString(oldData);
                                if (oldDataStr.length() > 5000) {
                                    oldDataStr = oldDataStr.substring(0, 5000) + "...";
                                }
                                operationLog.setOldData(oldDataStr);
                            }
                        }
                    } catch (Exception e) {
                        operationLog.setOldData("获取原数据失败: " + e.getMessage());
                    }
                }
                
                // UPDATE 操作：记录新数据（从请求参数中获取）
                if ("UPDATE".equals(action) && args != null) {
                    try {
                        // 通常新数据在请求体中，尝试找到实体对象
                        for (Object arg : args) {
                            if (arg != null && !isPrimitiveOrWrapper(arg.getClass())) {
                                String newDataStr = objectMapper.writeValueAsString(arg);
                                if (newDataStr.length() > 5000) {
                                    newDataStr = newDataStr.substring(0, 5000) + "...";
                                }
                                operationLog.setNewData(newDataStr);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        operationLog.setNewData("获取新数据失败: " + e.getMessage());
                    }
                }
            }
            
            operationLog.setModule(logAnnotation.module());
            operationLog.setAction(action);
            operationLog.setDescription(logAnnotation.description());
            operationLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
            
            // 执行目标方法
            result = point.proceed();
            
            // 记录成功
            operationLog.setStatus(1);
            try {
                String responseStr = objectMapper.writeValueAsString(result);
                if (responseStr.length() > 2000) {
                    responseStr = responseStr.substring(0, 2000) + "...";
                }
                operationLog.setResponseData(responseStr);
            } catch (Exception e) {
                operationLog.setResponseData("响应解析失败");
            }
            
        } catch (Throwable e) {
            // 记录失败
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时长
            long endTime = System.currentTimeMillis();
            operationLog.setDuration(endTime - startTime);
            
            // 异步保存日志
            operationLogService.save(operationLog);
        }
        
        return result;
    }

    /**
     * 根据实体类型和ID获取实体数据
     */
    private Object getEntityById(String entityType, Long id) {
        switch (entityType.toLowerCase()) {
            case "customer":
                return customerMapper != null ? customerMapper.getById(id) : null;
            case "sample":
                return sampleMapper != null ? sampleMapper.selectById(id) : null;
            case "order":
                return orderMapper != null ? orderMapper.getById(id) : null;
            case "employee":
                return employeeMapper != null ? employeeMapper.findById(id.intValue()) : null;
            case "user":
                return userMapper != null ? userMapper.findById(id) : null;
            default:
                return null;
        }
    }

    /**
     * 过滤不可序列化的参数（如 MultipartFile、HttpServletRequest 等）
     */
    private Object[] filterNonSerializableArgs(Object[] args) {
        Object[] filtered = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                filtered[i] = null;
            } else if (arg instanceof org.springframework.web.multipart.MultipartFile) {
                // MultipartFile 不可序列化，只记录文件名和大小
                org.springframework.web.multipart.MultipartFile file = 
                    (org.springframework.web.multipart.MultipartFile) arg;
                filtered[i] = "[File: " + file.getOriginalFilename() + ", size: " + file.getSize() + " bytes]";
            } else if (arg instanceof javax.servlet.http.HttpServletRequest) {
                filtered[i] = "[HttpServletRequest]";
            } else if (arg instanceof javax.servlet.http.HttpServletResponse) {
                filtered[i] = "[HttpServletResponse]";
            } else if (arg instanceof java.io.InputStream) {
                filtered[i] = "[InputStream]";
            } else if (arg instanceof java.io.OutputStream) {
                filtered[i] = "[OutputStream]";
            } else {
                filtered[i] = arg;
            }
        }
        return filtered;
    }

    /**
     * 判断是否为基本类型或包装类型
     */
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Long.class ||
                clazz == Integer.class ||
                clazz == Short.class ||
                clazz == Byte.class ||
                clazz == Double.class ||
                clazz == Float.class ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == String.class;
    }

    /**
     * 获取真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
