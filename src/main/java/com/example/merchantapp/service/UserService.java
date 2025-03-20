package com.example.merchantapp.service;

import com.example.merchantapp.model.User;
import com.example.merchantapp.model.Role;
import com.example.merchantapp.repository.UserRepository;
import com.example.merchantapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 创建新用户（编码密码并赋予角色）
    public User createUser(String username, String rawPassword, Set<String> roleNames) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            // 如果未指定角色，赋予默认角色 "MERCHANT"
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

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 按ID获取用户
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 更新用户信息（如用户名、密码、角色等）
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            // 如果提供了新密码，则进行加密更新
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
                user.setRoles(updatedUser.getRoles());
            }
            // 可更新其他字段...
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 删除用户
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
