package com.eye.controller;

import com.eye.entity.Student;
import com.eye.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/class")
public class ClassController {
    private final StudentRepository studentRepo;

    public ClassController(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    @GetMapping("/list")
    public List<String> getClasses(@RequestParam String username) {
        List<Student> students = studentRepo.findByUsername(username);
        Set<String> classes = new LinkedHashSet<>();
        for (Student s : students) {
            if (s.getClassName() != null && !s.getClassName().isEmpty()) {
                classes.add(s.getClassName());
            }
        }
        List<String> result = new ArrayList<>(classes);
        Collections.sort(result);
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> createClass(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("className", body.get("className"));
        return result;
    }
}
