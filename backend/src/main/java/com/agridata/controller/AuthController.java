package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.dto.LoginRequest;
import com.agridata.dto.RegisterRequest;
import com.agridata.dto.ChangePasswordRequest;
import com.agridata.dto.UpdateProfileRequest;
import com.agridata.model.User;
import com.agridata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Map<String, Object> result = userService.register(request);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("注册失败: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userService.login(request);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            User user = userService.getCurrentUser(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }

            // 清空密码字段
            user.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取用户信息失败: " + e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            Map<String, Object> result = userService.changePassword(
                authentication.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
            );

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("密码修改失败: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            Map<String, Object> result = userService.updateProfile(
                authentication.getName(),
                request.getUsername(),
                request.getEmail(),
                request.getPhone(),
                request.getLocation(),
                request.getBio()
            );

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("个人资料更新失败: " + e.getMessage()));
        }
    }
} 