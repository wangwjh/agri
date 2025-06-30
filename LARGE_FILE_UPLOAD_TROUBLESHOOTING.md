# 大文件上传故障排除指南

## 🚨 常见问题及解决方案

### 1. 网络错误 (Network Error)

**症状**：上传过程中出现"Network Error"或连接中断

**可能原因**：
- 网络连接不稳定
- 代理服务器配置限制
- 超时设置过短

**解决方案**：

#### A. 检查网络连接
```bash
# 测试网络连接稳定性
ping -c 10 localhost
# 或者
curl -I http://localhost:8080/api/health
```

#### B. 重启服务应用新配置
```bash
# 停止后端服务
cd backend
./mvnw spring-boot:stop

# 重新启动后端服务
./mvnw spring-boot:run
```

#### C. 如果使用Nginx代理
将 `nginx.conf.example` 配置应用到您的Nginx配置中：
```bash
# 备份现有配置
sudo cp /etc/nginx/sites-available/default /etc/nginx/sites-available/default.backup

# 应用新配置（根据实际路径调整）
sudo cp nginx.conf.example /etc/nginx/sites-available/agri-data

# 重启Nginx
sudo systemctl restart nginx
```

### 2. 文件过大错误 (413 Request Entity Too Large)

**症状**：上传时显示"文件过大"或HTTP 413错误

**解决方案**：

#### A. 检查Spring Boot配置
确认 `backend/src/main/resources/application.yml` 包含：
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5GB
      max-request-size: 5GB
      file-size-threshold: 10MB
      
server:
  tomcat:
    max-http-post-size: 5368709120  # 5GB
```

#### B. 检查Nginx配置（如果使用）
```nginx
client_max_body_size 5G;
```

### 3. 超时错误 (Timeout)

**症状**：上传过程中显示超时错误

**解决方案**：

#### A. 前端超时设置
已优化为30分钟超时：
```javascript
timeout: 1800000, // 30分钟
```

#### B. 后端超时设置
已在 `application.yml` 中配置：
```yaml
server:
  tomcat:
    connection-timeout: 60000  # 60秒连接超时
```

#### C. Nginx超时设置（如果使用）
```nginx
client_body_timeout 300s;
proxy_connect_timeout 1800s;
proxy_send_timeout 1800s;
proxy_read_timeout 1800s;
```

### 4. 内存不足错误

**症状**：上传大文件时服务器内存不足

**解决方案**：

#### A. 调整JVM内存设置
```bash
# 在启动Spring Boot时增加内存
export JAVA_OPTS="-Xmx4g -Xms2g"
./mvnw spring-boot:run
```

#### B. 使用流式处理
配置文件已设置：
```yaml
spring:
  servlet:
    multipart:
      file-size-threshold: 10MB  # 超过10MB写入磁盘
```

### 5. 磁盘空间不足

**症状**：上传失败，提示磁盘空间不足

**解决方案**：

#### A. 检查磁盘空间
```bash
df -h
```

#### B. 清理临时文件
```bash
# 清理Spring Boot临时文件
rm -rf ./temp/*

# 清理系统临时文件
sudo rm -rf /tmp/tomcat*
```

#### C. 修改上传路径
在 `application.yml` 中修改：
```yaml
app:
  upload:
    path: /path/to/large/disk/uploads
```

## 🔧 性能优化建议

### 1. 网络优化
- 使用有线网络连接
- 确保网络带宽充足
- 避免在网络高峰期上传

### 2. 服务器优化
- 增加服务器内存
- 使用SSD存储
- 优化数据库连接池

### 3. 浏览器优化
- 使用现代浏览器（Chrome、Firefox、Safari）
- 关闭不必要的标签页
- 禁用浏览器扩展

## 📊 监控和调试

### 1. 查看上传进度
打开浏览器开发者工具（F12）查看：
- Network标签：监控上传请求
- Console标签：查看调试信息

### 2. 后端日志
```bash
# 查看Spring Boot日志
tail -f backend/logs/spring.log

# 或者在控制台查看实时日志
cd backend
./mvnw spring-boot:run
```

### 3. 系统资源监控
```bash
# 监控内存使用
free -h

# 监控磁盘IO
iostat -x 1

# 监控网络连接
netstat -an | grep :8080
```

## 🎯 最佳实践

### 1. 上传前准备
- 确保网络连接稳定
- 关闭不必要的应用程序
- 检查磁盘空间充足

### 2. 上传过程中
- 不要关闭浏览器
- 不要切换网络连接
- 耐心等待上传完成

### 3. 文件压缩建议
- 对于文本数据，使用ZIP压缩可以显著减小文件大小
- 对于已压缩的文件（如图片、视频），再次压缩效果有限

## 🆘 紧急处理

如果上传仍然失败，请尝试以下步骤：

1. **分割文件**：将大文件分割成多个小文件上传
2. **使用其他格式**：尝试不同的压缩格式
3. **联系技术支持**：提供详细的错误信息和日志

## 📞 获取帮助

如果问题仍然存在，请提供以下信息：
- 文件大小和格式
- 错误信息截图
- 浏览器开发者工具的Network和Console信息
- 服务器日志（如果可以访问）

---

> 💡 **提示**：大文件上传是一个复杂的过程，涉及多个组件。请按照本指南逐步排查，大多数问题都可以解决。 