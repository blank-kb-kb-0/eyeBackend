package com.eye.controller;

import com.eye.service.UserService;
import com.eye.entity.User;
import com.eye.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        return userService.register(body.get("username"), body.get("password"));
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        return userService.login(body.get("username"), body.get("password"));
    }

    @GetMapping("/info")
    public Map<String, Object> info(@RequestParam String username) {
        Map<String, Object> result = userService.getUserInfo(username);
        return result;
    }
}