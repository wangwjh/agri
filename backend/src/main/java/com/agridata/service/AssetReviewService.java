package com.agridata.service;

import com.agridata.model.AgriculturalAsset;
import com.agridata.model.OperationLog;
import com.agridata.model.User;
import com.agridata.repository.AgriculturalAssetRepository;
import com.agridata.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AssetReviewService {

    @Autowired
    private AgriculturalAssetRepository assetRepository;
    
    @Autowired
    private OperationLogRepository operationLogRepository;

    // ==================== 资产审核 ====================
    
    /**
     * 获取待审核的资产列表
     */
    public Page<AgriculturalAsset> getPendingAssets(Pageable pageable) {
        return assetRepository.findByReviewStatusOrderByCreateTimeDesc(
            AgriculturalAsset.ReviewStatus.PENDING, pageable);
    }
    
    /**
     * 获取所有资产列表（按状态筛选）
     */
    public Page<AgriculturalAsset> getAssetsByReviewStatus(AgriculturalAsset.ReviewStatus reviewStatus, Pageable pageable) {
        if (reviewStatus == null) {
            return assetRepository.findAllByOrderByCreateTimeDesc(pageable);
        }
        return assetRepository.findByReviewStatusOrderByCreateTimeDesc(reviewStatus, pageable);
    }
    
    /**
     * 审核通过资产
     */
    public Map<String, Object> approveAsset(String reviewerId, String reviewerName, 
                                           String assetId, String comment) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<AgriculturalAsset> assetOptional = assetRepository.findById(assetId);
            if (assetOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "资产不存在");
                return response;
            }
            
            AgriculturalAsset asset = assetOptional.get();
            
            // 检查当前状态
            if (asset.getReviewStatus() != AgriculturalAsset.ReviewStatus.PENDING) {
                response.put("success", false);
                response.put("message", "该资产已经审核过了");
                return response;
            }
            
            // 更新审核状态
            asset.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset.setReviewerId(reviewerId);
            asset.setReviewerName(reviewerName);
            asset.setReviewComment(comment);
            asset.setReviewTime(LocalDateTime.now());
            asset.setUpdateTime(LocalDateTime.now());
            
            assetRepository.save(asset);
            
            // 记录操作日志
            OperationLog log = OperationLog.create(reviewerId, reviewerName, User.UserRole.REVIEWER,
                OperationLog.OperationType.ASSET_REVIEW, "ASSET", assetId);
            log.setTargetName(asset.getName());
            log.getOperationDetails().put("action", "APPROVE");
            log.getOperationDetails().put("comment", comment);
            operationLogRepository.save(log);
            
            response.put("success", true);
            response.put("message", "资产审核通过");
            response.put("asset", asset);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "审核失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 审核拒绝资产
     */
    public Map<String, Object> rejectAsset(String reviewerId, String reviewerName, 
                                          String assetId, String comment) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<AgriculturalAsset> assetOptional = assetRepository.findById(assetId);
            if (assetOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "资产不存在");
                return response;
            }
            
            AgriculturalAsset asset = assetOptional.get();
            
            // 检查当前状态
            if (asset.getReviewStatus() != AgriculturalAsset.ReviewStatus.PENDING) {
                response.put("success", false);
                response.put("message", "该资产已经审核过了");
                return response;
            }
            
            // 更新审核状态
            asset.setReviewStatus(AgriculturalAsset.ReviewStatus.REJECTED);
            asset.setReviewerId(reviewerId);
            asset.setReviewerName(reviewerName);
            asset.setReviewComment(comment);
            asset.setReviewTime(LocalDateTime.now());
            asset.setUpdateTime(LocalDateTime.now());
            
            assetRepository.save(asset);
            
            // 记录操作日志
            OperationLog log = OperationLog.create(reviewerId, reviewerName, User.UserRole.REVIEWER,
                OperationLog.OperationType.ASSET_REVIEW, "ASSET", assetId);
            log.setTargetName(asset.getName());
            log.getOperationDetails().put("action", "REJECT");
            log.getOperationDetails().put("comment", comment);
            operationLogRepository.save(log);
            
            response.put("success", true);
            response.put("message", "资产审核拒绝");
            response.put("asset", asset);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "审核失败: " + e.getMessage());
        }
        
        return response;
    }
    
    // ==================== 资产发布 ====================
    
    /**
     * 获取审核通过待发布的资产列表
     */
    public Page<AgriculturalAsset> getApprovedUnpublishedAssets(Pageable pageable) {
        return assetRepository.findByReviewStatusAndPublishStatusOrderByReviewTimeDesc(
            AgriculturalAsset.ReviewStatus.APPROVED, 
            AgriculturalAsset.PublishStatus.DRAFT, 
            pageable);
    }
    
    /**
     * 获取已发布的资产列表
     */
    public Page<AgriculturalAsset> getPublishedAssets(Pageable pageable) {
        return assetRepository.findByPublishStatusOrderByPublishTimeDesc(
            AgriculturalAsset.PublishStatus.PUBLISHED, pageable);
    }
    
    /**
     * 发布资产
     */
    public Map<String, Object> publishAsset(String publisherId, String publisherName, 
                                           String assetId, String comment) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<AgriculturalAsset> assetOptional = assetRepository.findById(assetId);
            if (assetOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "资产不存在");
                return response;
            }
            
            AgriculturalAsset asset = assetOptional.get();
            
            // 检查审核状态
            if (asset.getReviewStatus() != AgriculturalAsset.ReviewStatus.APPROVED) {
                response.put("success", false);
                response.put("message", "只能发布审核通过的资产");
                return response;
            }
            
            // 检查发布状态
            if (asset.getPublishStatus() == AgriculturalAsset.PublishStatus.PUBLISHED) {
                response.put("success", false);
                response.put("message", "该资产已经发布");
                return response;
            }
            
            // 更新发布状态
            asset.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset.setPublisherId(publisherId);
            asset.setPublisherName(publisherName);
            asset.setPublishComment(comment);
            asset.setPublishTime(LocalDateTime.now());
            asset.setUpdateTime(LocalDateTime.now());
            
            assetRepository.save(asset);
            
            // 记录操作日志
            OperationLog log = OperationLog.create(publisherId, publisherName, User.UserRole.PUBLISHER,
                OperationLog.OperationType.ASSET_PUBLISH, "ASSET", assetId);
            log.setTargetName(asset.getName());
            log.getOperationDetails().put("action", "PUBLISH");
            log.getOperationDetails().put("comment", comment);
            operationLogRepository.save(log);
            
            response.put("success", true);
            response.put("message", "资产发布成功");
            response.put("asset", asset);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发布失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 下线资产
     */
    public Map<String, Object> offlineAsset(String operatorId, String operatorName, User.UserRole operatorRole,
                                           String assetId, String reason) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<AgriculturalAsset> assetOptional = assetRepository.findById(assetId);
            if (assetOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "资产不存在");
                return response;
            }
            
            AgriculturalAsset asset = assetOptional.get();
            
            // 检查发布状态
            if (asset.getPublishStatus() != AgriculturalAsset.PublishStatus.PUBLISHED) {
                response.put("success", false);
                response.put("message", "只能下线已发布的资产");
                return response;
            }
            
            // 更新发布状态
            asset.setPublishStatus(AgriculturalAsset.PublishStatus.OFFLINE);
            asset.setUpdateTime(LocalDateTime.now());
            
            assetRepository.save(asset);
            
            // 记录操作日志
            OperationLog log = OperationLog.create(operatorId, operatorName, operatorRole,
                OperationLog.OperationType.ASSET_OFFLINE, "ASSET", assetId);
            log.setTargetName(asset.getName());
            log.getOperationDetails().put("action", "OFFLINE");
            log.getOperationDetails().put("reason", reason);
            operationLogRepository.save(log);
            
            response.put("success", true);
            response.put("message", "资产下线成功");
            response.put("asset", asset);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "下线失败: " + e.getMessage());
        }
        
        return response;
    }
    
    // ==================== 统计信息 ====================
    
    /**
     * 获取资产统计信息
     */
    public Map<String, Object> getAssetStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 审核状态统计
            long pendingCount = assetRepository.countByReviewStatus(AgriculturalAsset.ReviewStatus.PENDING);
            long approvedCount = assetRepository.countByReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            long rejectedCount = assetRepository.countByReviewStatus(AgriculturalAsset.ReviewStatus.REJECTED);
            
            // 发布状态统计
            long draftCount = assetRepository.countByPublishStatus(AgriculturalAsset.PublishStatus.DRAFT);
            long publishedCount = assetRepository.countByPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            long offlineCount = assetRepository.countByPublishStatus(AgriculturalAsset.PublishStatus.OFFLINE);
            
            // 今日数据
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            stats.put("review", Map.of(
                "pending", pendingCount,
                "approved", approvedCount,
                "rejected", rejectedCount,
                "total", pendingCount + approvedCount + rejectedCount
            ));
            
            stats.put("publish", Map.of(
                "draft", draftCount,
                "published", publishedCount,
                "offline", offlineCount,
                "total", draftCount + publishedCount + offlineCount
            ));
            
            stats.put("today", Map.of(
                "newAssets", assetRepository.countByCreateTimeBetween(todayStart, todayEnd),
                "reviewed", assetRepository.countByReviewTimeBetween(todayStart, todayEnd),
                "published", assetRepository.countByPublishTimeBetween(todayStart, todayEnd)
            ));
            
        } catch (Exception e) {
            stats.put("error", "获取统计信息失败: " + e.getMessage());
        }
        
        return stats;
    }
} 