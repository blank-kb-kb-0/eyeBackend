package com.eye.service;

import com.eye.entity.User;
import com.eye.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        if (userRepository.existsByUsername(username)) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        User user = new User(username, password);
        userRepository.save(user);
        result.put("success", true);
        result.put("message", "注册成功");
        return result;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        var opt = userRepository.findByUsername(username);
        if (opt.isPresent() && opt.get().getPassword().equals(password)) {
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("username", username);
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    public Map<String, Object> getUserInfo(String username) {
        Map<String, Object> result = new HashMap<>();
        var opt = userRepository.findByUsername(username);
        if (opt.isPresent()) {
            User user = opt.get();
            result.put("success", true);
            result.put("username", user.getUsername());
            result.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt() : System.currentTimeMillis());
        } else {
            result.put("success", false);
        }
        return result;
    }
}