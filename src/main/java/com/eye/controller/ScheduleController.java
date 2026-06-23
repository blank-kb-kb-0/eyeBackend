package com.eye.controller;

import com.eye.entity.CourseSchedule;
import com.eye.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/import")
    public Map<String, Object> importSchedule(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> courses = (List<Map<String, String>>) body.get("courses");
        return scheduleService.importSchedule((String) body.get("username"), courses);
    }

    @GetMapping("/list")
    public List<CourseSchedule> list(@RequestParam String username) {
        return scheduleService.list(username);
    }

    @GetMapping("/today")
    public List<CourseSchedule> getToday(@RequestParam String username) {
        return scheduleService.getTodayClasses(username);
    }

    @GetMapping("/current")
    public Map<String, Object> getCurrent(@RequestParam String username) {
        return scheduleService.getCurrentSessionInfo(username);
    }

    @DeleteMapping("/clear")
    public Map<String, Object> clear(@RequestParam String username) {
        scheduleService.clear(username);
        return Map.of("success", true);
    }
}
