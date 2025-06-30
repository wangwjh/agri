package com.agridata.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UpdateProfileRequest {
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20字符之间")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 20, message = "手机号长度不能超过20字符")
    private String phone;
    
    @Size(max = 100, message = "所在地区长度不能超过100字符")
    private String location;
    
    @Size(max = 500, message = "个人简介长度不能超过500字符")
    private String bio;
} 