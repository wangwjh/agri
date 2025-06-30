package com.agridata.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "operation_logs")
public class OperationLog {
    @Id
    private String id;
    
    // 操作者信息
    @Indexed
    private String operatorId;
    private String operatorName;
    private User.UserRole operatorRole;
    
    // 操作信息
    @Indexed
    private OperationType operationType;
    private String operationModule;
    private String operationDescription;
    
    // 目标对象信息
    private String targetType; // 操作对象类型：USER, ASSET, ADMIN等
    private String targetId;   // 操作对象ID
    private String targetName; // 操作对象名称
    
    // 操作详情
    private Map<String, Object> operationDetails = new HashMap<>();
    
    // 操作结果
    private Boolean success = true;
    private String errorMessage;
    
    // IP地址和用户代理
    private String ipAddress;
    private String userAgent;
    
    @Indexed
    private LocalDateTime createTime = LocalDateTime.now();
    
    // 操作类型枚举
    public enum OperationType {
        // 用户管理
        USER_CREATE("创建用户"),
        USER_UPDATE("更新用户"),
        USER_DELETE("删除用户"),
        USER_SUSPEND("暂停用户"),
        USER_ACTIVATE("激活用户"),
        USER_ROLE_CHANGE("修改用户角色"),
        
        // 资产管理
        ASSET_REVIEW("审核资产"),
        ASSET_PUBLISH("发布资产"),
        ASSET_OFFLINE("下线资产"),
        ASSET_DELETE("删除资产"),
        
        // 管理员管理
        ADMIN_CREATE("创建管理员"),
        ADMIN_UPDATE("更新管理员"),
        ADMIN_DELETE("删除管理员"),
        ADMIN_ROLE_CHANGE("修改管理员角色"),
        
        // 系统管理
        SYSTEM_CONFIG("系统配置"),
        DATA_EXPORT("数据导出"),
        DATA_IMPORT("数据导入"),
        
        // 登录相关
        LOGIN("登录"),
        LOGOUT("退出登录"),
        LOGIN_FAILED("登录失败");
        
        private final String description;
        
        OperationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 快速创建日志的静态方法
    public static OperationLog create(String operatorId, String operatorName, User.UserRole operatorRole,
                                    OperationType operationType, String targetType, String targetId) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperatorRole(operatorRole);
        log.setOperationType(operationType);
        log.setOperationModule(getModuleFromType(operationType));
        log.setOperationDescription(operationType.getDescription());
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }
    
    private static String getModuleFromType(OperationType type) {
        String typeName = type.name();
        if (typeName.startsWith("USER_")) return "用户管理";
        if (typeName.startsWith("ASSET_")) return "资产管理";
        if (typeName.startsWith("ADMIN_")) return "管理员管理";
        if (typeName.startsWith("SYSTEM_")) return "系统管理";
        return "其他";
    }
} 