package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.service.OperationLogService;
import com.example.demo.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminLoginServiceImpl 白盒测试
 * 覆盖登录逻辑的全部分支：用户不存在、密码错误、tokenVersion为null、正常登录
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminLoginServiceImpl 白盒测试")
class AdminLoginServiceImplTest {

    @InjectMocks
    private AdminLoginServiceImpl adminLoginService;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private OperationLogService operationLogService;

    // ==================== login 方法测试 ====================

    @Test
    @DisplayName("登录 - 用户名不存在")
    void login_usernameNotFound() {
        when(adminMapper.findByUsername("nouser")).thenReturn(null);

        ApiResponse response = adminLoginService.login("nouser", "123456");

        assertFalse(response.isSuccess());
        assertEquals("用户名不存在", response.getMessage());
        // 不应调用 updateTokenVersion 和 generateToken
        verify(adminMapper, never()).updateTokenVersion(anyLong(), anyInt());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("登录 - 密码错误")
    void login_wrongPassword() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("123456");
        when(adminMapper.findByUsername("admin")).thenReturn(admin);

        ApiResponse response = adminLoginService.login("admin", "wrongpwd");

        assertFalse(response.isSuccess());
        assertEquals("密码错误", response.getMessage());
        verify(adminMapper, never()).updateTokenVersion(anyLong(), anyInt());
    }

    @Test
    @DisplayName("登录 - 成功（tokenVersion为null，应默认从0自增到1）")
    void login_success_nullTokenVersion() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setName("系统管理员");
        admin.setTokenVersion(null); // 模拟数据库中版本为null

        when(adminMapper.findByUsername("admin")).thenReturn(admin);
        when(jwtUtil.generateToken(eq(1L), eq("admin"), eq("admin"), eq(1)))
                .thenReturn("mock_token");

        ApiResponse response = adminLoginService.login("admin", "123456");

        assertTrue(response.isSuccess());
        assertEquals("登录成功", response.getMessage());
        // tokenVersion 应从 null(0) + 1 = 1
        verify(adminMapper).updateTokenVersion(1L, 1);
        verify(jwtUtil).generateToken(1L, "admin", "admin", 1);
    }

    @Test
    @DisplayName("登录 - 成功（tokenVersion已有值，应自增）")
    void login_success_existingTokenVersion() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setName("系统管理员");
        admin.setTokenVersion(5);

        when(adminMapper.findByUsername("admin")).thenReturn(admin);
        when(jwtUtil.generateToken(eq(1L), eq("admin"), eq("admin"), eq(6)))
                .thenReturn("mock_token_v6");

        ApiResponse response = adminLoginService.login("admin", "123456");

        assertTrue(response.isSuccess());
        // tokenVersion 应从 5 + 1 = 6
        verify(adminMapper).updateTokenVersion(1L, 6);
        verify(jwtUtil).generateToken(1L, "admin", "admin", 6);
        // 验证返回数据
        assertNotNull(response.getData());
    }
}
