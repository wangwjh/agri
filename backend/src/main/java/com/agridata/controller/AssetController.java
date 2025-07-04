package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.model.AgriculturalAsset;
import com.agridata.service.AgriculturalAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AgriculturalAssetService assetService;

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<Page<AgriculturalAsset>>> getHotAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        try {
            Page<AgriculturalAsset> assets = assetService.getHotAssets(page, size);
            return ResponseEntity.ok(ApiResponse.success(assets));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取热门资产失败: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<AgriculturalAsset>>> searchAssets(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "likes") String sortBy) {
        try {
            Page<AgriculturalAsset> assets = assetService.searchAssets(keyword, page, size, sortBy);
            return ResponseEntity.ok(ApiResponse.success(assets));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("搜索失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgriculturalAsset>> getAssetById(@PathVariable String id) {
        try {
            AgriculturalAsset asset = assetService.getAssetById(id);
            if (asset == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ApiResponse.success(asset));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取资产详情失败: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<String>> toggleLike(@PathVariable String id, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            boolean success = assetService.toggleLike(id, authentication.getName());
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("操作成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("操作失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("点赞操作失败: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AgriculturalAsset>> createAsset(
            @RequestBody AgriculturalAsset asset, Authentication authentication) {
        try {
            System.out.println("收到创建资产请求");
            System.out.println("认证信息: " + authentication);
            System.out.println("请求体数据: " + asset);
            
            if (authentication == null || authentication.getName() == null) {
                System.out.println("用户未认证，拒绝请求");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("用户未登录或认证失败"));
            }

            String username = authentication.getName();
            System.out.println("用户: " + username + " 正在创建资产: " + asset.getName());
            
            // 验证必要字段
            if (asset.getName() == null || asset.getName().trim().isEmpty()) {
                System.out.println("资产名称为空");
                return ResponseEntity.badRequest().body(ApiResponse.error("资产名称不能为空"));
            }
            
            if (asset.getDescription() == null || asset.getDescription().trim().isEmpty()) {
                System.out.println("资产描述为空");
                return ResponseEntity.badRequest().body(ApiResponse.error("资产描述不能为空"));
            }
            
            if (asset.getPrice() == null || asset.getPrice() <= 0) {
                System.out.println("资产价格无效: " + asset.getPrice());
                return ResponseEntity.badRequest().body(ApiResponse.error("资产价格必须大于0"));
            }
            
            AgriculturalAsset createdAsset = assetService.createAsset(asset, username);
            if (createdAsset == null) {
                System.out.println("资产创建失败");
                return ResponseEntity.badRequest().body(ApiResponse.error("创建资产失败"));
            }
            
            System.out.println("资产创建成功，ID: " + createdAsset.getId());
            return ResponseEntity.ok(ApiResponse.success("创建成功", createdAsset));
        } catch (Exception e) {
            System.err.println("创建资产时发生异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("创建失败: " + e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AgriculturalAsset>>> getMyAssets(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            List<AgriculturalAsset> assets = assetService.getUserAssets(authentication.getName());
            return ResponseEntity.ok(ApiResponse.success(assets));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取用户资产失败: " + e.getMessage()));
        }
    }
} 