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
            
            // 创建示例农业数据资产 - 原有3条
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

            // 新增示例数据 - 补充6条
            AgriculturalAsset asset4 = new AgriculturalAsset();
            asset4.setName("东北大豆精准种植数据");
            asset4.setDescription("东北黑土地大豆精准种植技术数据，包含GPS定位、变量施肥、收获机械化等数据");
            asset4.setPrice(520.0);
            asset4.setCategory("大豆");
            asset4.setOwnerId("system");
            asset4.setOwnerName("系统管理员");
            asset4.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1606056992872-3d9b22d13a1e?w=400&q=80"));
            asset4.setStatus("AVAILABLE");
            asset4.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset4.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset4.setQualityRating("A+");
            asset4.setLikeCount(167L);
            asset4.setViewCount(789L);
            
            Map<String, Object> metrics4 = new HashMap<>();
            metrics4.put("temperature", 18.5);
            metrics4.put("humidity", 72.0);
            metrics4.put("soilPh", 6.5);
            metrics4.put("dataScale", 6500.0);
            asset4.setMetrics(metrics4);

            AgriculturalAsset asset5 = new AgriculturalAsset();
            asset5.setName("南方果园智能管理数据");
            asset5.setDescription("南方果园智能化管理系统数据，包括病虫害监测、智能灌溉、果实成熟度检测等");
            asset5.setPrice(680.0);
            asset5.setCategory("水果");
            asset5.setOwnerId("system");
            asset5.setOwnerName("系统管理员");
            asset5.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1541554440-7a8c17c14cdf?w=400&q=80"));
            asset5.setStatus("AVAILABLE");
            asset5.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset5.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset5.setQualityRating("A");
            asset5.setLikeCount(234L);
            asset5.setViewCount(1156L);
            
            Map<String, Object> metrics5 = new HashMap<>();
            metrics5.put("temperature", 26.0);
            metrics5.put("humidity", 78.0);
            metrics5.put("soilPh", 6.2);
            metrics5.put("dataScale", 4200.0);
            asset5.setMetrics(metrics5);

            AgriculturalAsset asset6 = new AgriculturalAsset();
            asset6.setName("高原青稞种植数据集");
            asset6.setDescription("青藏高原青稞种植适应性数据，包含高海拔环境下的生长周期、抗逆性等关键数据");
            asset6.setPrice(420.0);
            asset6.setCategory("青稞");
            asset6.setOwnerId("system");
            asset6.setOwnerName("系统管理员");
            asset6.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=400&q=80"));
            asset6.setStatus("AVAILABLE");
            asset6.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset6.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset6.setQualityRating("B+");
            asset6.setLikeCount(98L);
            asset6.setViewCount(567L);
            
            Map<String, Object> metrics6 = new HashMap<>();
            metrics6.put("temperature", 12.0);
            metrics6.put("humidity", 55.0);
            metrics6.put("soilPh", 7.8);
            metrics6.put("dataScale", 2800.0);
            asset6.setMetrics(metrics6);

            AgriculturalAsset asset7 = new AgriculturalAsset();
            asset7.setName("设施蔬菜无土栽培数据");
            asset7.setDescription("现代化温室蔬菜无土栽培技术数据，包含营养液配方、环境控制、产量优化等数据");
            asset7.setPrice(750.0);
            asset7.setCategory("蔬菜");
            asset7.setOwnerId("system");
            asset7.setOwnerName("系统管理员");
            asset7.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&q=80"));
            asset7.setStatus("AVAILABLE");
            asset7.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset7.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset7.setQualityRating("A+");
            asset7.setLikeCount(312L);
            asset7.setViewCount(1445L);
            
            Map<String, Object> metrics7 = new HashMap<>();
            metrics7.put("temperature", 24.0);
            metrics7.put("humidity", 70.0);
            metrics7.put("soilPh", 6.0);
            metrics7.put("dataScale", 3600.0);
            asset7.setMetrics(metrics7);

            AgriculturalAsset asset8 = new AgriculturalAsset();
            asset8.setName("棉花机械化种植数据");
            asset8.setDescription("新疆棉花机械化种植全流程数据，包含播种、田间管理、采收等机械化作业数据");
            asset8.setPrice(590.0);
            asset8.setCategory("棉花");
            asset8.setOwnerId("system");
            asset8.setOwnerName("系统管理员");
            asset8.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1502823403499-6ccfcf4fb453?w=400&q=80"));
            asset8.setStatus("AVAILABLE");
            asset8.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset8.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset8.setQualityRating("A");
            asset8.setLikeCount(145L);
            asset8.setViewCount(678L);
            
            Map<String, Object> metrics8 = new HashMap<>();
            metrics8.put("temperature", 27.0);
            metrics8.put("humidity", 40.0);
            metrics8.put("soilPh", 7.6);
            metrics8.put("dataScale", 9200.0);
            asset8.setMetrics(metrics8);

            AgriculturalAsset asset9 = new AgriculturalAsset();
            asset9.setName("茶园生态种植数据集");
            asset9.setDescription("有机茶园生态种植管理数据，包含土壤有机质、生物防治、品质检测等生态农业数据");
            asset9.setPrice(480.0);
            asset9.setCategory("茶叶");
            asset9.setOwnerId("system");
            asset9.setOwnerName("系统管理员");
            asset9.setImageUrls(Arrays.asList("https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400&q=80"));
            asset9.setStatus("AVAILABLE");
            asset9.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
            asset9.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
            asset9.setQualityRating("A");
            asset9.setLikeCount(189L);
            asset9.setViewCount(834L);
            
            Map<String, Object> metrics9 = new HashMap<>();
            metrics9.put("temperature", 20.0);
            metrics9.put("humidity", 82.0);
            metrics9.put("soilPh", 5.8);
            metrics9.put("dataScale", 3800.0);
            asset9.setMetrics(metrics9);

            // 保存所有示例数据
            assetRepository.saveAll(Arrays.asList(
                asset1, asset2, asset3, asset4, asset5, 
                asset6, asset7, asset8, asset9
            ));
            
            System.out.println("示例数据初始化完成！共创建9条农业数据资产");
        }
    }
} 