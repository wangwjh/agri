package com.agridata.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    private String password;
    
    @Indexed(unique = true)
    private String email;
    
    private String avatar;
    
    private String phone;
    
    private String location;
    
    private String bio;
    
    // 用户角色
    private UserRole role = UserRole.USER;
    
    // 账户状态
    private UserStatus status = UserStatus.ACTIVE;
    
    private List<String> ownedAssets = new ArrayList<>();
    
    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime = LocalDateTime.now();
    
    // 最后登录时间
    private LocalDateTime lastLoginTime;
    
    // 账户余额
    private Double balance = 0.0;
    
    // 冻结余额（用于待处理交易）
    private Double frozenBalance = 0.0;
    
    // 构造函数
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // 用户角色枚举
    public enum UserRole {
        USER("普通用户"),
        REVIEWER("审核管理员"),
        PUBLISHER("发布管理员"),
        OPERATOR("运营管理员"),
        SUPER_ADMIN("超级管理员");
        
        private final String description;
        
        UserRole(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 用户状态枚举
    public enum UserStatus {
        ACTIVE("正常"),
        SUSPENDED("暂停"),
        BANNED("禁用");
        
        private final String description;
        
        UserStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 余额信息类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceInfo {
        private Double totalBalance;    // 总余额
        private Double frozenBalance;   // 冻结余额
        private Double availableBalance; // 可用余额
    }
} 