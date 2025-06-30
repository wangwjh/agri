package com.agridata.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain chain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        System.out.println("=== JWT Filter处理请求: " + requestURI + " ===");
        
        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + requestTokenHeader);

        String username = null;
        String jwtToken = null;

        // JWT Token格式为 "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            System.out.println("提取的JWT Token: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            try {
                username = jwtUtil.extractUsername(jwtToken);
                System.out.println("从Token提取的用户名: " + username);
            } catch (Exception e) {
                System.err.println("无法从JWT Token提取用户名: " + e.getMessage());
                logger.warn("无法获取JWT Token中的用户名", e);
            }
        } else {
            System.out.println("请求中没有Bearer Token");
        }

        // 一旦获得token，验证它
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("开始验证Token...");
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("加载的UserDetails: " + userDetails.getUsername());

                // 如果token有效，配置Spring Security手动设置认证
                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    System.out.println("Token验证成功，设置认证信息");
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    System.out.println("认证设置成功");
                } else {
                    System.out.println("Token验证失败");
                }
            } catch (Exception e) {
                System.err.println("认证过程中发生异常: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (username == null) {
            System.out.println("没有用户名，跳过认证");
        } else {
            System.out.println("已有认证信息，跳过");
        }
        
        chain.doFilter(request, response);
    }
} 