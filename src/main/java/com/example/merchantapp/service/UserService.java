package com.example.merchantapp.service;

import com.example.merchantapp.model.User;
import com.example.merchantapp.model.Role;
import com.example.merchantapp.repository.UserRepository;
import com.example.merchantapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 根据用户名查找用户
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 创建新用户，设置注册时间
    public User createUser(String username, String rawPassword, Set<String> roleNames) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));

        // 设置注册时间为当前北京时间
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        user.setLastLoginAt(null);  // 初始时，上次登录时间为空

        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            Role defaultRole = roleRepository.findByName("MERCHANT")
                .orElseThrow(() -> new RuntimeException("Default role MERCHANT not found"));
            roles.add(defaultRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    // 更新最后登录时间
    public LocalDateTime updateLastLoginTime(User user) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        LocalDateTime lastLoginTime = user.getLastLoginAt();
        user.setLastLoginAt(now);
        userRepository.save(user);
        return lastLoginTime;
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 按ID获取用户
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 更新用户信息
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
                user.setRoles(updatedUser.getRoles());
            }
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 删除用户：假删除，用户名加上后缀 "_delete"
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String updatedUsername = user.getUsername() + "_delete";
        user.setUsername(updatedUsername);  // 假删除，修改用户名
        userRepository.save(user);
    }
}
