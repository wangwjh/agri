package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.model.OperationLog;
import com.agridata.model.User;
import com.agridata.service.AdminService;
import com.agridata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    // ==================== 系统统计 ====================
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats(Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要管理员权限"));
            }
            
            Map<String, Object> stats = adminService.getSystemStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            System.err.println("获取统计信息异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取统计信息失败: " + e.getMessage()));
        }
    }

    // ==================== 用户管理 ====================
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(
            @RequestParam(required = false) User.UserRole role,
            @RequestParam(required = false) User.UserStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "USER_MANAGEMENT")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要用户管理权限"));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = adminService.getUsers(role, status, keyword, pageable);
            
            // 清空密码字段
            users.getContent().forEach(user -> user.setPassword(null));
            
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            System.err.println("获取用户列表异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String userId, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "USER_MANAGEMENT")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要用户管理权限"));
            }
            
            User user = adminService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
            
            user.setPassword(null); // 清空密码字段
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            System.err.println("获取用户信息异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取用户信息失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Object>> updateUserStatus(
            @PathVariable String userId,
            @RequestParam User.UserStatus status,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "USER_MANAGEMENT")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要用户管理权限"));
            }
            
            Map<String, Object> result = adminService.updateUserStatus(
                currentUser.getId(), currentUser.getUsername(), currentUser.getRole(),
                userId, status, reason);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            System.err.println("更新用户状态异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("更新用户状态失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Object>> updateUserRole(
            @PathVariable String userId,
            @RequestParam User.UserRole role,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ADMIN_MANAGEMENT")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要管理员管理权限"));
            }
            
            Map<String, Object> result = adminService.updateUserRole(
                currentUser.getId(), currentUser.getUsername(), currentUser.getRole(),
                userId, role, reason);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            System.err.println("更新用户角色异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("更新用户角色失败: " + e.getMessage()));
        }
    }

    // ==================== 管理员管理 ====================
    
    @PostMapping("/admins/test")
    public ResponseEntity<ApiResponse<Object>> testCreateAdmin() {
        System.out.println("=== 测试创建管理员路径映射 ===");
        return ResponseEntity.ok(ApiResponse.success("路径映射正常", null));
    }
    
    @PostMapping(value = "/manage/create-admin-user", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ApiResponse<Object>> createAdmin(
            @RequestBody CreateAdminRequest request,
            Authentication authentication) {
        System.out.println("=== 创建管理员API调用开始 ===");
        System.out.println("请求时间: " + java.time.LocalDateTime.now());
        System.out.println("Authentication对象: " + authentication);
        System.out.println("请求对象: " + request);
        
        if (request != null) {
            System.out.println("用户名: " + request.getUsername());
            System.out.println("邮箱: " + request.getEmail());
            System.out.println("角色: " + request.getRole());
            System.out.println("密码长度: " + (request.getPassword() != null ? request.getPassword().length() : "null"));
        }
        
        try {
            System.out.println("开始获取当前用户...");
            User currentUser = getCurrentUser(authentication);
            System.out.println("当前用户获取结果: " + (currentUser != null ? currentUser.getUsername() + "(" + currentUser.getRole() + ")" : "null"));
            
            if (currentUser == null) {
                System.out.println("getCurrentUser返回null，权限验证失败");
                return ResponseEntity.status(403).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            System.out.println("开始权限检查...");
            boolean hasPermission = adminService.hasPermission(currentUser, "ADMIN_MANAGEMENT");
            System.out.println("权限检查结果: " + hasPermission);
            
            if (!hasPermission) {
                System.out.println("权限不足：用户角色=" + currentUser.getRole() + "，需要SUPER_ADMIN");
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，只有超级管理员可以创建管理员"));
            }
            
            System.out.println("权限验证通过，开始调用AdminService.createAdmin...");
            Map<String, Object> result = adminService.createAdmin(
                currentUser.getId(), currentUser.getUsername(), currentUser.getRole(),
                request.getUsername(), request.getPassword(), request.getEmail(), request.getRole());
            
            System.out.println("AdminService.createAdmin调用完成，结果: " + result);
            
            if ((Boolean) result.get("success")) {
                System.out.println("创建管理员成功");
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                System.out.println("创建管理员失败: " + result.get("message"));
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            System.err.println("=== 创建管理员异常详情 ===");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("异常消息: " + e.getMessage());
            System.err.println("异常发生时间: " + java.time.LocalDateTime.now());
            System.err.println("=== 异常堆栈跟踪 ===");
            e.printStackTrace();
            System.err.println("=== 异常详情结束 ===");
            return ResponseEntity.internalServerError().body(ApiResponse.error("创建管理员失败: " + e.getMessage()));
        }
    }

    // ==================== 操作日志 ====================
    
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Page<OperationLog>>> getOperationLogs(
            @RequestParam(required = false) String operatorId,
            @RequestParam(required = false) OperationLog.OperationType operationType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("权限不足"));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<OperationLog> logs = adminService.getOperationLogs(operatorId, operationType, targetType, startTime, endTime, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(logs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取操作日志失败: " + e.getMessage()));
        }
    }
    
    // ==================== 工具方法 ====================
    
    private User getCurrentUser(Authentication authentication) {
        System.out.println("=== getCurrentUser调试 ===");
        
        if (authentication == null) {
            System.out.println("Authentication is null");
            return null;
        }
        
        System.out.println("Authentication type: " + authentication.getClass().getName());
        System.out.println("Authentication.getName(): " + authentication.getName());
        System.out.println("Authentication.getPrincipal(): " + authentication.getPrincipal());
        System.out.println("Authentication.getPrincipal() type: " + authentication.getPrincipal().getClass().getName());
        
        String username = null;
        
        // 尝试多种方式获取用户名
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            System.out.println("从UserDetails获取用户名: " + username);
        } else if (principal instanceof String) {
            username = (String) principal;
            System.out.println("Principal是字符串: " + username);
        } else {
            username = authentication.getName();
            System.out.println("使用authentication.getName(): " + username);
        }
        
        if (username == null || username.isEmpty()) {
            System.out.println("无法获取用户名");
            return null;
        }
        
        // 通过UserService从数据库获取完整用户信息
        User user = userService.getCurrentUser(username);
        System.out.println("最终获取的用户: " + (user != null ? user.getUsername() + "(" + user.getRole() + ")" : "null"));
        return user;
    }

    // ==================== 请求DTO类 ====================
    
    public static class CreateAdminRequest {
        private String username;
        private String password;
        private String email;
        private User.UserRole role;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public User.UserRole getRole() { return role; }
        public void setRole(User.UserRole role) { this.role = role; }
        
        @Override
        public String toString() {
            return "CreateAdminRequest{username='" + username + "', email='" + email + "', role=" + role + "}";
        }
    }

    @GetMapping("/debug/current-user")
    public ResponseEntity<ApiResponse<Object>> debugCurrentUser(Authentication authentication) {
        System.out.println("=== 调试当前用户信息 ===");
        
        Map<String, Object> debugInfo = new HashMap<>();
        
        if (authentication == null) {
            debugInfo.put("authentication", "null");
            return ResponseEntity.ok(ApiResponse.success("调试信息", debugInfo));
        }
        
        debugInfo.put("authType", authentication.getClass().getName());
        debugInfo.put("name", authentication.getName());
        debugInfo.put("principal", authentication.getPrincipal().toString());
        debugInfo.put("isAuthenticated", authentication.isAuthenticated());
        
        User currentUser = getCurrentUser(authentication);
        if (currentUser != null) {
            debugInfo.put("currentUser", Map.of(
                "id", currentUser.getId(),
                "username", currentUser.getUsername(),
                "role", currentUser.getRole(),
                "status", currentUser.getStatus()
            ));
            
            debugInfo.put("permissions", Map.of(
                "USER_MANAGEMENT", adminService.hasPermission(currentUser, "USER_MANAGEMENT"),
                "ADMIN_MANAGEMENT", adminService.hasPermission(currentUser, "ADMIN_MANAGEMENT"),
                "ASSET_REVIEW", adminService.hasPermission(currentUser, "ASSET_REVIEW"),
                "ASSET_PUBLISH", adminService.hasPermission(currentUser, "ASSET_PUBLISH")
            ));
        } else {
            debugInfo.put("currentUser", "null");
        }
        
        return ResponseEntity.ok(ApiResponse.success("调试信息", debugInfo));
    }
} 