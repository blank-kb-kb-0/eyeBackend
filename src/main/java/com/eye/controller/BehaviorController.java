package com.eye.controller;

import com.eye.entity.AbnormalEvent;
import com.eye.repository.AbnormalEventRepository;
import com.eye.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.*;

@RestController
@RequestMapping("/api/behavior")
public class BehaviorController {
    private final RestTemplate restTemplate;
    private final AbnormalEventRepository abnormalRepo;
    private final NotificationService notificationService;
    private static final String BEHAVIOR_SERVICE_URL = "http://127.0.0.1:5002";

    public BehaviorController(AbnormalEventRepository abnormalRepo, NotificationService notificationService) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
        this.abnormalRepo = abnormalRepo;
        this.notificationService = notificationService;
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> response = restTemplate.postForObject(
                BEHAVIOR_SERVICE_URL + "/analyze", body, Map.class);
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                result.put("success", true);
                result.put("behaviors", response.get("behaviors"));

                String username = (String) body.get("username");
                String recordId = (String) body.get("recordId");
                List<Map<String, Object>> behaviors =
                    (List<Map<String, Object>>) response.get("behaviors");
                List<Map<String, Object>> persons =
                    (List<Map<String, Object>>) body.get("persons");

                if (behaviors != null && persons != null && username != null) {
                    for (int i = 0; i < behaviors.size() && i < persons.size(); i++) {
                        Map<String, Object> b = behaviors.get(i);
                        String behavior = (String) b.get("behavior");
                        if (!"normal".equals(behavior)) {
                            Map<String, Object> p = persons.get(i);
                            String personName = (String) p.getOrDefault("className", "未知");
                            AbnormalEvent event = new AbnormalEvent();
                            event.setEventId(UUID.randomUUID().toString());
                            event.setTimestamp(System.currentTimeMillis());
                            event.setPersonName(personName);
                            event.setBehavior(behavior);
                            event.setBehaviorLabel((String) b.get("behaviorLabel"));
                            event.setLocation("教室");
                            event.setConfidence(b.get("confidence") instanceof Number
                                ? ((Number) b.get("confidence")).doubleValue() : 0.5);
                            event.setSourceHistoryId(recordId != null ? recordId : "");
                            event.setUsername(username);
                            abnormalRepo.save(event);
                            notificationService.notifyAbnormal(personName, (String)b.get("behaviorLabel"),
                                new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
                        }
                    }
                }
            } else {
                result.put("success", false);
                result.put("message", "行为服务无响应");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "行为分析失败: " + e.getMessage());
        }
        return result;
    }
}
