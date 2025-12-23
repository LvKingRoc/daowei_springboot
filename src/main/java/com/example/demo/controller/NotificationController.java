package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE 实时通知控制器
 * 用于向客户端推送订单状态变更等通知
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    // 存储所有连接的客户端 SSE Emitter
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 客户端订阅通知
     * @param userId 用户ID（可选，用于定向推送）
     * @return SSE Emitter
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam(required = false, defaultValue = "anonymous") String userId) {
        // 30分钟超时
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        
        String clientId = userId + "_" + System.currentTimeMillis();
        emitters.put(clientId, emitter);

        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("{\"status\":\"connected\",\"clientId\":\"" + clientId + "\"}"));
        } catch (IOException e) {
            emitters.remove(clientId);
        }

        // 设置回调
        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));
        emitter.onError(e -> emitters.remove(clientId));

        return emitter;
    }

    /**
     * 向所有客户端广播通知
     * @param type 通知类型（如 order_status_change）
     * @param message 通知内容
     */
    public static void broadcast(String type, String message) {
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(type)
                        .data(message));
            } catch (IOException e) {
                emitters.remove(clientId);
            }
        });
    }

    /**
     * 向指定用户推送通知
     * @param userId 用户ID前缀
     * @param type 通知类型
     * @param message 通知内容
     */
    public static void sendToUser(String userId, String type, String message) {
        emitters.forEach((clientId, emitter) -> {
            if (clientId.startsWith(userId + "_")) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(type)
                            .data(message));
                } catch (IOException e) {
                    emitters.remove(clientId);
                }
            }
        });
    }

    /**
     * 获取当前连接数
     */
    @GetMapping("/connections")
    public ApiResponse getConnectionCount() {
        return ApiResponse.success(emitters.size());
    }

    /**
     * 测试发送通知（仅开发调试用）
     */
    @PostMapping("/test")
    public ApiResponse testNotification(@RequestBody Map<String, String> payload) {
        String type = payload.getOrDefault("type", "test");
        String message = payload.getOrDefault("message", "{\"content\":\"测试通知\"}");
        broadcast(type, message);
        return ApiResponse.success("通知已发送到 " + emitters.size() + " 个客户端");
    }
}
