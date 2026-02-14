package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.OperationLogService;
import com.example.demo.util.JwtUtil;
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
 * UserLoginServiceImpl 白盒测试
 * 覆盖登录全部分支 + CRUD 操作的成功/失败/异常路径
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserLoginServiceImpl 白盒测试")
class UserLoginServiceImplTest {

    @InjectMocks
    private UserLoginServiceImpl userLoginService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private OperationLogService operationLogService;

    // ==================== login 方法测试 ====================

    @Test
    @DisplayName("登录 - 用户不存在")
    void login_userNotFound() {
        when(userMapper.findByUsername("nouser")).thenReturn(null);

        ApiResponse response = userLoginService.login("nouser", "123456");

        assertFalse(response.isSuccess());
        assertEquals("用户名不存在", response.getMessage());
    }

    @Test
    @DisplayName("登录 - 密码错误")
    void login_wrongPassword() {
        User user = createUser(1L, "10001", "123456", "测试用户", 0);
        when(userMapper.findByUsername("10001")).thenReturn(user);

        ApiResponse response = userLoginService.login("10001", "wrong");

        assertFalse(response.isSuccess());
        assertEquals("密码错误", response.getMessage());
    }

    @Test
    @DisplayName("登录 - 成功（tokenVersion为null）")
    void login_success_nullVersion() {
        User user = createUser(1L, "10001", "123456", "测试用户", null);
        when(userMapper.findByUsername("10001")).thenReturn(user);
        when(jwtUtil.generateToken(eq(1L), eq("10001"), eq("user"), eq(1)))
                .thenReturn("mock_token");

        ApiResponse response = userLoginService.login("10001", "123456");

        assertTrue(response.isSuccess());
        assertEquals("登录成功", response.getMessage());
        verify(userMapper).updateTokenVersion(1L, 1);
    }

    @Test
    @DisplayName("登录 - 成功（tokenVersion自增）")
    void login_success_incrementVersion() {
        User user = createUser(1L, "10001", "123456", "测试用户", 3);
        when(userMapper.findByUsername("10001")).thenReturn(user);
        when(jwtUtil.generateToken(eq(1L), eq("10001"), eq("user"), eq(4)))
                .thenReturn("mock_token");

        ApiResponse response = userLoginService.login("10001", "123456");

        assertTrue(response.isSuccess());
        verify(userMapper).updateTokenVersion(1L, 4);
    }

    // ==================== findAll 测试 ====================

    @Test
    @DisplayName("查询所有用户")
    void findAll() {
        List<User> users = Arrays.asList(
                createUser(1L, "u1", "p1", "用户1", 0),
                createUser(2L, "u2", "p2", "用户2", 0)
        );
        when(userMapper.findAll()).thenReturn(users);

        List<User> result = userLoginService.findAll();

        assertEquals(2, result.size());
        verify(userMapper).findAll();
    }

    // ==================== insertUser 测试 ====================

    @Test
    @DisplayName("添加用户 - 成功")
    void insertUser_success() {
        User user = createUser(null, "newuser", "123456", "新用户", 0);
        when(userMapper.insertUser(any(User.class))).thenReturn(1);

        ApiResponse response = userLoginService.insertUser(user);

        assertTrue(response.isSuccess());
        assertEquals("用户添加成功", response.getMessage());
    }

    @Test
    @DisplayName("添加用户 - 失败（影响行数为0）")
    void insertUser_fail() {
        User user = createUser(null, "newuser", "123456", "新用户", 0);
        when(userMapper.insertUser(any(User.class))).thenReturn(0);

        ApiResponse response = userLoginService.insertUser(user);

        assertFalse(response.isSuccess());
        assertEquals("用户添加失败", response.getMessage());
    }

    @Test
    @DisplayName("添加用户 - 异常")
    void insertUser_exception() {
        User user = createUser(null, "newuser", "123456", "新用户", 0);
        when(userMapper.insertUser(any(User.class))).thenThrow(new RuntimeException("数据库异常"));

        ApiResponse response = userLoginService.insertUser(user);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("数据库异常"));
    }

    // ==================== updateUser 测试 ====================

    @Test
    @DisplayName("更新用户 - 成功")
    void updateUser_success() {
        User user = createUser(1L, "u1", "newpwd", "更新用户", 0);
        when(userMapper.updateUser(any(User.class))).thenReturn(1);

        ApiResponse response = userLoginService.updateUser(user);

        assertTrue(response.isSuccess());
        assertEquals("用户更新成功", response.getMessage());
    }

    @Test
    @DisplayName("更新用户 - 失败（用户不存在）")
    void updateUser_fail() {
        User user = createUser(999L, "u1", "newpwd", "更新用户", 0);
        when(userMapper.updateUser(any(User.class))).thenReturn(0);

        ApiResponse response = userLoginService.updateUser(user);

        assertFalse(response.isSuccess());
        assertEquals("用户更新失败", response.getMessage());
    }

    @Test
    @DisplayName("更新用户 - 异常")
    void updateUser_exception() {
        User user = createUser(1L, "u1", "newpwd", "更新用户", 0);
        when(userMapper.updateUser(any(User.class))).thenThrow(new RuntimeException("约束冲突"));

        ApiResponse response = userLoginService.updateUser(user);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("约束冲突"));
    }

    // ==================== deleteUser 测试 ====================

    @Test
    @DisplayName("删除用户 - 成功")
    void deleteUser_success() {
        when(userMapper.deleteUser(1L)).thenReturn(1);

        ApiResponse response = userLoginService.deleteUser(1L);

        assertTrue(response.isSuccess());
        assertEquals("用户删除成功", response.getMessage());
    }

    @Test
    @DisplayName("删除用户 - 失败")
    void deleteUser_fail() {
        when(userMapper.deleteUser(999L)).thenReturn(0);

        ApiResponse response = userLoginService.deleteUser(999L);

        assertFalse(response.isSuccess());
        assertEquals("用户删除失败", response.getMessage());
    }

    @Test
    @DisplayName("删除用户 - 异常")
    void deleteUser_exception() {
        when(userMapper.deleteUser(1L)).thenThrow(new RuntimeException("外键约束"));

        ApiResponse response = userLoginService.deleteUser(1L);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("外键约束"));
    }

    // ==================== 辅助方法 ====================

    private User createUser(Long id, String username, String password, String name, Integer tokenVersion) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setTokenVersion(tokenVersion);
        return user;
    }
}
