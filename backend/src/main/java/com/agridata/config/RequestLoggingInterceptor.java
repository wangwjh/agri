package com.agridata.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * 请求日志拦截器
 * 记录所有HTTP请求的详细信息，方便调试
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String REQUEST_START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        
        // 构建请求信息
        StringBuilder requestInfo = new StringBuilder();
        requestInfo.append("\n=== HTTP请求开始 ===\n");
        requestInfo.append("时间: ").append(LocalDateTime.now()).append("\n");
        requestInfo.append("方法: ").append(request.getMethod()).append("\n");
        requestInfo.append("URL: ").append(request.getRequestURL()).append("\n");
        
        if (request.getQueryString() != null) {
            requestInfo.append("查询参数: ").append(request.getQueryString()).append("\n");
        }
        
        requestInfo.append("远程地址: ").append(request.getRemoteAddr()).append("\n");
        requestInfo.append("用户代理: ").append(request.getHeader("User-Agent")).append("\n");
        
        // 记录请求头
        requestInfo.append("请求头:\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // 隐藏敏感信息
            if (headerName.toLowerCase().contains("authorization") || 
                headerName.toLowerCase().contains("token")) {
                headerValue = "***HIDDEN***";
            }
            requestInfo.append("  ").append(headerName).append(": ").append(headerValue).append("\n");
        }
        
        requestInfo.append("=================");
        
        logger.info(requestInfo.toString());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            
            StringBuilder responseInfo = new StringBuilder();
            responseInfo.append("\n=== HTTP请求完成 ===\n");
            responseInfo.append("时间: ").append(LocalDateTime.now()).append("\n");
            responseInfo.append("方法: ").append(request.getMethod()).append("\n");
            responseInfo.append("URL: ").append(request.getRequestURL()).append("\n");
            responseInfo.append("状态码: ").append(response.getStatus()).append("\n");
            responseInfo.append("处理时间: ").append(duration).append(" ms\n");
            
            if (ex != null) {
                responseInfo.append("异常: ").append(ex.getClass().getSimpleName())
                           .append(" - ").append(ex.getMessage()).append("\n");
            }
            
            responseInfo.append("=================");
            
            if (ex != null) {
                logger.error(responseInfo.toString());
            } else if (response.getStatus() >= 400) {
                logger.warn(responseInfo.toString());
            } else {
                logger.info(responseInfo.toString());
            }
        }
    }
} 