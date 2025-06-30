package com.agridata.repository;

import com.agridata.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // 管理员相关查询
    List<User> findByRoleIn(List<User.UserRole> roles);
    Page<User> findByRoleInOrderByCreateTimeDesc(List<User.UserRole> roles, Pageable pageable);
    
    // 根据角色查询
    List<User> findByRole(User.UserRole role);
    Page<User> findByRoleOrderByCreateTimeDesc(User.UserRole role, Pageable pageable);
    
    // 根据状态查询
    Page<User> findByStatusOrderByCreateTimeDesc(User.UserStatus status, Pageable pageable);
    
    // 根据角色和状态查询
    Page<User> findByRoleAndStatusOrderByCreateTimeDesc(User.UserRole role, User.UserStatus status, Pageable pageable);
    
    // 查询特定角色的用户
    Optional<User> findFirstByRole(User.UserRole role);
    
    // 统计方法
    Long countByRole(User.UserRole role);
    Long countByStatus(User.UserStatus status);
    Long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 搜索用户
    @Query("{ $or: [ " +
           "{ 'username': { $regex: ?0, $options: 'i' } }, " +
           "{ 'email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'phone': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<User> searchUsers(String keyword, Pageable pageable);
} 