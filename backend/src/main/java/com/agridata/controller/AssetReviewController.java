package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.model.AgriculturalAsset;
import com.agridata.model.User;
import com.agridata.service.AdminService;
import com.agridata.service.AssetReviewService;
import com.agridata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/assets")
@CrossOrigin(origins = "*")
public class AssetReviewController {

    @Autowired
    private AssetReviewService assetReviewService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserService userService;

    // ==================== 资产统计 ====================
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssetStats(Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要管理员权限"));
            }
            
            Map<String, Object> stats = assetReviewService.getAssetStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            System.err.println("获取资产统计异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取资产统计失败: " + e.getMessage()));
        }
    }

    // ==================== 资产审核 ====================
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<AgriculturalAsset>>> getPendingAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_REVIEW")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产审核权限"));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<AgriculturalAsset> assets = assetReviewService.getPendingAssets(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(assets));
        } catch (Exception e) {
            System.err.println("获取待审核资产异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取待审核资产失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/review")
    public ResponseEntity<ApiResponse<Page<AgriculturalAsset>>> getAssetsByReviewStatus(
            @RequestParam(required = false) AgriculturalAsset.ReviewStatus reviewStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_REVIEW")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产审核权限"));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<AgriculturalAsset> assets = assetReviewService.getAssetsByReviewStatus(reviewStatus, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(assets));
        } catch (Exception e) {
            System.err.println("获取资产列表异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取资产列表失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{assetId}/approve")
    public ResponseEntity<ApiResponse<Object>> approveAsset(
            @PathVariable String assetId,
            @RequestParam(required = false) String comment,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_REVIEW")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产审核权限"));
            }
            
            Map<String, Object> result = assetReviewService.approveAsset(
                currentUser.getId(), currentUser.getUsername(), assetId, comment);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            System.err.println("审核通过异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("审核通过失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{assetId}/reject")
    public ResponseEntity<ApiResponse<Object>> rejectAsset(
            @PathVariable String assetId,
            @RequestParam(required = false) String comment,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_REVIEW")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产审核权限"));
            }
            
            Map<String, Object> result = assetReviewService.rejectAsset(
                currentUser.getId(), currentUser.getUsername(), assetId, comment);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            System.err.println("审核拒绝异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("审核拒绝失败: " + e.getMessage()));
        }
    }

    // ==================== 资产发布 ====================
    
    @GetMapping("/approved-unpublished")
    public ResponseEntity<ApiResponse<Page<AgriculturalAsset>>> getApprovedUnpublishedAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_PUBLISH")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产发布权限"));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<AgriculturalAsset> assets = assetReviewService.getApprovedUnpublishedAssets(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(assets));
        } catch (Exception e) {
            System.err.println("获取待发布资产异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取待发布资产失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/published")
    public ResponseEntity<ApiResponse<Page<AgriculturalAsset>>> getPublishedAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_PUBLISH")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产发布权限"));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<AgriculturalAsset> assets = assetReviewService.getPublishedAssets(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(assets));
        } catch (Exception e) {
            System.err.println("获取已发布资产异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取已发布资产失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{assetId}/publish")
    public ResponseEntity<ApiResponse<Object>> publishAsset(
            @PathVariable String assetId,
            @RequestParam(required = false) String comment,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_PUBLISH")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产发布权限"));
            }
            
            Map<String, Object> result = assetReviewService.publishAsset(
                currentUser.getId(), currentUser.getUsername(), assetId, comment);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            System.err.println("发布资产异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("发布资产失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{assetId}/offline")
    public ResponseEntity<ApiResponse<Object>> offlineAsset(
            @PathVariable String assetId,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!adminService.hasPermission(currentUser, "ASSET_PUBLISH")) {
                return ResponseEntity.status(403).body(ApiResponse.error("权限不足，需要资产发布权限"));
            }
            
            Map<String, Object> result = assetReviewService.offlineAsset(
                currentUser.getId(), currentUser.getUsername(), currentUser.getRole(), assetId, reason);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.get("message").toString()));
            }
        } catch (Exception e) {
            System.err.println("下线资产异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("下线资产失败: " + e.getMessage()));
        }
    }
    
    // ==================== 工具方法 ====================
    
    private User getCurrentUser(Authentication authentication) {
        System.out.println("=== AssetReviewController.getCurrentUser调试 ===");
        
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
} 