package com.eye.service;

import com.eye.entity.AttendanceRecord;
import com.eye.entity.Student;
import com.eye.repository.AttendanceRepository;
import com.eye.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepo;
    private final StudentRepository studentRepo;
    private static final Map<String, Long> SESSION_STARTS = new HashMap<>();
    private static final long LATE_THRESHOLD_MS = 10 * 60 * 1000; // 10分钟

    public AttendanceService(AttendanceRepository attendanceRepo, StudentRepository studentRepo) {
        this.attendanceRepo = attendanceRepo;
        this.studentRepo = studentRepo;
    }

    public Map<String, Object> recordAttendance(String username, String studentName, String sessionId, Boolean isProxy, String className) {
        Map<String, Object> result = new HashMap<>();
        if (username == null || studentName == null || sessionId == null) {
            result.put("success", false);
            result.put("message", "参数不完整");
            return result;
        }

        List<AttendanceRecord> existing = attendanceRepo
            .findByUsernameAndSessionIdOrderByTimestampDesc(username, sessionId);
        for (AttendanceRecord r : existing) {
            if (r.getStudentName().equals(studentName)) {
                result.put("success", true);
                result.put("message", "已签到，无需重复记录");
                result.put("alreadyExists", true);
                return result;
            }
        }

        long now = System.currentTimeMillis();
        Long sessionStart = SESSION_STARTS.get(sessionId);
        boolean isLate = sessionStart != null && (now - sessionStart) > LATE_THRESHOLD_MS;

        AttendanceRecord record = new AttendanceRecord();
        record.setStudentName(studentName);
        record.setTimestamp(now);
        record.setSessionId(sessionId);
        record.setUsername(username);
        record.setIsProxy(isProxy != null && isProxy);
        record.setClassName(className != null ? className : "");
        String note = "";
        if (isProxy != null && isProxy) note = "疑似代课";
        if (isLate) note += (note.isEmpty() ? "" : " + ") + "迟到";
        record.setNote(note.isEmpty() ? null : note);
        attendanceRepo.save(record);

        result.put("success", true);
        result.put("message", "考勤记录成功");
        result.put("alreadyExists", false);
        return result;
    }

    public List<AttendanceRecord> getRecords(String username) {
        if (username == null) return new ArrayList<>();
        return attendanceRepo.findByUsernameOrderByTimestampDesc(username);
    }

    public List<AttendanceRecord> getSessionRecords(String username, String sessionId) {
        if (username == null || sessionId == null) return new ArrayList<>();
        return attendanceRepo.findByUsernameAndSessionIdOrderByTimestampDesc(username, sessionId);
    }

    public Map<String, Object> getAttendanceReport(String username, String sessionId) {
        Map<String, Object> report = new HashMap<>();
        List<Student> allStudents = studentRepo.findByUsername(username);
        List<AttendanceRecord> records = attendanceRepo
            .findByUsernameAndSessionIdOrderByTimestampDesc(username, sessionId);

        Set<String> presentStudents = new LinkedHashSet<>();
        List<AttendanceRecord> proxies = new ArrayList<>();
        for (AttendanceRecord r : records) {
            presentStudents.add(r.getStudentName());
            if (Boolean.TRUE.equals(r.getIsProxy())) {
                proxies.add(r);
            }
        }

        List<String> present = new ArrayList<>(presentStudents);
        List<String> absent = new ArrayList<>();
        for (Student s : allStudents) {
            if (!presentStudents.contains(s.getName())) {
                absent.add(s.getName());
            }
        }

        report.put("sessionId", sessionId);
        report.put("totalStudents", allStudents.size());
        report.put("present", present);
        report.put("presentCount", present.size());
        report.put("absent", absent);
        report.put("absentCount", absent.size());
        report.put("proxyCount", proxies.size());
        report.put("proxyRecords", proxies);
        return report;
    }

    public String getLatestSessionId(String username) {
        List<AttendanceRecord> all = attendanceRepo.findByUsernameOrderByTimestampDesc(username);
        Set<String> seen = new LinkedHashSet<>();
        for (AttendanceRecord r : all) {
            if (r.getSessionId() != null && !r.getSessionId().isEmpty()) {
                seen.add(r.getSessionId());
            }
        }
        return seen.isEmpty() ? "" : seen.iterator().next();
    }

    public int getTotalAttendance(String username) {
        List<AttendanceRecord> all = attendanceRepo.findByUsernameOrderByTimestampDesc(username);
        Set<String> unique = new HashSet<>();
        for (AttendanceRecord r : all) {
            if (r.getStudentName() != null) {
                unique.add(r.getStudentName());
            }
        }
        return unique.size();
    }

    public Map<String, Object> startSession(String username) {
        Map<String, Object> result = new HashMap<>();
        if (username == null || username.isEmpty()) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        String sessionId = username + "_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        SESSION_STARTS.put(sessionId, System.currentTimeMillis());
        result.put("success", true);
        result.put("sessionId", sessionId);
        result.put("message", "课堂已创建");
        return result;
    }
}
