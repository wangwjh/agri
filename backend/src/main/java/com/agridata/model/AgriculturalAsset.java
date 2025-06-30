package com.agridata.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "agricultural_assets")
public class AgriculturalAsset {
    @Id
    private String id;
    
    @TextIndexed(weight = 2)
    @Indexed
    private String name; // 农作物名称
    
    @TextIndexed
    private String description; // 描述
    
    private Double price; // 价格
    
    @Indexed
    private String ownerId; // 所有者ID
    
    private String ownerName; // 所有者名称
    
    // 关键指标数据
    private Map<String, Object> metrics = new HashMap<>(); // {阳光:值,温度:值,湿度:值,土壤PH:值,...}
    
    @TextIndexed
    private String cultivationTechnique; // 培养技术
    
    private Double yield; // 产量
    
    private String qualityRating; // 质量评级(A+，A，B等)
    
    private List<String> imageUrls = new ArrayList<>(); // 展示图片URL
    
    // 附件文件信息
    private List<AttachedFile> attachedFiles = new ArrayList<>(); // 附件文件列表
    
    @Indexed
    private Long likeCount = 0L; // 点赞数
    
    @Indexed
    private Long viewCount = 0L; // 浏览数
    
    private List<String> likedBy = new ArrayList<>(); // 点赞用户ID列表
    
    @Indexed
    private String category; // 分类
    
    @Indexed
    private String status = "AVAILABLE"; // 状态: AVAILABLE, SOLD, DRAFT
    
    // 审核状态
    private ReviewStatus reviewStatus = ReviewStatus.PENDING;
    
    // 发布状态
    private PublishStatus publishStatus = PublishStatus.DRAFT;
    
    // 审核信息
    private String reviewerId;
    private String reviewerName;
    private String reviewComment;
    private LocalDateTime reviewTime;
    
    // 发布信息
    private String publisherId;
    private String publisherName;
    private String publishComment;
    private LocalDateTime publishTime;
    
    @Indexed
    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime = LocalDateTime.now();
    
    // 审核状态枚举
    public enum ReviewStatus {
        PENDING("待审核"),
        APPROVED("审核通过"),
        REJECTED("审核拒绝");
        
        private final String description;
        
        ReviewStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 发布状态枚举
    public enum PublishStatus {
        DRAFT("草稿"),
        PUBLISHED("已发布"),
        OFFLINE("已下线");
        
        private final String description;
        
        PublishStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public AgriculturalAsset(String name, String description, Double price, String ownerId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.ownerId = ownerId;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // 点赞操作
    public void addLike(String userId) {
        if (userId == null || userId.isEmpty()) {
            return;
        }
        if (this.likedBy == null) {
            this.likedBy = new ArrayList<>();
        }
        if (this.likeCount == null) {
            this.likeCount = 0L;
        }
        if (!likedBy.contains(userId)) {
            likedBy.add(userId);
            likeCount++;
        }
    }
    
    // 取消点赞
    public void removeLike(String userId) {
        if (userId == null || userId.isEmpty()) {
            return;
        }
        if (this.likedBy == null) {
            this.likedBy = new ArrayList<>();
        }
        if (this.likeCount == null) {
            this.likeCount = 0L;
        }
        if (likedBy.contains(userId)) {
            likedBy.remove(userId);
            if (likeCount > 0) {
                likeCount--;
            }
        }
    }
    
    // 检查是否已点赞
    public boolean isLikedBy(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        if (this.likedBy == null) {
            this.likedBy = new ArrayList<>();
        }
        return likedBy.contains(userId);
    }
    
    // 附件文件内部类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachedFile {
        private String name;         // 原始文件名
        private String savedName;    // 保存的文件名
        private Long size;           // 文件大小
        private String type;         // 文件MIME类型
        private String relativePath; // 相对路径
        private LocalDateTime uploadTime; // 上传时间
        
        public AttachedFile(String name, String savedName, Long size, String type, String relativePath) {
            this.name = name;
            this.savedName = savedName;
            this.size = size;
            this.type = type;
            this.relativePath = relativePath;
            this.uploadTime = LocalDateTime.now();
        }
    }
} 