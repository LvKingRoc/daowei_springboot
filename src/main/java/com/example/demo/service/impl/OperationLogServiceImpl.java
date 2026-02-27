package com.example.demo.service.impl;

import com.example.demo.entity.OperationLog;
import com.example.demo.mapper.OperationLogMapper;
import com.example.demo.service.OperationLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 操作日志服务实现类
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 异步保存日志，不影响主业务
     */
    @Override
    @Async
    public void save(OperationLog log) {
        try {
            operationLogMapper.insert(log);
        } catch (Exception e) {
            // 日志保存失败不影响主业务
            e.printStackTrace();
        }
    }

    @Override
    public PageInfo<OperationLog> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> list = operationLogMapper.selectAll();
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<OperationLog> findByCondition(
            String username,
            String module,
            String action,
            String startTime,
            String endTime,
            int pageNum,
            int pageSize
    ) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> list = operationLogMapper.selectByCondition(
                username, module, action, startTime, endTime
        );
        return new PageInfo<>(list);
    }

    @Override
    public OperationLog findById(Long id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    public int cleanOldLogs(int days) {
        LocalDate date = LocalDate.now().minusDays(days);
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return operationLogMapper.deleteByDate(dateStr);
    }

    @Override
    public int cleanLoginLogs() {
        return operationLogMapper.deleteByAction("LOGIN");
    }
}
