package com.agridata.repository;

import com.agridata.model.AgriculturalAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgriculturalAssetRepository extends MongoRepository<AgriculturalAsset, String> {
    
    // 根据所有者ID查找
    List<AgriculturalAsset> findByOwnerId(String ownerId);
    
    // 根据状态查找
    Page<AgriculturalAsset> findByStatus(String status, Pageable pageable);
    
    // 全文搜索
    @Query("{ $text: { $search: ?0 } }")
    Page<AgriculturalAsset> findByTextSearch(String searchText, Pageable pageable);
    
    // 按点赞数排序获取热门资产
    @Query("{ 'status': 'AVAILABLE' }")
    Page<AgriculturalAsset> findHotAssets(Pageable pageable);
    
    // 按分类查找
    Page<AgriculturalAsset> findByCategory(String category, Pageable pageable);
    
    // 价格范围查询
    Page<AgriculturalAsset> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    
    // 组合搜索：名称模糊匹配或描述模糊匹配
    @Query("{ $or: [ " +
           "{ 'name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } }, " +
           "{ 'cultivationTechnique': { $regex: ?0, $options: 'i' } } " +
           "], 'status': 'AVAILABLE' }")
    Page<AgriculturalAsset> findByKeywordSearch(String keyword, Pageable pageable);
    
    // ==================== 审核相关查询 ====================
    
    // 根据审核状态查询
    Page<AgriculturalAsset> findByReviewStatusOrderByCreateTimeDesc(AgriculturalAsset.ReviewStatus reviewStatus, Pageable pageable);
    
    // 根据发布状态查询
    Page<AgriculturalAsset> findByPublishStatusOrderByPublishTimeDesc(AgriculturalAsset.PublishStatus publishStatus, Pageable pageable);
    
    // 根据审核状态和发布状态查询
    Page<AgriculturalAsset> findByReviewStatusAndPublishStatusOrderByReviewTimeDesc(
        AgriculturalAsset.ReviewStatus reviewStatus, 
        AgriculturalAsset.PublishStatus publishStatus, 
        Pageable pageable);
    
    // 获取所有资产按时间排序
    Page<AgriculturalAsset> findAllByOrderByCreateTimeDesc(Pageable pageable);
    
    // 统计方法
    Long countByReviewStatus(AgriculturalAsset.ReviewStatus reviewStatus);
    Long countByPublishStatus(AgriculturalAsset.PublishStatus publishStatus);
    Long countByReviewStatusAndPublishStatus(AgriculturalAsset.ReviewStatus reviewStatus, AgriculturalAsset.PublishStatus publishStatus);
    
    // 时间范围统计
    Long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    Long countByReviewTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    Long countByPublishTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 前台查询：只返回已发布的资产
    @Query("{ 'reviewStatus': 'APPROVED', 'publishStatus': 'PUBLISHED' }")
    Page<AgriculturalAsset> findPublishedAssets(Pageable pageable);
    
    @Query("{ 'reviewStatus': 'APPROVED', 'publishStatus': 'PUBLISHED', " +
           "$or: [ " +
           "{ 'name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<AgriculturalAsset> searchPublishedAssets(String keyword, Pageable pageable);
    
    @Query("{ 'reviewStatus': 'APPROVED', 'publishStatus': 'PUBLISHED', 'category': ?0 }")
    Page<AgriculturalAsset> findPublishedAssetsByCategory(String category, Pageable pageable);
} 