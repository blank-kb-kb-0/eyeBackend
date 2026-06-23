package com.eye.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class NotificationService {
    private static final String SEND_KEY = "YOUR_SERVERCHAN_KEY";
    private static final String SERVER_CHAN_URL = "https://sctapi.ftqq.com/" + SEND_KEY + ".send";

    private final HttpClient client = HttpClient.newHttpClient();

    public void sendWeChat(String title, String content) {
        try {
            String body = "title=" + URLEncoder.encode(title, StandardCharsets.UTF_8)
                + "&desp=" + URLEncoder.encode(content, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_CHAN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(java.time.Duration.ofSeconds(5))
                .build();
            client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("[通知] 已推送: " + title);
        } catch (Exception e) {
            System.err.println("[通知] 推送失败: " + e.getMessage());
        }
    }

    public void notifyAbnormal(String studentName, String behavior, String time) {
        String title = "⚠️ 异常行为告警";
        String content = "**学生：**" + studentName + "\n\n**行为：**" + behavior + "\n\n**时间：**" + time;
        sendWeChat(title, content);
    }

    public void notifyAbsent(String studentName, String courseName) {
        String title = "❌ 旷课通知";
        String content = "**学生：**" + studentName + "\n\n**课程：**" + courseName + "\n\n请及时关注。";
        sendWeChat(title, content);
    }
}
