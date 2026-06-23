package com.eye.controller;

import com.eye.entity.AttendanceRecord;
import com.eye.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/record")
    public Map<String, Object> record(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String studentName = body.get("studentName");
        String sessionId = body.get("sessionId");
        boolean isProxy = "true".equalsIgnoreCase(body.get("isProxy"));
        String className = body.getOrDefault("className", "");
        return attendanceService.recordAttendance(username, studentName, sessionId, isProxy, className);
    }

    @GetMapping("/records")
    public List<AttendanceRecord> getRecords(@RequestParam String username) {
        return attendanceService.getRecords(username);
    }

    @GetMapping("/session")
    public List<AttendanceRecord> getSession(@RequestParam String username, @RequestParam String sessionId) {
        return attendanceService.getSessionRecords(username, sessionId);
    }

    @GetMapping("/report")
    public Map<String, Object> getReport(@RequestParam String username, @RequestParam String sessionId) {
        return attendanceService.getAttendanceReport(username, sessionId);
    }

    @GetMapping("/latest")
    public Map<String, Object> getLatest(@RequestParam String username) {
        String sessionId = attendanceService.getLatestSessionId(username);
        return Map.of("success", true, "sessionId", sessionId);
    }

    @GetMapping("/total")
    public Map<String, Object> getTotal(@RequestParam String username) {
        int total = attendanceService.getTotalAttendance(username);
        return Map.of("success", true, "total", total);
    }

    @GetMapping("/export")
    public void export(@RequestParam String username, @RequestParam String sessionId, HttpServletResponse response) {
        Map<String, Object> report = attendanceService.getAttendanceReport(username, sessionId);
        response.setContentType("text/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=attendance_" + sessionId + ".csv");
        try (PrintWriter w = response.getWriter()) {
            w.write("学生姓名,状态,备注\n");
            @SuppressWarnings("unchecked")
            List<String> present = (List<String>) report.getOrDefault("present", List.of());
            for (String name : present) w.write(name + ",已签到,\n");
            @SuppressWarnings("unchecked")
            List<String> absent = (List<String>) report.getOrDefault("absent", List.of());
            for (String name : absent) w.write(name + ",旷课,\n");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> proxies = (List<Map<String, Object>>) report.getOrDefault("proxyRecords", List.of());
            for (Map<String, Object> p : proxies) {
                w.write(p.get("studentName") + ",疑似代课,\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/start")
    public Map<String, Object> startSession(@RequestBody Map<String, String> body) {
        return attendanceService.startSession(body.get("username"));
    }
}
