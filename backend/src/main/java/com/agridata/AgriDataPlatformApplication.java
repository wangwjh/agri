package com.agridata;

import com.agridata.model.AgriculturalAsset;
import com.agridata.repository.AgriculturalAssetRepository;
import com.agridata.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableMongoRepositories
public class AgriDataPlatformApplication implements CommandLineRunner {

    @Autowired
    private AgriculturalAssetRepository assetRepository;
    
    @Autowired
    private AdminService adminService;

    public static void main(String[] args) {
        SpringApplication.run(AgriDataPlatformApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化超级管理员
        adminService.initSuperAdmin();
        
        // 检查是否已有数据，避免重复添加
        if (assetRepository.count() == 0) {
            System.out.println("正在初始化示例数据...");
            
            // 创建示例农业数据资产
            AgriculturalAsset asset1 = new AgriculturalAsset();
            asset1.setName("华北平原小麦种植数据集");
            asset1.setDescription("包含华北平原地区2023年小麦种植的完整数据，包括土壤条件、气候数据、产量统计等");
            asset1.setPrice(299.0);
            asset1.setCategory("小麦");
            asset1.setOwnerId("system");
            asset1.setOwnerName("系统管理员");
            asset1.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&q=80"));
            asset1.setStatus("AVAILABLE");
            asset1.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset1.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset1.setQualityRating("A");
            asset1.setLikeCount(156L);
            asset1.setViewCount(1024L);
            
            Map<String, Object> metrics1 = new HashMap<>();
            metrics1.put("temperature", 22.5);
            metrics1.put("humidity", 65.0);
            metrics1.put("soilPh", 7.2);
            metrics1.put("dataScale", 5000.0);
            asset1.setMetrics(metrics1);

            AgriculturalAsset asset2 = new AgriculturalAsset();
            asset2.setName("江南水稻智能种植数据");
            asset2.setDescription("基于IoT传感器收集的江南地区水稻智能种植数据，包含水位、温度、湿度等实时监测数据");
            asset2.setPrice(458.0);
            asset2.setCategory("水稻");
            asset2.setOwnerId("system");
            asset2.setOwnerName("系统管理员");
            asset2.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1586771107445-d3ca888129ff?w=400&q=80"));
            asset2.setStatus("AVAILABLE");
            asset2.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset2.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset2.setQualityRating("A+");
            asset2.setLikeCount(203L);
            asset2.setViewCount(856L);
            
            Map<String, Object> metrics2 = new HashMap<>();
            metrics2.put("temperature", 28.0);
            metrics2.put("humidity", 85.0);
            metrics2.put("soilPh", 6.8);
            metrics2.put("dataScale", 3200.0);
            asset2.setMetrics(metrics2);

            AgriculturalAsset asset3 = new AgriculturalAsset();
            asset3.setName("西北地区玉米抗旱数据");
            asset3.setDescription("西北干旱地区玉米抗旱种植技术数据，包括节水灌溉、抗旱品种选择等关键数据");
            asset3.setPrice(380.0);
            asset3.setCategory("玉米");
            asset3.setOwnerId("system");
            asset3.setOwnerName("系统管理员");
            asset3.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1605000797499-95a51c5269ae?w=400&q=80"));
            asset3.setStatus("AVAILABLE");
            asset3.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset3.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset3.setQualityRating("B+");
            asset3.setLikeCount(89L);
            asset3.setViewCount(432L);
            
            Map<String, Object> metrics3 = new HashMap<>();
            metrics3.put("temperature", 25.0);
            metrics3.put("humidity", 45.0);
            metrics3.put("soilPh", 7.5);
            metrics3.put("dataScale", 8000.0);
            asset3.setMetrics(metrics3);

            // 保存示例数据
            assetRepository.saveAll(Arrays.asList(asset1, asset2, asset3));
            
            System.out.println("示例数据初始化完成！");
        }
    }
} 