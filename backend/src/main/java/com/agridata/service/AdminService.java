package com.agridata.service;

import com.agridata.model.OperationLog;
import com.agridata.model.User;
import com.agridata.repository.OperationLogRepository;
import com.agridata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== 用户管理 ====================
    
    /**
     * 获取用户列表（分页）
     */
    public Page<User> getUsers(User.UserRole role, User.UserStatus status, String keyword, Pageable pageable) {
        // 根据不同的参数组合使用不同的查询方法
        if (role != null && status != null) {
            // 同时指定角色和状态
            return userRepository.findByRoleAndStatusOrderByCreateTimeDesc(role, status, pageable);
        } else if (role != null) {
            // 只指定角色
            return userRepository.findByRoleOrderByCreateTimeDesc(role, pageable);
        } else if (status != null) {
            // 只指定状态
            return userRepository.findByStatusOrderByCreateTimeDesc(status, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // 只有关键字搜索
            return userRepository.searchUsers(keyword.trim(), pageable);
        } else {
            // 获取所有用户
            return userRepository.findAll(pageable);
        }
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    /**
     * 更新用户状态
     */
    public Map<String, Object> updateUserStatus(String adminId, String adminName, User.UserRole adminRole,
                                               String userId, User.UserStatus newStatus, String reason) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return response;
            }
            
            User user = userOptional.get();
            User.UserStatus oldStatus = user.getStatus();
            user.setStatus(newStatus);
            user.setUpdateTime(LocalDateTime.now());
            
            userRepository.save(user);
            
            // 记录操作日志
            OperationLog log = OperationLog.create(adminId, adminName, adminRole,
                newStatus == User.UserStatus.SUSPENDED ? OperationLog.OperationType.USER_SUSPEND :
                newStatus == User.UserStatus.ACTIVE ? OperationLog.OperationType.USER_ACTIVATE :
                OperationLog.OperationType.USER_UPDATE, "USER", userId);
            log.setTargetName(user.getUsername());
            log.getOperationDetails().put("oldStatus", oldStatus);
            log.getOperationDetails().put("newStatus", newStatus);
            log.getOperationDetails().put("reason", reason);
            operationLogRepository.save(log);
            
            response.put("success", true);
            response.put("message", "用户状态更新成功");
            response.put("user", createUserResponse(user));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 更新用户角色
     */
    public Map<String, Object> updateUserRole(String adminId, String adminName, User.UserRole adminRole,
                                             String userId, User.UserRole newRole, String reason) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 权限检查：只有超级管理员可以修改角色
            if (adminRole != User.UserRole.SUPER_ADMIN) {
                response.put("success", false);
                response.put("message", "权限不足，只有超级管理员可以修改用户角色");
                return response;
            }
            
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return response;
            }
            
            User user = userOptional.get();
            User.UserRole oldRole = user.getRole();
            
            // 不能修改超级管理员的角色
            if (oldRole == User.UserRole.SUPER_ADMIN) {
                response.put("success", false);
                response.put("message", "不能修改超级管理员的角色");
                return response;
            }
            
            user.setRole(newRole);
            user.setUpdateTime(LocalDateTime.now());
            
            userRepository.save(user);
            
            // 记录操作日志
            OperationLog log = OperationLog.create(adminId, adminName, adminRole,
                OperationLog.OperationType.USER_ROLE_CHANGE, "USER", userId);
            log.setTargetName(user.getUsername());
            log.getOperationDetails().put("oldRole", oldRole);
            log.getOperationDetails().put("newRole", newRole);
            log.getOperationDetails().put("reason", reason);
            operationLogRepository.save(log);
            
            response.put("success", true);
            response.put("message", "用户角色更新成功");
            response.put("user", createUserResponse(user));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 创建管理员账户
     */
    public Map<String, Object> createAdmin(String adminId, String adminName, User.UserRole adminRole,
                                          String username, String password, String email, 
                                          User.UserRole newAdminRole) {
        System.out.println("=== AdminService.createAdmin调用开始 ===");
        System.out.println("adminId: " + adminId);
        System.out.println("adminName: " + adminName);
        System.out.println("adminRole: " + adminRole);
        System.out.println("username: " + username);
        System.out.println("email: " + email);
        System.out.println("newAdminRole: " + newAdminRole);
        System.out.println("passwordEncoder是否为null: " + (passwordEncoder == null));
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 权限检查：只有超级管理员可以创建管理员
            if (adminRole != User.UserRole.SUPER_ADMIN) {
                response.put("success", false);
                response.put("message", "权限不足，只有超级管理员可以创建管理员");
                return response;
            }
            
            // 检查用户名和邮箱是否已存在
            if (userRepository.existsByUsername(username)) {
                response.put("success", false);
                response.put("message", "用户名已存在");
                return response;
            }
            
            if (userRepository.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "邮箱已被注册");
                return response;
            }
            
            // 创建管理员用户
            User newAdmin = new User();
            newAdmin.setUsername(username);
            newAdmin.setPassword(passwordEncoder.encode(password));
            newAdmin.setEmail(email);
            newAdmin.setRole(newAdminRole);
            newAdmin.setStatus(User.UserStatus.ACTIVE);
            newAdmin.setCreateTime(LocalDateTime.now());
            newAdmin.setUpdateTime(LocalDateTime.now());
            
            User savedAdmin = userRepository.save(newAdmin);
            
            // 记录操作日志
            OperationLog log = OperationLog.create(adminId, adminName, adminRole,
                OperationLog.OperationType.ADMIN_CREATE, "ADMIN", savedAdmin.getId());
            log.setTargetName(savedAdmin.getUsername());
            log.getOperationDetails().put("newAdminRole", newAdminRole);
            operationLogRepository.save(log);
            
            response.put("success", true);
            response.put("message", "管理员创建成功");
            response.put("admin", createUserResponse(savedAdmin));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
        }
        
        return response;
    }
    
    // ==================== 统计信息 ====================
    
    /**
     * 获取系统统计信息
     */
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 用户统计
            long totalUsers = userRepository.countByRole(User.UserRole.USER);
            long activeUsers = userRepository.countByStatus(User.UserStatus.ACTIVE);
            long suspendedUsers = userRepository.countByStatus(User.UserStatus.SUSPENDED);
            
            // 管理员统计
            List<User.UserRole> adminRoles = Arrays.asList(
                User.UserRole.SUPER_ADMIN, User.UserRole.REVIEWER, 
                User.UserRole.PUBLISHER, User.UserRole.OPERATOR
            );
            long totalAdmins = userRepository.findByRoleIn(adminRoles).size();
            
            // 今日新增用户
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            long todayNewUsers = userRepository.countByCreateTimeBetween(todayStart, todayEnd);
            
            stats.put("users", Map.of(
                "total", totalUsers,
                "active", activeUsers,
                "suspended", suspendedUsers,
                "todayNew", todayNewUsers
            ));
            
            stats.put("admins", Map.of(
                "total", totalAdmins,
                "superAdmin", userRepository.countByRole(User.UserRole.SUPER_ADMIN),
                "reviewer", userRepository.countByRole(User.UserRole.REVIEWER),
                "publisher", userRepository.countByRole(User.UserRole.PUBLISHER),
                "operator", userRepository.countByRole(User.UserRole.OPERATOR)
            ));
            
        } catch (Exception e) {
            stats.put("error", "获取统计信息失败: " + e.getMessage());
        }
        
        return stats;
    }
    
    // ==================== 操作日志 ====================
    
    /**
     * 获取操作日志
     */
    public Page<OperationLog> getOperationLogs(String operatorId, OperationLog.OperationType operationType, 
                                             String targetType, LocalDateTime startTime, LocalDateTime endTime, 
                                             Pageable pageable) {
        return operationLogRepository.findByConditions(operatorId, operationType, targetType, startTime, endTime, pageable);
    }
    
    // ==================== 权限检查 ====================
    
    /**
     * 检查用户是否有管理员权限
     */
    public boolean isAdmin(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.getRole() != User.UserRole.USER;
    }
    
    /**
     * 检查用户是否有特定权限
     */
    public boolean hasPermission(User user, String permission) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        
        User.UserRole role = user.getRole();
        
        switch (permission) {
            case "USER_MANAGEMENT":
                return role == User.UserRole.SUPER_ADMIN || role == User.UserRole.OPERATOR;
            case "ASSET_REVIEW":
                return role == User.UserRole.SUPER_ADMIN || role == User.UserRole.REVIEWER;
            case "ASSET_PUBLISH":
                return role == User.UserRole.SUPER_ADMIN || role == User.UserRole.PUBLISHER;
            case "ADMIN_MANAGEMENT":
                return role == User.UserRole.SUPER_ADMIN;
            case "SYSTEM_CONFIG":
                return role == User.UserRole.SUPER_ADMIN;
            default:
                return false;
        }
    }
    
    // ==================== 初始化超级管理员 ====================
    
    /**
     * 初始化超级管理员（如果不存在）
     */
    public void initSuperAdmin() {
        try {
            Optional<User> superAdmin = userRepository.findFirstByRole(User.UserRole.SUPER_ADMIN);
            if (superAdmin.isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@agridata.com");
                admin.setRole(User.UserRole.SUPER_ADMIN);
                admin.setStatus(User.UserStatus.ACTIVE);
                admin.setCreateTime(LocalDateTime.now());
                admin.setUpdateTime(LocalDateTime.now());
                
                userRepository.save(admin);
                System.out.println("超级管理员初始化成功: admin/admin123");
            }
        } catch (Exception e) {
            System.err.println("超级管理员初始化失败: " + e.getMessage());
        }
    }
    
    // ==================== 工具方法 ====================
    
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
        userResponse.put("updateTime", user.getUpdateTime());
        userResponse.put("lastLoginTime", user.getLastLoginTime());
        return userResponse;
    }
} 