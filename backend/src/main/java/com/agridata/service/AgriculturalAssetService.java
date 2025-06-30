package com.agridata.service;

import com.agridata.model.AgriculturalAsset;
import com.agridata.model.User;
import com.agridata.repository.AgriculturalAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgriculturalAssetService {

    @Autowired
    private AgriculturalAssetRepository assetRepository;

    @Autowired
    private UserService userService;

    // 获取热门资产 (按点赞数排序)
    public Page<AgriculturalAsset> getHotAssets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        return assetRepository.findPublishedAssets(pageable);
    }

    // 搜索资产
    public Page<AgriculturalAsset> searchAssets(String keyword, int page, int size, String sortBy) {
        Sort sort = createSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return assetRepository.findPublishedAssets(pageable);
        }
        
        return assetRepository.searchPublishedAssets(keyword.trim(), pageable);
    }

    // 搜索建议
    public List<String> getSearchSuggestions(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        
        Pageable pageable = PageRequest.of(0, limit);
        Page<AgriculturalAsset> assets = assetRepository.findByKeywordSearch(query.trim(), pageable);
        
        return assets.getContent().stream()
                .map(AgriculturalAsset::getName)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // 点赞/取消点赞
    public boolean toggleLike(String assetId, String username) {
        try {
            Optional<AgriculturalAsset> assetOptional = assetRepository.findById(assetId);
            if (assetOptional.isEmpty()) {
                System.err.println("点赞失败：资产不存在 - " + assetId);
                return false;
            }

            User user = userService.getCurrentUser(username);
            if (user == null) {
                System.err.println("点赞失败：用户不存在 - " + username);
                return false;
            }

            if (user.getId() == null || user.getId().isEmpty()) {
                System.err.println("点赞失败：用户ID为空 - " + username);
                return false;
            }

            AgriculturalAsset asset = assetOptional.get();
            
            if (asset.isLikedBy(user.getId())) {
                asset.removeLike(user.getId());
            } else {
                asset.addLike(user.getId());
            }
            
            asset.setUpdateTime(LocalDateTime.now());
            assetRepository.save(asset);
            return true;
        } catch (Exception e) {
            System.err.println("点赞操作时发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 获取资产详情
    public AgriculturalAsset getAssetById(String assetId) {
        return assetRepository.findById(assetId).orElse(null);
    }

    // 创建资产
    public AgriculturalAsset createAsset(AgriculturalAsset asset, String username) {
        try {
            User user = userService.getCurrentUser(username);
            if (user == null) {
                System.err.println("创建资产失败：用户不存在 - " + username);
                return null;
            }

            if (user.getId() == null || user.getId().isEmpty()) {
                System.err.println("创建资产失败：用户ID为空 - " + username);
                return null;
            }

            // 添加调试信息
            System.out.println("=== 创建资产调试信息 ===");
            System.out.println("资产名称: " + asset.getName());
            System.out.println("用户: " + username);
            System.out.println("附件文件数量: " + (asset.getAttachedFiles() != null ? asset.getAttachedFiles().size() : 0));
            if (asset.getAttachedFiles() != null && !asset.getAttachedFiles().isEmpty()) {
                System.out.println("附件文件详情:");
                for (int i = 0; i < asset.getAttachedFiles().size(); i++) {
                    AgriculturalAsset.AttachedFile file = asset.getAttachedFiles().get(i);
                    System.out.println("  文件" + (i+1) + ": " + file.getName() + " (" + file.getSize() + " bytes)");
                }
            }

            asset.setOwnerId(user.getId());
            asset.setOwnerName(user.getUsername());
            asset.setCreateTime(LocalDateTime.now());
            asset.setUpdateTime(LocalDateTime.now());
            asset.setStatus("AVAILABLE");
            
            // 设置审核和发布状态
            asset.setReviewStatus(AgriculturalAsset.ReviewStatus.PENDING);
            asset.setPublishStatus(AgriculturalAsset.PublishStatus.DRAFT);

            AgriculturalAsset savedAsset = assetRepository.save(asset);
            
            // 验证保存后的数据
            System.out.println("保存后的资产ID: " + savedAsset.getId());
            System.out.println("保存后的附件文件数量: " + (savedAsset.getAttachedFiles() != null ? savedAsset.getAttachedFiles().size() : 0));
            System.out.println("=== 调试信息结束 ===");
            
            return savedAsset;
        } catch (Exception e) {
            System.err.println("创建资产时发生异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 获取用户的资产
    public List<AgriculturalAsset> getUserAssets(String username) {
        User user = userService.getCurrentUser(username);
        if (user == null) {
            return List.of();
        }
        return assetRepository.findByOwnerId(user.getId());
    }

    // 保存资产
    public AgriculturalAsset saveAsset(AgriculturalAsset asset) {
        if (asset != null) {
            asset.setUpdateTime(LocalDateTime.now());
            return assetRepository.save(asset);
        }
        return null;
    }

    private Sort createSort(String sortBy) {
        return switch (sortBy) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "likes" -> Sort.by(Sort.Direction.DESC, "likeCount");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createTime");
            default -> Sort.by(Sort.Direction.DESC, "likeCount");
        };
    }
} 