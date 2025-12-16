package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方API代理控制器
 * 将敏感API密钥保存在后端，前端通过此代理访问第三方服务
 */
@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    // 百度OCR配置
    @Value("${baidu.ocr.ak:}")
    private String baiduOcrAk;

    @Value("${baidu.ocr.sk:}")
    private String baiduOcrSk;

    // 高德天气配置
    @Value("${amap.weather.key:}")
    private String amapWeatherKey;

    @Value("${amap.weather.city:440300}")
    private String amapCityCode;

    // 日历API配置
    @Value("${calendar.api.id:}")
    private String calendarApiId;

    @Value("${calendar.api.key:}")
    private String calendarApiKey;

    // 缓存百度OCR的access_token
    private String baiduAccessToken;
    private long baiduTokenExpireTime = 0;

    /**
     * 高德天气API代理
     */
    @GetMapping("/weather")
    public ResponseEntity<ApiResponse> getWeather(
            @RequestParam(required = false) String city) {
        try {
            String cityCode = city != null ? city : amapCityCode;
            
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://restapi.amap.com/v3/weather/weatherInfo")
                    .queryParam("key", amapWeatherKey)
                    .queryParam("city", cityCode)
                    .queryParam("extensions", "base")
                    .build()
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return ResponseEntity.ok(ApiResponse.success("获取天气成功", response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取天气失败: " + e.getMessage()));
        }
    }

    /**
     * 日历API代理
     */
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse> getCalendar(
            @RequestParam(required = false) String date) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("https://cn.apihz.cn/api/time/getday.php")
                    .queryParam("id", calendarApiId)
                    .queryParam("key", calendarApiKey);
            
            if (date != null) {
                builder.queryParam("date", date);
            }

            String url = builder.build().toUriString();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return ResponseEntity.ok(ApiResponse.success("获取日历成功", response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日历失败: " + e.getMessage()));
        }
    }

    /**
     * 百度OCR API代理 - 获取access_token
     */
    @GetMapping("/ocr/token")
    public ResponseEntity<ApiResponse> getOcrToken() {
        try {
            String token = getBaiduAccessToken();
            Map<String, String> data = new HashMap<>();
            data.put("access_token", token);
            return ResponseEntity.ok(ApiResponse.success("获取Token成功", data));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取Token失败: " + e.getMessage()));
        }
    }

    /**
     * 百度OCR API代理 - 文字识别
     */
    @PostMapping("/ocr/recognize")
    public ResponseEntity<ApiResponse> ocrRecognize(@RequestBody Map<String, String> request) {
        try {
            String token = getBaiduAccessToken();
            String image = request.get("image");
            
            if (image == null || image.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("图片数据不能为空"));
            }

            // 构建请求URL
            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + token;

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 构建请求体
            String body = "image=" + java.net.URLEncoder.encode(image, StandardCharsets.UTF_8.toString());

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            return ResponseEntity.ok(ApiResponse.success("识别成功", response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("OCR识别失败: " + e.getMessage()));
        }
    }

    /**
     * 获取百度API的access_token（带缓存）
     */
    private synchronized String getBaiduAccessToken() throws Exception {
        // 检查缓存是否有效
        if (baiduAccessToken != null && System.currentTimeMillis() < baiduTokenExpireTime) {
            return baiduAccessToken;
        }

        // 获取新token
        String url = UriComponentsBuilder
                .fromHttpUrl("https://aip.baidubce.com/oauth/2.0/token")
                .queryParam("grant_type", "client_credentials")
                .queryParam("client_id", baiduOcrAk)
                .queryParam("client_secret", baiduOcrSk)
                .build()
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map body = response.getBody();

        if (body != null && body.containsKey("access_token")) {
            baiduAccessToken = (String) body.get("access_token");
            // 设置过期时间（提前5分钟过期）
            int expiresIn = (Integer) body.getOrDefault("expires_in", 2592000);
            baiduTokenExpireTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
            return baiduAccessToken;
        }

        throw new RuntimeException("获取百度access_token失败");
    }

    /**
     * 获取API配置信息（不含敏感密钥）
     */
    @GetMapping("/config")
    public ResponseEntity<ApiResponse> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("weatherCityCode", amapCityCode);
        config.put("hasWeatherKey", !amapWeatherKey.isEmpty());
        config.put("hasOcrKey", !baiduOcrAk.isEmpty() && !baiduOcrSk.isEmpty());
        config.put("hasCalendarKey", !calendarApiId.isEmpty() && !calendarApiKey.isEmpty());
        return ResponseEntity.ok(ApiResponse.success("获取配置成功", config));
    }
}
