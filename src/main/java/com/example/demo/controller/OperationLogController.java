package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.OperationLog;
import com.example.demo.service.OperationLogService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 * 提供日志查询相关的REST API接口
 */
@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询日志
     * 
     * @param pageNum  页码（默认1）
     * @param pageSize 每页数量（默认20）
     * @return 分页日志数据
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        PageInfo<OperationLog> pageInfo = operationLogService.findByPage(pageNum, pageSize);
        return ResponseEntity.ok(ApiResponse.success(pageInfo));
    }

    /**
     * 条件查询日志
     * 
     * @param username  用户名（模糊查询）
     * @param module    模块名称
     * @param action    操作类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 分页日志数据
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        PageInfo<OperationLog> pageInfo = operationLogService.findByCondition(
                username, module, action, startTime, endTime, pageNum, pageSize
        );
        return ResponseEntity.ok(ApiResponse.success(pageInfo));
    }

    /**
     * 查询日志详情
     * 
     * @param id 日志ID
     * @return 日志详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLogDetail(@PathVariable Long id) {
        OperationLog log = operationLogService.findById(id);
        if (log != null) {
            return ResponseEntity.ok(ApiResponse.success(log));
        }
        return ResponseEntity.ok(ApiResponse.error("日志不存在", 404));
    }

    /**
     * 清理旧日志
     * 
     * @param days 保留天数（删除该天数之前的日志）
     * @return 删除的日志数量
     */
    @DeleteMapping("/clean")
    public ResponseEntity<ApiResponse> cleanOldLogs(@RequestParam(defaultValue = "30") int days) {
        int count = operationLogService.cleanOldLogs(days);
        return ResponseEntity.ok(ApiResponse.success("已清理 " + count + " 条日志", count));
    }

    /**
     * 清除所有登录日志
     * 
     * @return 删除的日志数量
     */
    @DeleteMapping("/clean-login")
    public ResponseEntity<ApiResponse> cleanLoginLogs() {
        int count = operationLogService.cleanLoginLogs();
        return ResponseEntity.ok(ApiResponse.success("已清除 " + count + " 条登录日志", count));
    }
}
