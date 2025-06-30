package com.agridata.config;

import com.agridata.model.AgriculturalAsset;
import com.agridata.model.User;
import com.agridata.repository.AgriculturalAssetRepository;
import com.agridata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgriculturalAssetRepository assetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 初始化测试用户
        User testUser = null;
        if (userRepository.count() == 0) {
            testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("123456"));
            testUser.setEmail("test@example.com");
            testUser.setBalance(5000.0); // 给测试用户5000元初始余额
            testUser.setCreateTime(LocalDateTime.now());
            testUser.setUpdateTime(LocalDateTime.now());
            testUser = userRepository.save(testUser);
        } else {
            // 如果用户已存在，查找testuser
            testUser = userRepository.findByUsername("testuser").orElse(null);
            // 如果testuser存在且余额为0，给个初始余额
            if (testUser != null && (testUser.getBalance() == null || testUser.getBalance() == 0.0)) {
                testUser.setBalance(5000.0);
                testUser.setUpdateTime(LocalDateTime.now());
                testUser = userRepository.save(testUser);
            }
        }
        
        // 检查是否有已发布的资产，如果没有则创建测试数据
        long publishedAssetCount = assetRepository.countByReviewStatusAndPublishStatus(
            AgriculturalAsset.ReviewStatus.APPROVED, 
            AgriculturalAsset.PublishStatus.PUBLISHED
        );
        
        if (publishedAssetCount == 0 && testUser != null) {
            System.out.println("创建测试农业数据资产...");
            initTestAssets(testUser);
        }

        // 添加管理员用户
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@agridata.com");
            admin.setPhone("13800138000");
            admin.setRole(User.UserRole.SUPER_ADMIN);
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setBalance(10000.0); // 给管理员10000元初始余额
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            admin = userRepository.save(admin);
        } else {
            // 如果admin存在且余额为0，给个初始余额
            if (admin.getBalance() == null || admin.getBalance() == 0.0) {
                admin.setBalance(10000.0);
                admin.setUpdateTime(LocalDateTime.now());
                admin = userRepository.save(admin);
            }
        }
    }

    private void initTestAssets(User owner) {
        // 创建测试农业数据资产
        AgriculturalAsset[] assets = {
            createAsset("有机小麦种植数据", "包含完整生长周期的有机小麦种植数据，记录了温度、湿度、光照等关键指标", 1200.0, owner, "A+", "有机种植", createWheatMetrics()),
            createAsset("智能温室番茄数据", "采用智能温室技术种植的番茄全过程数据，产量优异", 1800.0, owner, "A", "智能温室", createTomatoMetrics()),
            createAsset("水稻精准农业数据", "基于精准农业技术的水稻种植数据，包含土壤分析报告", 1500.0, owner, "A+", "精准农业", createRiceMetrics()),
            createAsset("胡萝卜种植全程记录", "胡萝卜从播种到收获的完整数据记录，质量优良", 800.0, owner, "B+", "传统种植", createCarrotMetrics()),
            createAsset("苹果果园管理数据", "现代化果园管理的苹果种植数据，包含病虫害防治记录", 2200.0, owner, "A+", "现代化管理", createAppleMetrics()),
            createAsset("绿色蔬菜基地数据", "绿色蔬菜种植基地的综合数据，多种蔬菜混合种植", 1000.0, owner, "A", "绿色种植", createVegetableMetrics()),
            createAsset("草莓温室种植数据", "温室草莓四季种植技术数据，产量稳定", 1600.0, owner, "A", "温室技术", createStrawberryMetrics()),
            createAsset("玉米大田种植数据", "大田玉米机械化种植全程数据记录", 900.0, owner, "B", "机械化种植", createCornMetrics()),
            createAsset("茶叶种植工艺数据", "高山茶叶种植及加工工艺完整数据", 2800.0, owner, "A+", "传统工艺", createTeaMetrics())
        };

        for (AgriculturalAsset asset : assets) {
            // 随机设置点赞数
            asset.setLikeCount((long)(Math.random() * 100 + 10));
            assetRepository.save(asset);
        }
    }

    private AgriculturalAsset createAsset(String name, String description, Double price, User owner, 
                                        String qualityRating, String technique, Map<String, Object> metrics) {
        AgriculturalAsset asset = new AgriculturalAsset();
        asset.setName(name);
        asset.setDescription(description);
        asset.setPrice(price);
        asset.setOwnerId(owner.getId());
        asset.setOwnerName(owner.getUsername());
        asset.setQualityRating(qualityRating);
        asset.setCultivationTechnique(technique);
        asset.setMetrics(metrics);
        asset.setStatus("AVAILABLE");
        asset.setCategory("农作物");
        asset.setImageUrls(List.of(
            "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400",
            "https://images.unsplash.com/photo-1560493676-04071c5f467b?w=400"
        ));
        asset.setCreateTime(LocalDateTime.now());
        asset.setUpdateTime(LocalDateTime.now());
        
        // 设置为已审核通过且已发布状态，这样前端就能查询到测试数据
        asset.setReviewStatus(AgriculturalAsset.ReviewStatus.APPROVED);
        asset.setPublishStatus(AgriculturalAsset.PublishStatus.PUBLISHED);
        asset.setReviewTime(LocalDateTime.now());
        asset.setPublishTime(LocalDateTime.now());
        
        return asset;
    }

    private Map<String, Object> createWheatMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("平均温度", "22°C");
        metrics.put("湿度", "65%");
        metrics.put("光照时长", "12小时/天");
        metrics.put("土壤pH", "6.8");
        metrics.put("产量", "450kg/亩");
        return metrics;
    }

    private Map<String, Object> createTomatoMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("温室温度", "25°C");
        metrics.put("湿度", "70%");
        metrics.put("CO2浓度", "800ppm");
        metrics.put("土壤EC值", "2.5");
        metrics.put("产量", "8000kg/亩");
        return metrics;
    }

    private Map<String, Object> createRiceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("水温", "28°C");
        metrics.put("水深", "5cm");
        metrics.put("土壤养分", "优良");
        metrics.put("病虫害率", "2%");
        metrics.put("产量", "550kg/亩");
        return metrics;
    }

    private Map<String, Object> createCarrotMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("土壤温度", "18°C");
        metrics.put("湿度", "60%");
        metrics.put("土壤类型", "沙壤土");
        metrics.put("施肥次数", "3次");
        metrics.put("产量", "3500kg/亩");
        return metrics;
    }

    private Map<String, Object> createAppleMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("气温", "20°C");
        metrics.put("降雨量", "800mm/年");
        metrics.put("日照时数", "2200小时");
        metrics.put("土壤有机质", "3.2%");
        metrics.put("产量", "4000kg/亩");
        return metrics;
    }

    private Map<String, Object> createVegetableMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("温度", "23°C");
        metrics.put("湿度", "68%");
        metrics.put("轮作周期", "3个月");
        metrics.put("农药使用", "无");
        metrics.put("综合产量", "6000kg/亩");
        return metrics;
    }

    private Map<String, Object> createStrawberryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("温室温度", "20°C");
        metrics.put("湿度", "75%");
        metrics.put("基质类型", "椰糠+珍珠岩");
        metrics.put("营养液EC", "1.8");
        metrics.put("产量", "3000kg/亩");
        return metrics;
    }

    private Map<String, Object> createCornMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("播种深度", "3cm");
        metrics.put("行距", "65cm");
        metrics.put("密度", "4500株/亩");
        metrics.put("施肥量", "45kg/亩");
        metrics.put("产量", "600kg/亩");
        return metrics;
    }

    private Map<String, Object> createTeaMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("海拔", "1200m");
        metrics.put("年降雨量", "1500mm");
        metrics.put("采摘标准", "一芽二叶");
        metrics.put("发酵度", "85%");
        metrics.put("产量", "150kg/亩");
        return metrics;
    }
} 