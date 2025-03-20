package com.example.merchantapp.controller;

import com.example.merchantapp.config.JwtUtil;
import com.example.merchantapp.model.LoginLog;
import com.example.merchantapp.repository.LoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LoginLogRepository loginLogRepository;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 进行认证
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password)
            );
            // 如果认证通过，生成JWT令牌
            String token = jwtUtil.generateToken(request.username);
            // 记录成功登录日志
            LoginLog log = new LoginLog();
            log.setUsername(request.username);
            log.setSuccess(true);
            log.setTimestamp(LocalDateTime.now());
            loginLogRepository.save(log);
            // 返回 Token 给前端
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException ex) {
            // 登录失败，记录失败日志
            LoginLog log = new LoginLog();
            log.setUsername(request.username);
            log.setSuccess(false);
            log.setTimestamp(LocalDateTime.now());
            loginLogRepository.save(log);
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
    }
}
