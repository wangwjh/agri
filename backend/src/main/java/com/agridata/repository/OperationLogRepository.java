package com.agridata.repository;

import com.agridata.model.OperationLog;
import com.agridata.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends MongoRepository<OperationLog, String> {
    
    // 根据操作者查询
    Page<OperationLog> findByOperatorIdOrderByCreateTimeDesc(String operatorId, Pageable pageable);
    
    // 根据操作类型查询
    Page<OperationLog> findByOperationTypeOrderByCreateTimeDesc(OperationLog.OperationType operationType, Pageable pageable);
    
    // 根据操作模块查询
    Page<OperationLog> findByOperationModuleOrderByCreateTimeDesc(String operationModule, Pageable pageable);
    
    // 根据目标类型查询
    Page<OperationLog> findByTargetTypeOrderByCreateTimeDesc(String targetType, Pageable pageable);
    
    // 根据时间范围查询
    Page<OperationLog> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // 根据操作者角色查询
    Page<OperationLog> findByOperatorRoleOrderByCreateTimeDesc(User.UserRole operatorRole, Pageable pageable);
    
    // 复合查询
    @Query("{ $and: [ " +
           "{ $or: [ { 'operatorId': ?0 }, { ?0: null } ] }, " +
           "{ $or: [ { 'operationType': ?1 }, { ?1: null } ] }, " +
           "{ $or: [ { 'targetType': ?2 }, { ?2: null } ] }, " +
           "{ $or: [ { 'createTime': { $gte: ?3 } }, { ?3: null } ] }, " +
           "{ $or: [ { 'createTime': { $lte: ?4 } }, { ?4: null } ] } " +
           "] }")
    Page<OperationLog> findByConditions(String operatorId, OperationLog.OperationType operationType, 
                                       String targetType, LocalDateTime startTime, LocalDateTime endTime, 
                                       Pageable pageable);
    
    // 统计相关方法
    Long countByOperatorIdAndCreateTimeBetween(String operatorId, LocalDateTime startTime, LocalDateTime endTime);
    
    Long countByOperationTypeAndCreateTimeBetween(OperationLog.OperationType operationType, LocalDateTime startTime, LocalDateTime endTime);
    
    // 查询失败的操作
    Page<OperationLog> findBySuccessFalseOrderByCreateTimeDesc(Pageable pageable);
} 