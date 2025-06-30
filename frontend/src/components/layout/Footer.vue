<template>
  <footer class="bg-gradient-to-r from-blue-800 to-blue-900 text-white">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- 品牌信息 -->
        <div class="col-span-1 md:col-span-2">
          <div class="flex items-center space-x-2 mb-4">
            <div class="w-8 h-8 bg-white rounded-lg flex items-center justify-center">
              <span class="text-blue-600 font-bold text-sm">农</span>
            </div>
            <span class="text-xl font-bold">农业数据资产交易平台</span>
          </div>
          <p class="text-blue-200 mb-4 max-w-md">
            专注于农业数据资产的交易与共享，为现代农业发展提供数据支持，推动智慧农业创新。
          </p>
          <div class="flex space-x-4">
            <div class="w-8 h-8 bg-blue-700 rounded-lg flex items-center justify-center hover:bg-blue-600 transition-colors cursor-pointer">
              <span class="text-sm">📧</span>
            </div>
            <div class="w-8 h-8 bg-blue-700 rounded-lg flex items-center justify-center hover:bg-blue-600 transition-colors cursor-pointer">
              <span class="text-sm">📱</span>
            </div>
            <div class="w-8 h-8 bg-blue-700 rounded-lg flex items-center justify-center hover:bg-blue-600 transition-colors cursor-pointer">
              <span class="text-sm">🌐</span>
            </div>
          </div>
        </div>

        <!-- 快速导航 -->
        <div>
          <h3 class="text-lg font-semibold mb-4">快速导航</h3>
          <ul class="space-y-2">
            <li>
              <router-link to="/" class="text-blue-200 hover:text-white transition-colors">
                首页
              </router-link>
            </li>
            <li>
              <router-link to="/search" class="text-blue-200 hover:text-white transition-colors">
                搜索农业数据
              </router-link>
            </li>
            <li>
              <router-link to="/transactions" class="text-blue-200 hover:text-white transition-colors">
                交易记录
              </router-link>
            </li>
          </ul>
        </div>

        <!-- 联系信息 -->
        <div>
          <h3 class="text-lg font-semibold mb-4">联系我们</h3>
          <ul class="space-y-2 text-blue-200">
            <li class="flex items-center space-x-2">
              <span>📧</span>
              <span>contact@agridata.com</span>
            </li>
            <li class="flex items-center space-x-2">
              <span>📞</span>
              <span>400-123-4567</span>
            </li>
            <li class="flex items-center space-x-2">
              <span>📍</span>
              <span>北京市朝阳区农业科技园</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- 底部分割线和版权信息 -->
      <div class="border-t border-blue-700 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center">
        <p class="text-blue-200 text-sm">
          © 2024 农业数据资产交易平台. 保留所有权利.
        </p>
        <div class="flex space-x-6 mt-4 md:mt-0">
          <a href="#" class="text-blue-200 hover:text-white text-sm transition-colors">
            隐私政策
          </a>
          <a href="#" class="text-blue-200 hover:text-white text-sm transition-colors">
            使用条款
          </a>
          <a href="#" class="text-blue-200 hover:text-white text-sm transition-colors">
            帮助中心
          </a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
// 页脚组件无需特殊逻辑
</script> 