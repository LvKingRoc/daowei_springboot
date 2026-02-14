package com.example.demo.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 白盒测试
 * 覆盖 Token 生成、解析、验证、过期等全部分支路径
 */
@DisplayName("JwtUtil 白盒测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 通过反射注入配置值
        ReflectionTestUtils.setField(jwtUtil, "secret", "daowei_jwt_secret_key_2024_this_is_a_very_long_secret_key");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        // 调用 @PostConstruct 初始化方法
        jwtUtil.init();
    }

    // ==================== generateToken 测试 ====================

    @Test
    @DisplayName("生成Token - 正常参数")
    void generateToken_normalParams() {
        String token = jwtUtil.generateToken(1L, "admin", "admin", 1);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT 格式: header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("生成Token - tokenVersion为null时默认为0")
    void generateToken_nullTokenVersion() {
        String token = jwtUtil.generateToken(1L, "admin", "admin", null);
        assertNotNull(token);
        Integer version = jwtUtil.getTokenVersion(token);
        assertEquals(0, version);
    }

    // ==================== parseToken 测试 ====================

    @Test
    @DisplayName("解析Token - 正常解析")
    void parseToken_normal() {
        String token = jwtUtil.generateToken(1L, "testuser", "user", 5);
        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals("user", claims.get("role", String.class));
        assertEquals(5, claims.get("tokenVersion", Integer.class));
    }

    @Test
    @DisplayName("解析Token - 无效Token抛出异常")
    void parseToken_invalidToken() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jwtUtil.parseToken("invalid.token.string"));
        assertEquals("Token无效", ex.getMessage());
    }

    @Test
    @DisplayName("解析Token - 过期Token抛出异常")
    void parseToken_expiredToken() {
        // 设置过期时间为 -1ms（立即过期）
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secret", "daowei_jwt_secret_key_2024_this_is_a_very_long_secret_key");
        ReflectionTestUtils.setField(expiredJwtUtil, "expiration", -1L);
        expiredJwtUtil.init();

        String token = expiredJwtUtil.generateToken(1L, "admin", "admin", 1);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jwtUtil.parseToken(token));
        assertEquals("Token已过期", ex.getMessage());
    }

    // ==================== validateToken 测试 ====================

    @Test
    @DisplayName("验证Token - 有效Token返回true")
    void validateToken_valid() {
        String token = jwtUtil.generateToken(1L, "admin", "admin", 1);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("验证Token - 无效Token返回false")
    void validateToken_invalid() {
        assertFalse(jwtUtil.validateToken("invalid.token"));
    }

    @Test
    @DisplayName("验证Token - 空字符串返回false")
    void validateToken_emptyString() {
        assertFalse(jwtUtil.validateToken(""));
    }

    // ==================== getUserId / getUsername / getRole 测试 ====================

    @Test
    @DisplayName("从Token中获取UserId")
    void getUserId() {
        String token = jwtUtil.generateToken(99L, "testuser", "user", 1);
        assertEquals(99L, jwtUtil.getUserId(token));
    }

    @Test
    @DisplayName("从Token中获取Username")
    void getUsername() {
        String token = jwtUtil.generateToken(1L, "testuser", "user", 1);
        assertEquals("testuser", jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("从Token中获取Role")
    void getRole() {
        String token = jwtUtil.generateToken(1L, "admin", "admin", 1);
        assertEquals("admin", jwtUtil.getRole(token));
    }

    // ==================== isTokenExpired 测试 ====================

    @Test
    @DisplayName("Token未过期返回false")
    void isTokenExpired_notExpired() {
        String token = jwtUtil.generateToken(1L, "admin", "admin", 1);
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Token已过期返回true")
    void isTokenExpired_expired() {
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secret", "daowei_jwt_secret_key_2024_this_is_a_very_long_secret_key");
        ReflectionTestUtils.setField(expiredJwtUtil, "expiration", -1L);
        expiredJwtUtil.init();

        String token = expiredJwtUtil.generateToken(1L, "admin", "admin", 1);
        assertTrue(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("无效Token视为已过期")
    void isTokenExpired_invalidToken() {
        assertTrue(jwtUtil.isTokenExpired("invalid"));
    }

    // ==================== getTokenVersion 测试 ====================

    @Test
    @DisplayName("获取TokenVersion - 正常值")
    void getTokenVersion_normal() {
        String token = jwtUtil.generateToken(1L, "admin", "admin", 10);
        assertEquals(10, jwtUtil.getTokenVersion(token));
    }

    // ==================== refreshToken 测试 ====================

    @Test
    @DisplayName("刷新Token - 信息不变，过期时间重置")
    void refreshToken() throws InterruptedException {
        String oldToken = jwtUtil.generateToken(1L, "admin", "admin", 5);
        // 等待1秒，确保 iat 时间戳不同
        Thread.sleep(1000);
        String newToken = jwtUtil.refreshToken(oldToken);

        assertNotNull(newToken);
        assertNotEquals(oldToken, newToken);
        // 信息应保持不变
        assertEquals(jwtUtil.getUserId(oldToken), jwtUtil.getUserId(newToken));
        assertEquals(jwtUtil.getUsername(oldToken), jwtUtil.getUsername(newToken));
        assertEquals(jwtUtil.getRole(oldToken), jwtUtil.getRole(newToken));
        assertEquals(jwtUtil.getTokenVersion(oldToken), jwtUtil.getTokenVersion(newToken));
    }

    // ==================== init 密钥填充测试 ====================

    @Test
    @DisplayName("密钥长度不足时自动填充")
    void init_shortSecret() {
        JwtUtil shortJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortJwtUtil, "secret", "short");
        ReflectionTestUtils.setField(shortJwtUtil, "expiration", 86400000L);
        // 不应抛出异常
        assertDoesNotThrow(() -> shortJwtUtil.init());
        // 生成Token应正常工作
        String token = shortJwtUtil.generateToken(1L, "test", "user", 1);
        assertNotNull(token);
    }
}
