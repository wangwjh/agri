package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局错误处理控制器
 * 统一处理应用中的各种异常和错误
 */
@ControllerAdvice
@RestController
@RequestMapping("/api/debug")
public class ErrorHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerController.class);

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        errorDetails.put("fieldErrors", fieldErrors);
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.warn("参数校验失败: {}", fieldErrors);
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("参数校验失败", errorDetails));
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.warn("约束违反: {}", ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("数据约束违反", errorDetails));
    }

    /**
     * 处理JSON解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "请求体格式错误，请检查JSON格式");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.warn("JSON解析失败: {}", ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("请求格式错误", errorDetails));
    }

    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "用户名或密码错误");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.warn("认证失败: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("认证失败", errorDetails));
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "权限不足，无法访问该资源");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.warn("权限不足: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("权限不足", errorDetails));
    }

    /**
     * 处理认证不足异常
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleInsufficientAuthentication(
            InsufficientAuthenticationException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "请先登录");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.warn("认证不足: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("请先登录", errorDetails));
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "不支持的请求方法: " + ex.getMethod());
        errorDetails.put("supportedMethods", ex.getSupportedMethods());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.warn("不支持的请求方法: {}", ex.getMethod());
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("请求方法不支持", errorDetails));
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNotFound(
            NoHandlerFoundException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "请求的资源不存在");
        errorDetails.put("requestURL", ex.getRequestURL());
        errorDetails.put("httpMethod", ex.getHttpMethod());
        errorDetails.put("timestamp", LocalDateTime.now());
        
        logger.warn("404错误: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("资源不存在", errorDetails));
    }

    /**
     * 处理其他所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("type", ex.getClass().getSimpleName());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        
        logger.error("未处理异常: ", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("服务器内部错误", errorDetails));
    }

    /**
     * 系统状态检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("version", "1.0.0");
        healthInfo.put("environment", System.getProperty("spring.profiles.active", "default"));
        
        return ResponseEntity.ok(ApiResponse.success("系统正常运行", healthInfo));
    }

    /**
     * 获取系统信息接口
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> systemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        // JVM信息
        Runtime runtime = Runtime.getRuntime();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");
        systemInfo.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        systemInfo.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        systemInfo.put("processors", runtime.availableProcessors());
        
        // 系统信息
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("系统信息", systemInfo));
    }

    /**
     * 测试错误接口 - 用于测试错误处理机制
     */
    @PostMapping("/test-error")
    public ResponseEntity<ApiResponse<Object>> testError(@RequestParam(defaultValue = "runtime") String type) {
        switch (type.toLowerCase()) {
            case "runtime":
                throw new RuntimeException("这是一个测试的运行时异常");
            case "null":
                throw new NullPointerException("这是一个测试的空指针异常");
            case "illegal":
                throw new IllegalArgumentException("这是一个测试的非法参数异常");
            default:
                return ResponseEntity.ok(ApiResponse.success("错误测试完成，未抛出异常"));
        }
    }
} 