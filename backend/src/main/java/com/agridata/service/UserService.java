package com.agridata.service;

import com.agridata.dto.LoginRequest;
import com.agridata.dto.RegisterRequest;
import com.agridata.model.User;
import com.agridata.repository.UserRepository;
import com.agridata.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> register(RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            response.put("success", false);
            response.put("message", "邮箱已被注册");
            return response;
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // 生成JWT Token
        String token = jwtUtil.generateToken(savedUser.getUsername());

        response.put("success", true);
        response.put("message", "注册成功");
        response.put("token", token);
        response.put("user", createUserResponse(savedUser));

        return response;
    }

    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return response;
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return response;
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getUsername());

        response.put("success", true);
        response.put("message", "登录成功");
        response.put("token", token);
        response.put("user", createUserResponse(user));

        return response;
    }

    public User getCurrentUser(String username) {
        System.out.println("UserService.getCurrentUser called with username: " + username);
        if (username == null || username.isEmpty()) {
            System.out.println("Username is null or empty, returning null");
            return null;
        }
        
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("Found user: " + user.getUsername() + ", role: " + user.getRole());
            return user;
        } else {
            System.out.println("User not found in database: " + username);
            return null;
        }
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public Map<String, Object> changePassword(String username, String currentPassword, String newPassword) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }

        User user = userOptional.get();

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            response.put("success", false);
            response.put("message", "当前密码错误");
            return response;
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "密码修改成功");
        return response;
    }

    public Map<String, Object> updateProfile(String username, String newUsername, String email, String phone, String location, String bio) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }

        User user = userOptional.get();

        // 检查新用户名是否已被其他用户使用
        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            if (userRepository.existsByUsername(newUsername)) {
                response.put("success", false);
                response.put("message", "用户名已被使用");
                return response;
            }
            user.setUsername(newUsername);
        }

        // 检查新邮箱是否已被其他用户使用
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "邮箱已被使用");
                return response;
            }
            user.setEmail(email);
        }

        // 更新其他字段
        if (phone != null) {
            user.setPhone(phone);
        }
        if (location != null) {
            user.setLocation(location);
        }
        if (bio != null) {
            user.setBio(bio);
        }

        user.setUpdateTime(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        response.put("success", true);
        response.put("message", "个人资料更新成功");
        response.put("user", createUserResponse(updatedUser));
        return response;
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("avatar", user.getAvatar());
        userResponse.put("phone", user.getPhone());
        userResponse.put("location", user.getLocation());
        userResponse.put("bio", user.getBio());
        userResponse.put("role", user.getRole());
        userResponse.put("status", user.getStatus());
        userResponse.put("createTime", user.getCreateTime());
        return userResponse;
    }
} 