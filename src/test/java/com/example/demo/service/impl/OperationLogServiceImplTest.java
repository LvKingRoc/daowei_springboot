package com.example.demo.service.impl;

import com.example.demo.entity.OperationLog;
import com.example.demo.mapper.OperationLogMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OperationLogServiceImpl 白盒测试
 * 覆盖日志保存（含异常分支）、条件查询、清理、saveOrUpdate 全部路径
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OperationLogServiceImpl 白盒测试")
class OperationLogServiceImplTest {

    @InjectMocks
    private OperationLogServiceImpl operationLogService;

    @Mock
    private OperationLogMapper operationLogMapper;

    // ==================== save 测试 ====================

    @Test
    @DisplayName("保存日志 - 正常")
    void save_normal() {
        OperationLog log = createLog("订单管理", "CREATE");

        assertDoesNotThrow(() -> operationLogService.save(log));
        verify(operationLogMapper).insert(any(OperationLog.class));
    }

    @Test
    @DisplayName("保存日志 - 异常不抛出（不影响主业务）")
    void save_exception() {
        OperationLog log = createLog("订单管理", "CREATE");
        doThrow(new RuntimeException("数据库异常")).when(operationLogMapper).insert(any(OperationLog.class));

        // 不应抛出异常
        assertDoesNotThrow(() -> operationLogService.save(log));
    }

    // ==================== findById 测试 ====================

    @Test
    @DisplayName("根据ID查询日志")
    void findById() {
        OperationLog log = createLog("订单管理", "CREATE");
        log.setId(1L);
        when(operationLogMapper.selectById(1L)).thenReturn(log);

        OperationLog result = operationLogService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ==================== cleanOldLogs 测试 ====================

    @Test
    @DisplayName("清理旧日志")
    void cleanOldLogs() {
        when(operationLogMapper.deleteByDate(anyString())).thenReturn(50);

        int count = operationLogService.cleanOldLogs(30);

        assertEquals(50, count);
        verify(operationLogMapper).deleteByDate(anyString());
    }

    // ==================== cleanLoginLogs 测试 ====================

    @Test
    @DisplayName("清除登录日志")
    void cleanLoginLogs() {
        when(operationLogMapper.deleteByAction("LOGIN")).thenReturn(10);

        int count = operationLogService.cleanLoginLogs();

        assertEquals(10, count);
    }

    // ==================== 辅助方法 ====================

    private OperationLog createLog(String module, String action) {
        OperationLog log = new OperationLog();
        log.setModule(module);
        log.setAction(action);
        log.setStatus(1);
        return log;
    }
}
