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
import java.util.HashMap;
import java.util.Map;
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
    private UserService userService;

    // 登录请求DTO
    static class LoginRequest {
        public String username;
        public String password;
    }

    // 登录响应DTO
    static class LoginResponse {
        public String token;
        public LocalDateTime lastLoginAt;  // 返回上次登录时间
        public LoginResponse(String token, LocalDateTime lastLoginAt) {
            this.token = token;
            this.lastLoginAt = lastLoginAt;
        }
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

            // 获取用户并更新登录时间
            Optional<User> userOpt = userService.findByUsername(request.username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                LocalDateTime lastLoginTime = userService.updateLastLoginTime(user);

                // 记录登录日志
                LoginLog log = new LoginLog();
                log.setUsername(request.username);
                log.setSuccess(true);
                log.setTimestamp(LocalDateTime.now());
                loginLogRepository.save(log);

                // 返回Token及上次登录时间
                return ResponseEntity.ok(new LoginResponse(token, lastLoginTime));
            } else {
                return ResponseEntity.status(401).body("用户不存在");
            }
        } catch (AuthenticationException ex) {
            // 记录登录失败日志
            LoginLog log = new LoginLog();
            log.setUsername(request.username);
            log.setSuccess(false);
            log.setTimestamp(LocalDateTime.now());
            loginLogRepository.save(log);
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Optional<User> existingUser = userService.findByUsername(request.username);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("用户名已存在");
        }

        try {
            // 创建用户
            User newUser = userService.createUser(request.username, request.password, request.roles);
            return ResponseEntity.status(201).body(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("创建用户失败");
        }
    }
}
