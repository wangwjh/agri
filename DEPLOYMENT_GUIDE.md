# 农业数据交易平台部署指南

## 架构概述
- **前端**: Vue 3 + Vite → Netlify
- **后端**: Spring Boot + Java 17 → Render
- **数据库**: MongoDB → MongoDB Atlas

## 部署步骤

### 1. MongoDB Atlas 数据库设置

#### 1.1 创建MongoDB Atlas账户
1. 访问 [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. 注册账户并登录
3. 创建免费的M0集群（512MB存储）

#### 1.2 配置数据库
1. 选择云服务商（推荐AWS）和地区（选择离用户最近的）
2. 集群名称：`agri-data-cluster`
3. 等待集群创建完成（约5-10分钟）

#### 1.3 设置数据库访问
1. **Database Access**：
   - 创建数据库用户
   - 用户名：`agri-admin`
   - 密码：生成强密码（记住这个密码）
   - 权限：`Read and write to any database`

2. **Network Access**：
   - 添加IP地址：`0.0.0.0/0`（允许所有IP访问）
   - 或者后续添加Render的IP地址

#### 1.4 获取连接字符串
1. 点击 `Connect` → `Connect your application`
2. 复制连接字符串，格式如：
   ```
   mongodb+srv://agri-admin:<password>@agri-data-cluster.xxxxx.mongodb.net/agri_trade_db?retryWrites=true&w=majority
   ```

### 2. 后端部署到Render

#### 2.1 创建Render账户
1. 访问 [Render](https://render.com/)
2. 使用GitHub账户登录

#### 2.2 创建Web Service
1. 点击 `New` → `Web Service`
2. 连接你的GitHub仓库
3. 选择分支：`main`
4. 配置如下：
   - **Name**: `agri-data-backend`
   - **Root Directory**: `backend`
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/agri-data-platform-1.0.0.jar --spring.profiles.active=prod`

#### 2.3 设置环境变量
在Render的Environment Variables中添加：
```
MONGO_URI=mongodb+srv://agri-admin:<your-password>@agri-data-cluster.xxxxx.mongodb.net/agri_trade_db?retryWrites=true&w=majority
MONGO_DATABASE=agri_trade_db
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
CORS_ORIGINS=https://your-frontend-app.netlify.app
PORT=8080
```

#### 2.4 部署
1. 点击 `Create Web Service`
2. 等待构建和部署完成（约5-10分钟）
3. 记录你的后端URL：`https://agri-data-backend.onrender.com`

### 3. 前端部署到Netlify

#### 3.1 创建Netlify账户
1. 访问 [Netlify](https://www.netlify.com/)
2. 使用GitHub账户登录

#### 3.2 部署前端
1. 点击 `New site from Git`
2. 选择GitHub并授权
3. 选择你的仓库
4. 配置如下：
   - **Branch**: `main`
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `frontend/dist`

#### 3.3 设置环境变量
在Netlify的Environment Variables中添加：
```
VITE_API_BASE_URL=https://agri-data-backend.onrender.com
VITE_APP_TITLE=农业数据交易平台
VITE_APP_ENV=production
```

#### 3.4 更新netlify.toml
编辑 `netlify.toml` 文件，将后端URL替换为实际的Render URL：
```toml
[[redirects]]
  from = "/api/*"
  to = "https://agri-data-backend.onrender.com/api/:splat"
  status = 200
  force = true
```

### 4. 域名和SSL配置

#### 4.1 Netlify域名
1. 在Netlify中，你会得到一个免费域名：`https://your-app-name.netlify.app`
2. 可以自定义域名前缀或购买自定义域名

#### 4.2 Render域名
1. Render自动提供HTTPS域名：`https://your-backend-app.onrender.com`
2. 可以添加自定义域名

### 5. 最终配置更新

#### 5.1 更新后端CORS配置
确保后端的CORS_ORIGINS环境变量包含你的前端域名：
```
CORS_ORIGINS=https://your-frontend-app.netlify.app,https://your-custom-domain.com
```

#### 5.2 测试部署
1. 访问你的前端URL
2. 测试用户注册、登录功能
3. 测试文件上传下载功能
4. 检查API调用是否正常

## 部署后维护

### 监控和日志
1. **Render**: 提供实时日志和监控
2. **Netlify**: 提供构建日志和分析
3. **MongoDB Atlas**: 提供数据库监控

### 自动部署
- 推送到GitHub主分支会自动触发重新部署
- 前端和后端都支持自动部署

### 性能优化
1. **Render**: 考虑升级到付费计划以获得更好性能
2. **MongoDB Atlas**: 监控数据库性能，必要时升级
3. **Netlify**: 利用CDN加速静态资源

## 常见问题

### 1. 后端启动失败
- 检查环境变量是否正确设置
- 查看Render的部署日志
- 确保MongoDB连接字符串正确

### 2. 前端API调用失败
- 检查netlify.toml中的API代理配置
- 确保后端CORS设置正确
- 检查环境变量是否正确

### 3. 文件上传失败
- 检查文件大小限制
- 确保后端有足够的存储空间
- 考虑使用云存储服务（如AWS S3）

### 4. 数据库连接问题
- 检查MongoDB Atlas的网络访问设置
- 确保连接字符串中的密码正确
- 检查数据库用户权限

## 成本估算

### 免费额度
- **Netlify**: 100GB带宽/月，300分钟构建时间/月
- **Render**: 750小时/月（免费服务会在不活跃时休眠）
- **MongoDB Atlas**: 512MB存储，无时间限制

### 升级建议
当流量增长时，考虑升级：
- **Netlify Pro**: $19/月，更多带宽和功能
- **Render**: $7/月起，专用实例，不休眠
- **MongoDB Atlas**: $9/月起，更多存储和性能

## 安全建议

1. **定期更新依赖**: 保持前后端依赖库最新
2. **环境变量管理**: 不要在代码中硬编码敏感信息
3. **HTTPS**: 确保所有通信都使用HTTPS
4. **数据库安全**: 定期备份，设置合适的访问权限
5. **监控**: 设置错误监控和性能监控

---

**部署完成后，你的应用将运行在：**
- 前端：`https://your-app.netlify.app`
- 后端：`https://your-backend.onrender.com`
- 数据库：MongoDB Atlas云端

记得将实际的URL替换到相应的配置文件中！ 