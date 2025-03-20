package com.example.merchantapp.controller;

import com.example.merchantapp.config.JwtUtil;
import com.example.merchantapp.model.User;
import com.example.merchantapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 认证控制器
 * 处理用户登录请求，返回JWT令牌
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return jwtUtil.generateToken(username, "USER");
        }
        throw new RuntimeException("Invalid username or password");
    }
}
