package com.agridata.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 调试配置类
 * 提供应用启动时的调试信息和配置
 */
@Configuration
public class DebugConfig {

    private static final Logger logger = LoggerFactory.getLogger(DebugConfig.class);

    @Autowired
    private Environment environment;

    /**
     * 应用启动完成后输出调试信息
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("=".repeat(60));
        logger.info("农业数据平台应用启动完成");
        logger.info("=".repeat(60));
        
        // 打印基本信息
        logger.info("启动时间: {}", LocalDateTime.now());
        logger.info("JVM版本: {}", System.getProperty("java.version"));
        logger.info("操作系统: {} {}", 
                System.getProperty("os.name"), 
                System.getProperty("os.version"));
        
        // 打印活动配置文件
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            logger.info("活动配置文件: {}", Arrays.toString(activeProfiles));
        } else {
            logger.info("活动配置文件: [default]");
        }
        
        // 打印端口信息
        String port = environment.getProperty("server.port", "8080");
        logger.info("服务端口: {}", port);
        
        // 打印可用的调试接口
        logger.info("调试接口:");
        logger.info("  健康检查: GET /api/debug/health");
        logger.info("  系统信息: GET /api/debug/info");
        logger.info("  错误测试: POST /api/debug/test-error?type=[runtime|null|illegal]");
        
        // 打印内存信息
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        
        logger.info("内存信息:");
        logger.info("  最大内存: {} MB", maxMemory);
        logger.info("  总内存: {} MB", totalMemory);
        logger.info("  可用内存: {} MB", freeMemory);
        logger.info("  处理器核心数: {}", runtime.availableProcessors());
        
        logger.info("=".repeat(60));
    }
} 