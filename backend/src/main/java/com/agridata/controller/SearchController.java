package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.service.AgriculturalAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private AgriculturalAssetService assetService;

    @GetMapping("/suggest")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<String> suggestions = assetService.getSearchSuggestions(q, limit);
            return ResponseEntity.ok(ApiResponse.success(suggestions));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取搜索建议失败: " + e.getMessage()));
        }
    }
} 