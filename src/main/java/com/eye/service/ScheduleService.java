package com.eye.service;

import com.eye.entity.CourseSchedule;
import com.eye.repository.CourseScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScheduleService {
    private final CourseScheduleRepository scheduleRepo;

    private static final Map<String, String> DAY_MAP = new LinkedHashMap<>();
    static {
        DAY_MAP.put("周一", "1"); DAY_MAP.put("周二", "2"); DAY_MAP.put("周三", "3");
        DAY_MAP.put("周四", "4"); DAY_MAP.put("周五", "5"); DAY_MAP.put("周六", "6"); DAY_MAP.put("周日", "7");
        DAY_MAP.put("星期一", "1"); DAY_MAP.put("星期二", "2"); DAY_MAP.put("星期三", "3");
        DAY_MAP.put("星期四", "4"); DAY_MAP.put("星期五", "5"); DAY_MAP.put("星期六", "6"); DAY_MAP.put("星期日", "7");
    }

    public ScheduleService(CourseScheduleRepository scheduleRepo) {
        this.scheduleRepo = scheduleRepo;
    }

    public Map<String, Object> importSchedule(String username, List<Map<String, String>> courses) {
        scheduleRepo.deleteByUsername(username);
        int count = 0;
        for (Map<String, String> c : courses) {
            String day = c.get("dayOfWeek");
            String dayNum = DAY_MAP.getOrDefault(day, day);
            CourseSchedule s = new CourseSchedule();
            s.setUsername(username);
            s.setCourseName(c.get("courseName"));
            s.setClassName(c.getOrDefault("className", ""));
            s.setDayOfWeek(dayNum);
            s.setStartTime(c.get("startTime"));
            s.setEndTime(c.get("endTime"));
            s.setLocation(c.getOrDefault("location", ""));
            scheduleRepo.save(s);
            count++;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        result.put("message", "成功导入 " + count + " 节课");
        return result;
    }

    public List<CourseSchedule> list(String username) {
        return scheduleRepo.findByUsernameOrderByDayOfWeekAscStartTimeAsc(username);
    }

    public List<CourseSchedule> getTodayClasses(String username) {
        String todayNum = String.valueOf(LocalDate.now().getDayOfWeek().getValue());
        List<CourseSchedule> all = scheduleRepo.findByUsernameOrderByDayOfWeekAscStartTimeAsc(username);
        List<CourseSchedule> today = new ArrayList<>();
        for (CourseSchedule s : all) {
            if (s.getDayOfWeek().equals(todayNum)) {
                today.add(s);
            }
        }
        return today;
    }

    public Map<String, Object> getCurrentSessionInfo(String username) {
        Map<String, Object> result = new HashMap<>();
        List<CourseSchedule> today = getTodayClasses(username);
        LocalTime now = LocalTime.now();
        for (CourseSchedule s : today) {
            LocalTime start = LocalTime.parse(s.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(s.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));
            if (!now.isBefore(start) && !now.isAfter(end)) {
                result.put("inClass", true);
                result.put("courseName", s.getCourseName());
                result.put("className", s.getClassName());
                result.put("location", s.getLocation());
                result.put("startTime", s.getStartTime());
                result.put("endTime", s.getEndTime());
                return result;
            }
        }
        result.put("inClass", false);
        return result;
    }

    @Transactional
    public void clear(String username) {
        scheduleRepo.deleteByUsername(username);
    }
}
