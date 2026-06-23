package com.eye.controller;

import com.eye.entity.AbnormalEvent;
import com.eye.entity.DetectionRecord;
import com.eye.service.DetectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detection")
public class DetectionController {
    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @GetMapping("/history")
    public List<DetectionRecord> getHistory(@RequestParam String username) {
        return detectionService.getHistory(username);
    }

    @GetMapping("/history/light")
    public List<DetectionRecord> getHistoryLight(@RequestParam String username) {
        return detectionService.getHistoryLight(username);
    }

    @GetMapping("/photo")
    public String getPhoto(@RequestParam String username, @RequestParam String recordId) {
        return detectionService.getPhoto(username, recordId);
    }

    @PostMapping("/save")
    public Map<String, Object> saveRecord(@RequestParam String username, @RequestBody Map<String, Object> body) {
        return detectionService.saveDetectionWithEvents(username, body);
    }

    @DeleteMapping("/history")
    public Map<String, Object> deleteRecord(@RequestParam String username, @RequestParam String recordId) {
        detectionService.deleteRecord(username, recordId);
        return Map.of("success", true);
    }

    @GetMapping("/abnormal")
    public List<AbnormalEvent> getAbnormalEvents(@RequestParam String username) {
        return detectionService.getAbnormalEvents(username);
    }

    @GetMapping("/abnormal/recent")
    public List<AbnormalEvent> getRecentAbnormalEvents(@RequestParam String username) {
        return detectionService.getRecentAbnormalEvents(username);
    }

    @DeleteMapping("/abnormal/clear")
    public Map<String, Object> clearAbnormalEvents(@RequestParam String username) {
        detectionService.clearAbnormalEvents(username);
        return Map.of("success", true);
    }

    @GetMapping("/students")
    public List<String> getStudentNames(@RequestParam String username) {
        return detectionService.getAllStudentNames(username);
    }

    @PostMapping("/favorite/add")
    public Map<String, Object> addFavorite(@RequestParam String username, @RequestParam String recordId) {
        return detectionService.addFavorite(username, recordId);
    }

    @DeleteMapping("/favorite/remove")
    public Map<String, Object> removeFavorite(@RequestParam String username, @RequestParam String recordId) {
        return detectionService.removeFavorite(username, recordId);
    }

    @GetMapping("/favorites")
    public List<DetectionRecord> getFavorites(@RequestParam String username) {
        return detectionService.getFavorites(username);
    }
}