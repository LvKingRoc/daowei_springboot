package com.example.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成、解析和验证 JWT Token
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:daowei_jwt_secret_key_2024_this_is_a_very_long_secret_key}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration; // 默认24小时

    private SecretKey key;

    @PostConstruct
    public void init() {
        // 确保密钥至少256位
        String paddedSecret = secret;
        while (paddedSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            paddedSecret += secret;
        }
        this.key = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param role         角色（admin/user）
     * @param tokenVersion Token版本号（用于单点登录）
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, String role, Integer tokenVersion) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("tokenVersion", tokenVersion != null ? tokenVersion : 0);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token 获取 Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期");
        } catch (JwtException e) {
            throw new RuntimeException("Token无效");
        }
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取角色
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 判断 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从 Token 中获取版本号
     */
    public Integer getTokenVersion(String token) {
        Claims claims = parseToken(token);
        return claims.get("tokenVersion", Integer.class);
    }

    /**
     * 刷新 Token（滑动过期）
     * 基于原 Token 生成新的 Token，重置过期时间
     *
     * @param token 原 JWT Token
     * @return 新的 JWT Token
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);
        Integer tokenVersion = claims.get("tokenVersion", Integer.class);
        
        // 生成新 Token，重置过期时间为 24 小时
        return generateToken(userId, username, role, tokenVersion);
    }
}
