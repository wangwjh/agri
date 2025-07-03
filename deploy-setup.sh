#!/bin/bash

# 农业数据交易平台部署设置脚本
echo "=== 农业数据交易平台部署设置 ==="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查是否在项目根目录
if [ ! -f "DEPLOYMENT_GUIDE.md" ]; then
    echo -e "${RED}错误：请在项目根目录运行此脚本${NC}"
    exit 1
fi

echo -e "${BLUE}1. 检查项目结构...${NC}"
if [ -d "frontend" ] && [ -d "backend" ]; then
    echo -e "${GREEN}✓ 项目结构正确${NC}"
else
    echo -e "${RED}✗ 项目结构不正确，请确保frontend和backend目录存在${NC}"
    exit 1
fi

echo -e "${BLUE}2. 检查前端依赖...${NC}"
cd frontend
if [ -f "package.json" ]; then
    echo -e "${GREEN}✓ 找到package.json${NC}"
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}正在安装前端依赖...${NC}"
        npm install
    fi
else
    echo -e "${RED}✗ 未找到package.json${NC}"
    exit 1
fi

echo -e "${BLUE}3. 检查后端配置...${NC}"
cd ../backend
if [ -f "pom.xml" ]; then
    echo -e "${GREEN}✓ 找到pom.xml${NC}"
else
    echo -e "${RED}✗ 未找到pom.xml${NC}"
    exit 1
fi

cd ..

echo -e "${BLUE}4. 创建环境变量模板...${NC}"

# 创建前端环境变量文件
if [ ! -f "frontend/.env.local" ]; then
    cat > frontend/.env.local << EOF
# 前端环境变量配置
# 请根据实际部署情况修改以下值

# API基础URL - 替换为你的Render后端URL
VITE_API_BASE_URL=https://your-backend-app.onrender.com

# 应用标题
VITE_APP_TITLE=农业数据交易平台

# 环境标识
VITE_APP_ENV=production
EOF
    echo -e "${GREEN}✓ 创建前端环境变量文件：frontend/.env.local${NC}"
else
    echo -e "${YELLOW}! 前端环境变量文件已存在${NC}"
fi

echo ""
echo -e "${GREEN}=== 部署准备完成 ===${NC}"
echo ""
echo -e "${YELLOW}下一步操作：${NC}"
echo "1. 修改 frontend/.env.local 中的后端URL"
echo "2. 修改 netlify.toml 中的后端URL"
echo "3. 按照 DEPLOYMENT_GUIDE.md 进行部署"
echo ""
echo -e "${BLUE}重要提醒：${NC}"
echo "• 确保已将代码推送到GitHub"
echo "• 先部署后端到Render，获取URL后再部署前端"
echo "• 记得设置所有必要的环境变量"
echo ""
echo -e "${GREEN}部署指南：${NC}查看 DEPLOYMENT_GUIDE.md 获取详细步骤" 