package com.eye.controller;

import com.eye.entity.Student;
import com.eye.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/enroll")
    public Map<String, Object> enroll(@RequestBody Map<String, String> body) {
        return studentService.enroll(
            body.get("username"),
            body.get("name"),
            body.get("faceImage"),
            body.getOrDefault("className", ""),
            body.getOrDefault("studentId", "")
        );
    }

    @GetMapping("/list")
    public List<Student> list(@RequestParam String username) {
        return studentService.list(username);
    }

    @GetMapping("/unregistered")
    public List<String> getUnregistered(@RequestParam String username) {
        return studentService.getUnregistered(username);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/batch")
    public Map<String, Object> batchImport(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        List<String> names = (List<String>) body.get("names");
        String className = (String) body.getOrDefault("className", "");
        List<String> studentIds = (List<String>) body.getOrDefault("studentIds", new ArrayList<>());
        return studentService.batchImport(username, names, className, studentIds);
    }

    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam String username, @RequestParam String name) {
        studentService.delete(username, name);
        return Map.of("success", true, "message", "删除成功");
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/recognize")
    public Map<String, Object> recognize(@RequestBody Map<String, Object> body) {
        List<Integer> bbox = null;
        if (body.containsKey("bbox") && body.get("bbox") instanceof List) {
            bbox = (List<Integer>) body.get("bbox");
        }
        return studentService.recognize(
            (String) body.get("username"),
            (String) body.get("faceImage"),
            bbox
        );
    }
}
