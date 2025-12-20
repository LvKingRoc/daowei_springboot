package com.example.demo.service;

import com.example.demo.entity.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DDNS动态域名更新服务
 * 每15分钟自动访问YDNS更新URL，保持动态公网IP的实时更新
 */
@Service
public class DdnsUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(DdnsUpdateService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 记录上次更新时间和结果
    private String lastUpdateTime = "未更新";
    private String lastUpdateResult = "未更新";
    private int successCount = 0;
    private int failCount = 0;
    private int ipChangeCount = 0;  // IP地址变化次数

    @Autowired
    private OperationLogService operationLogService;

    // YDNS更新URL，可通过配置文件覆盖
    @Value("${ddns.update.url:https://ydns.io/hosts/update/ASg4ey8tug5HHnR22UkAQ4RW}")
    private String ddnsUpdateUrl;

    // 是否启用DDNS更新，默认启用
    @Value("${ddns.update.enabled:true}")
    private boolean ddnsEnabled;

    /**
     * 定时任务：每1小时执行一次DDNS更新
     * fixedRate = 3600000 表示每3600秒（1小时）执行一次
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 10000)
    public void updateDdns() {
        if (!ddnsEnabled) {
            logger.debug("DDNS更新已禁用，跳过本次更新");
            return;
        }

        String currentTime = LocalDateTime.now().format(formatter);
        
        try {
            logger.info("========== DDNS更新开始 [{}] ==========", currentTime);
            logger.info("DDNS更新URL: {}", ddnsUpdateUrl);
            
            URL url = new URL(ddnsUpdateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "Daowei-DDNS-Updater/1.0");

            int responseCode = connection.getResponseCode();
            
            // 读取响应内容
            String responseBody = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                responseBody = sb.toString();
            } catch (Exception ignored) {}
            
            lastUpdateTime = currentTime;
            
            if (responseCode == 200) {
                successCount++;
                lastUpdateResult = "成功";
                logger.info("DDNS更新成功! 响应码: {}, 响应内容: {}", responseCode, responseBody);
                logger.info("统计: 成功{}次, 失败{}次", successCount, failCount);
            } else {
                failCount++;
                lastUpdateResult = "失败(响应码:" + responseCode + ")";
                logger.warn("DDNS更新返回非200响应码: {}, 响应内容: {}", responseCode, responseBody);
            }

            connection.disconnect();
            logger.info("========== DDNS更新结束 ==========");
            
            // 写入操作日志数据库
            saveToOperationLog(responseCode == 200, responseBody, null);
        } catch (Exception e) {
            failCount++;
            lastUpdateTime = currentTime;
            lastUpdateResult = "异常: " + e.getMessage();
            logger.error("========== DDNS更新失败 ==========");
            logger.error("DDNS更新异常: {}", e.getMessage(), e);
            logger.error("统计: 成功{}次, 失败{}次", successCount, failCount);
            
            // 写入操作日志数据库
            saveToOperationLog(false, null, e.getMessage());
        }
    }

    /**
     * 获取DDNS更新状态
     */
    public String getStatus() {
        return String.format("上次更新: %s, 结果: %s, 统计: 成功%d次/失败%d次", 
            lastUpdateTime, lastUpdateResult, successCount, failCount);
    }

    /**
     * 将DDNS更新结果写入操作日志数据库
     * 用户名为system，只保留一份（更新而非新增）
     */
    private void saveToOperationLog(boolean success, String responseBody, String errorMsg) {
        try {
            OperationLog log = new OperationLog();
            log.setOperatorName("system");
            log.setRole("system");
            log.setModule("DDNS更新");
            
            // 根据响应内容确定操作类型
            boolean ipChanged = false;
            if (success) {
                if (responseBody != null && responseBody.contains("unchanged")) {
                    log.setAction("IP未变更");
                } else {
                    log.setAction("IP已更新");
                    ipChangeCount++;  // IP变化次数+1
                    ipChanged = true;
                }
                log.setStatus(1);  // 成功
            } else {
                log.setAction("更新失败");
                log.setStatus(0);  // 失败
                log.setErrorMsg(errorMsg);
            }
            
            // 设置日志描述：DDNS更新X次，IP地址变化X次，本次IP有/无更新
            log.setDescription(String.format("DDNS更新%d次，IP地址变化%d次，本次IP%s更新",
                    successCount, ipChangeCount, ipChanged ? "有" : "无"));
            
            log.setResponseData(responseBody);
            log.setRequestUrl(ddnsUpdateUrl);
            log.setRequestMethod("GET");
            
            // 使用saveOrUpdateSystemLog方法，只保留一份日志
            operationLogService.saveOrUpdateSystemLog(log);
            logger.info("DDNS更新结果已写入操作日志数据库");
        } catch (Exception e) {
            logger.error("写入操作日志失败: {}", e.getMessage());
        }
    }

    /**
     * 手动触发DDNS更新（可通过API调用）
     */
    public String manualUpdate() {
        logger.info("手动触发DDNS更新...");
        try {
            URL url = new URL(ddnsUpdateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            if (responseCode == 200) {
                logger.info("手动DDNS更新成功");
                return "DDNS更新成功";
            } else {
                logger.warn("手动DDNS更新返回非200响应码: {}", responseCode);
                return "DDNS更新失败，响应码: " + responseCode;
            }
        } catch (Exception e) {
            logger.error("手动DDNS更新失败: {}", e.getMessage());
            return "DDNS更新失败: " + e.getMessage();
        }
    }
}
