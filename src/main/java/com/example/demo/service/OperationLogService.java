package com.example.demo.service;

import com.example.demo.entity.OperationLog;
import com.github.pagehelper.PageInfo;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 保存日志
     */
    void save(OperationLog log);

    /**
     * 分页查询日志
     */
    PageInfo<OperationLog> findByPage(int pageNum, int pageSize);

    /**
     * 条件分页查询日志
     */
    PageInfo<OperationLog> findByCondition(
            String username,
            String module,
            String action,
            String startTime,
            String endTime,
            int pageNum,
            int pageSize
    );

    /**
     * 根据ID查询日志详情
     */
    OperationLog findById(Long id);

    /**
     * 清理指定天数前的日志
     */
    int cleanOldLogs(int days);

    /**
     * 清除所有登录日志
     */
    int cleanLoginLogs();
}
