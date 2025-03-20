package com.example.merchantapp.controller;

import com.example.merchantapp.config.JwtUtil;
import com.example.merchantapp.model.LoginLog;
import com.example.merchantapp.model.User;
import com.example.merchantapp.repository.LoginLogRepository;
import com.example.merchantapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginLogRepository loginLogRepository;

    @Autowired
    private UserService userService;  // 注入 UserService 以便注册用户

    // 登录请求DTO
    static class LoginRequest {
        public String username;
        public String password;
    }

    // 登录响应DTO
    static class LoginResponse {
        public String token;
        public LoginResponse(String token) { this.token = token; }
    }

    // 注册请求DTO
    static class RegisterRequest {
        public String username;
        public String password;
        public Set<String> roles;  // 角色
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password)
            );
            String token = jwtUtil.generateToken(request.username);
            LoginLog log = new LoginLog();
            log.setUsername(request.username);
            log.setSuccess(true);
            log.setTimestamp(LocalDateTime.now());
            loginLogRepository.save(log);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException ex) {
            LoginLog log = new LoginLog();
            log.setUsername(request.username);
            log.setSuccess(false);
            log.setTimestamp(LocalDateTime.now());
            loginLogRepository.save(log);
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
    }

    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userService.findByUsername(request.username);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("用户名已存在");
        }

        try {
            // 创建用户
            User newUser = userService.createUser(request.username, request.password, request.roles);
            return ResponseEntity.status(201).body(newUser);  // 返回新用户信息，状态码 201 表示创建成功
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("创建用户失败");
        }
    }
}