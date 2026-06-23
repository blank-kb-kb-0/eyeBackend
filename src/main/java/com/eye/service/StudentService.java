package com.eye.service;

import com.eye.entity.Student;
import com.eye.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.*;

@Service
public class StudentService {
    private final StudentRepository studentRepo;
    private final RestTemplate restTemplate;
    private static final String FACE_SERVICE_URL = "http://127.0.0.1:5001";

    public StudentService(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(factory);
    }

    public Map<String, Object> enroll(String username, String name, String faceImage, String className, String studentId) {
        Map<String, Object> result = new HashMap<>();
        if (username == null || username.isEmpty() || name == null || name.isEmpty()) {
            result.put("success", false);
            result.put("message", "参数不完整");
            return result;
        }
        try {
            Map<String, Object> faceReq = new HashMap<>();
            faceReq.put("name", name);
            faceReq.put("faceImage", faceImage);
            Map<String, Object> faceResp = restTemplate.postForObject(
                FACE_SERVICE_URL + "/enroll", faceReq, Map.class);
            if (faceResp != null && Boolean.TRUE.equals(faceResp.get("success"))) {
                Student student;
                if (studentRepo.existsByUsernameAndName(username, name)) {
                    student = studentRepo.findByUsernameAndName(username, name).get();
                } else {
                    student = new Student();
                    student.setUsername(username);
                    student.setName(name);
                }
                student.setFaceImage(faceImage);
                if (className != null && !className.isEmpty()) {
                    student.setClassName(className);
                }
                student.setStudentId(studentId != null && !studentId.isEmpty() ? studentId : "S" + System.currentTimeMillis());
                studentRepo.save(student);
                result.put("success", true);
                result.put("message", "注册成功");
            } else {
                String msg = (faceResp != null && faceResp.get("message") != null)
                    ? faceResp.get("message").toString() : "人脸服务错误";
                result.put("success", false);
                result.put("message", msg);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "人脸服务连接失败: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> batchImport(String username, List<String> names, String className, List<String> studentIds) {
        Map<String, Object> result = new HashMap<>();
        int success = 0, fail = 0;
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (name == null || name.trim().isEmpty()) continue;
            name = name.trim();
            try {
                if (!studentRepo.existsByUsernameAndName(username, name)) {
                    Student s = new Student();
                    s.setUsername(username);
                    s.setName(name);
                    s.setFaceImage("");
                    s.setClassName(className != null ? className : "");
                    String sid = (studentIds != null && i < studentIds.size()) ? studentIds.get(i) : "";
                    if (sid.isEmpty()) sid = "S" + System.currentTimeMillis() + i;
                    s.setStudentId(sid);
                    studentRepo.save(s);
                    success++;
                } else {
                    fail++;
                }
            } catch (Exception e) {
                fail++;
            }
        }
        result.put("success", true);
        result.put("imported", success);
        result.put("skipped", fail);
        result.put("message", "成功导入 " + success + " 人" + (fail > 0 ? "，跳过 " + fail + " 人（已存在）" : ""));
        return result;
    }

    public void delete(String username, String name) {
        if (username == null || name == null) return;
        studentRepo.findByUsernameAndName(username, name).ifPresent(s -> studentRepo.delete(s));
    }

    public List<String> getUnregistered(String username) {
        List<Student> all = studentRepo.findByUsername(username);
        List<String> result = new ArrayList<>();
        for (Student s : all) {
            if (s.getFaceImage() == null || s.getFaceImage().isEmpty()) {
                result.add(s.getName());
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<Student> list(String username) {
        if (username == null) return new ArrayList<>();
        return studentRepo.findByUsername(username);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> recognize(String username, String faceImage, List<Integer> bbox) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "");
        result.put("confidence", 0);
        result.put("isMatch", false);
        try {
            List<Student> students = studentRepo.findByUsername(username);
            if (students.isEmpty()) {
                result.put("success", false);
                result.put("message", "暂无注册学生");
                return result;
            }

            List<Map<String, String>> studentNames = new ArrayList<>();
            for (Student s : students) {
                Map<String, String> entry = new HashMap<>();
                entry.put("name", s.getName());
                studentNames.add(entry);
            }

            Map<String, Object> faceReq = new HashMap<>();
            faceReq.put("faceImage", faceImage);
            faceReq.put("students", studentNames);
            if (bbox != null && bbox.size() == 4) {
                faceReq.put("bbox", bbox);
            }

            Map<String, Object> faceResp = restTemplate.postForObject(
                FACE_SERVICE_URL + "/recognize", faceReq, Map.class);

            if (faceResp != null) {
                result.put("success", faceResp.getOrDefault("success", false));
                result.put("name", faceResp.getOrDefault("name", ""));
                result.put("confidence", faceResp.getOrDefault("confidence", 0));
                result.put("isMatch", faceResp.getOrDefault("isMatch", false));
                result.put("message", faceResp.getOrDefault("message", ""));
                result.put("faceBox", faceResp.getOrDefault("faceBox", null));
            } else {
                result.put("success", false);
                result.put("message", "人脸服务无响应");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "人脸识别失败: " + e.getMessage());
        }
        return result;
    }
}
