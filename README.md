# 农业数据资产交易平台

## 项目概述
这是一个基于农业数据交易的电商平台，采用现代化技术栈构建，提供科技感的用户体验。平台包含完整的用户系统、资产交易系统和管理后台系统。

## 技术栈
- **后端**: Java Spring Boot 3.2 + MongoDB
- **前端**: Vue 3 + Vite + Tailwind CSS + Framer Motion
- **数据库**: MongoDB
- **认证**: JWT Token

## 功能特性

### 前台功能
- 🌾 农业数据资产展示与交易
- 🔍 智能搜索与推荐系统
- 👤 用户注册登录系统
- 💰 交易记录管理
- 📱 响应式设计
- ✨ 动态交互效果

### 后台管理系统
- 👑 **完整的权限角色体系**
  - 超级管理员：最高权限，唯一账户
  - 审核管理员：负责数据资产审核
  - 发布管理员：负责数据资产发布
  - 运营管理员：负责用户管理
- 🔄 **严格的数据流程管理**
  - 用户上传数据 → 审核管理员审核 → 发布管理员发布 → 前台展示
- 📊 **实时数据统计分析**
- 👥 **用户管理功能**
- 📝 **完整的操作日志记录**
- ⚙️ **系统配置管理**

## 管理后台账户

系统启动后会自动创建超级管理员账户：
- **用户名**: admin
- **密码**: admin123
- **权限**: 超级管理员（可创建其他管理员）

## 权限说明

| 角色 | 权限 | 说明 |
|------|------|------|
| 超级管理员 | 全部权限 | 系统最高权限，可管理所有功能 |
| 审核管理员 | 资产审核 | 审核用户上传的数据资产 |
| 发布管理员 | 资产发布 | 将审核通过的资产发布到前台 |
| 运营管理员 | 用户管理 | 管理用户状态和基本信息 |
| 普通用户 | 基础功能 | 浏览、搜索、购买数据资产 |

## 快速开始

### 后端启动
```bash
cd backend
./mvnw spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 访问管理后台
1. 使用管理员账户登录
2. 点击右上角用户菜单中的"后台"按钮
3. 或直接访问：http://localhost:5173/admin

**未登录用户访问后台**：
- 点击"后台"按钮会提示需要先登录
- 可在登录页面使用"管理员快速登录"按钮直接使用admin账户登录并跳转到后台

## 项目结构
```
agri_data/
├── backend/           # Spring Boot 后端
│   ├── src/main/java/com/agridata/
│   │   ├── controller/    # REST API控制器
│   │   │   ├── AdminController.java         # 管理员功能API
│   │   │   └── AssetReviewController.java   # 资产审核发布API
│   │   ├── service/       # 业务逻辑层
│   │   │   ├── AdminService.java           # 管理员服务
│   │   │   └── AssetReviewService.java     # 资产审核服务
│   │   ├── model/         # 数据模型
│   │   │   ├── User.java               # 用户模型（含角色）
│   │   │   ├── AgriculturalAsset.java  # 资产模型（含审核状态）
│   │   │   └── OperationLog.java       # 操作日志模型
│   │   └── repository/    # 数据访问层
│   └── ...
├── frontend/          # Vue 3 前端
│   ├── src/
│   │   ├── views/admin/           # 管理后台页面
│   │   │   └── AdminDashboard.vue # 管理仪表板
│   │   ├── components/layout/     # 布局组件
│   │   └── router/               # 路由配置
│   └── ...
└── README.md
```

## 数据流程

### 资产发布流程
1. **用户上传** - 普通用户上传农业数据资产
2. **待审核** - 资产进入待审核状态（PENDING）
3. **审核阶段** - 审核管理员审核资产
   - 通过：状态变为 APPROVED
   - 拒绝：状态变为 REJECTED，需要重新提交
4. **待发布** - 审核通过的资产等待发布
5. **发布阶段** - 发布管理员发布资产
   - 发布：状态变为 PUBLISHED，前台可见
   - 下线：状态变为 OFFLINE，前台不可见

### 用户管理流程
1. **用户注册** - 默认为普通用户（USER）
2. **状态管理** - 管理员可修改用户状态
   - ACTIVE：正常状态
   - SUSPENDED：暂停状态
   - BANNED：禁用状态
3. **角色管理** - 超级管理员可修改用户角色

## API接口

### 管理员API
- `GET /api/admin/stats` - 获取系统统计
- `GET /api/admin/users` - 获取用户列表
- `PUT /api/admin/users/{id}/status` - 更新用户状态
- `PUT /api/admin/users/{id}/role` - 更新用户角色
- `POST /api/admin/admins` - 创建管理员

### 资产审核API
- `GET /api/admin/assets/pending` - 获取待审核资产
- `POST /api/admin/assets/{id}/approve` - 审核通过
- `POST /api/admin/assets/{id}/reject` - 审核拒绝
- `POST /api/admin/assets/{id}/publish` - 发布资产
- `POST /api/admin/assets/{id}/offline` - 下线资产

## 环境要求
- Java 17+
- Node.js 18+
- MongoDB 6.0+

## 开发说明

### 后端开发
- 使用Spring Boot 3.2框架
- MongoDB作为数据存储
- JWT实现用户认证
- 完整的权限控制体系

### 前端开发
- Vue 3 Composition API
- Tailwind CSS样式框架
- 响应式设计支持移动端
- 现代化的用户界面

## 部署说明

### 生产环境配置
1. 修改数据库连接配置
2. 配置JWT密钥
3. 设置文件上传路径
4. 配置管理员邮箱通知

### 安全建议
1. 修改默认管理员密码
2. 启用HTTPS
3. 配置防火墙规则
4. 定期备份数据库 