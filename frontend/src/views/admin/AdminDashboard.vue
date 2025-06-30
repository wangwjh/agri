<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航栏 -->
    <nav class="bg-white shadow-sm border-b">
      <div class="mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <h1 class="text-xl font-semibold text-gray-900">农业数据管理后台</h1>
          </div>
          <div class="flex items-center space-x-4">
            <!-- 实时时间显示 -->
            <div class="text-sm text-gray-600">
              {{ currentTime }}
            </div>
            <div class="text-sm text-gray-600">
              欢迎，{{ currentUser?.username }}
            </div>
            <button 
              @click="logout"
              class="text-sm text-red-600 hover:text-red-800">
              退出登录
            </button>
          </div>
        </div>
      </div>
    </nav>

    <div class="flex">
      <!-- 左侧菜单 -->
      <div class="w-64 bg-white shadow-sm h-screen">
        <nav class="mt-8">
          <div class="px-4">
            <ul class="space-y-2">
              <li>
                <button 
                  @click="activeTab = 'dashboard'"
                  :class="{'bg-blue-50 text-blue-700': activeTab === 'dashboard', 'text-gray-700': activeTab !== 'dashboard'}"
                  class="w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 transition-all duration-200">
                  📊 仪表板
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'users'"
                  :class="{'bg-blue-50 text-blue-700': activeTab === 'users', 'text-gray-700': activeTab !== 'users'}"
                  class="w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 transition-all duration-200">
                  👥 用户管理
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'review'"
                  :class="{'bg-blue-50 text-blue-700': activeTab === 'review', 'text-gray-700': activeTab !== 'review'}"
                  class="w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 transition-all duration-200">
                  🔍 资产审核
                  <span v-if="assetStats.review?.pending > 0" 
                        class="ml-2 inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 animate-pulse">
                    {{ assetStats.review.pending }}
                  </span>
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'publish'"
                  :class="{'bg-blue-50 text-blue-700': activeTab === 'publish', 'text-gray-700': activeTab !== 'publish'}"
                  class="w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 transition-all duration-200">
                  📤 资产发布
                </button>
              </li>
              <li>
                <button 
                  @click="activeTab = 'logs'"
                  :class="{'bg-blue-50 text-blue-700': activeTab === 'logs', 'text-gray-700': activeTab !== 'logs'}"
                  class="w-full text-left px-4 py-2 rounded-lg hover:bg-gray-50 transition-all duration-200">
                  📝 操作日志
                </button>
              </li>
            </ul>
          </div>
        </nav>
      </div>

      <!-- 主内容区域 -->
      <div class="flex-1 p-8">
        <!-- 仪表板 -->
        <div v-if="activeTab === 'dashboard'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-gray-900">系统概览</h2>
            <div class="flex space-x-2">
              <button 
                @click="refreshDashboard" 
                :disabled="refreshing"
                class="flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 transition-all">
                <svg :class="{'animate-spin': refreshing}" class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                刷新数据
              </button>
              <div class="flex items-center px-3 py-2 text-sm bg-green-50 text-green-700 rounded-md">
                <div class="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse"></div>
                系统运行正常
              </div>
            </div>
          </div>
          
          <!-- 加载状态 -->
          <div v-if="loading.stats || loading.assetStats" class="flex items-center justify-center py-8">
            <div class="text-gray-500">
              <svg class="animate-spin h-8 w-8 mx-auto mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              正在加载统计数据...
            </div>
          </div>
          
          <!-- 错误状态 -->
          <div v-else-if="errors.stats || errors.assetStats" class="bg-red-50 border border-red-200 rounded-lg p-4">
            <div class="text-red-800">
              <h3 class="font-semibold mb-2">加载失败</h3>
              <p v-if="errors.stats">{{ errors.stats }}</p>
              <p v-if="errors.assetStats">{{ errors.assetStats }}</p>
              <button 
                @click="loadStats(); loadAssetStats()" 
                class="mt-2 px-3 py-1 bg-red-100 text-red-700 rounded hover:bg-red-200">
                重试
              </button>
            </div>
          </div>
          
          <!-- 统计卡片 -->
          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <!-- 用户统计卡片 -->
            <div 
              class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl shadow-sm border border-blue-200 p-6 hover:shadow-md transition-all duration-300 transform hover:-translate-y-1 stat-card"
              v-motion
              :initial="{ opacity: 0, y: 20 }"
              :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 100 } }"
            >
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-blue-500 rounded-xl flex items-center justify-center shadow-lg">
                    <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-1a.5.5 0 01.5.5v.5" />
                    </svg>
                  </div>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-blue-700 truncate">总用户数</dt>
                    <dd class="text-2xl font-bold text-blue-900 count-number">
                      <CountUp :endVal="stats.users?.total || 0" :duration="2000" />
                    </dd>
                    <dd class="text-xs text-blue-600 mt-1">
                      活跃用户: {{ stats.users?.active || 0 }}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>

            <!-- 资产统计卡片 -->
            <div 
              class="bg-gradient-to-br from-green-50 to-green-100 rounded-xl shadow-sm border border-green-200 p-6 hover:shadow-md transition-all duration-300 transform hover:-translate-y-1 stat-card"
              v-motion
              :initial="{ opacity: 0, y: 20 }"
              :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 200 } }"
            >
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-green-500 rounded-xl flex items-center justify-center shadow-lg">
                    <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-green-700 truncate">已发布资产</dt>
                    <dd class="text-2xl font-bold text-green-900 count-number">
                      <CountUp :endVal="assetStats.publish?.published || 0" :duration="2000" />
                    </dd>
                    <dd class="text-xs text-green-600 mt-1">
                      总计: {{ (assetStats.publish?.published || 0) + (assetStats.review?.pending || 0) }}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>

            <!-- 待审核统计卡片 -->
            <div 
              class="bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-xl shadow-sm border border-yellow-200 p-6 hover:shadow-md transition-all duration-300 transform hover:-translate-y-1 stat-card"
              v-motion
              :initial="{ opacity: 0, y: 20 }"
              :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 300 } }"
            >
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-yellow-500 rounded-xl flex items-center justify-center shadow-lg">
                    <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-yellow-700 truncate">待审核资产</dt>
                    <dd class="text-2xl font-bold text-yellow-900 count-number">
                      <CountUp :endVal="assetStats.review?.pending || 0" :duration="2000" />
                    </dd>
                    <dd class="text-xs text-yellow-600 mt-1">
                      需要处理
                      <span v-if="assetStats.review?.pending > 0" class="inline-flex items-center ml-1">
                        <div class="w-1.5 h-1.5 bg-yellow-500 rounded-full animate-pulse status-indicator"></div>
                      </span>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>

            <!-- 系统健康状态卡片 -->
            <div 
              class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl shadow-sm border border-purple-200 p-6 hover:shadow-md transition-all duration-300 transform hover:-translate-y-1 stat-card"
              v-motion
              :initial="{ opacity: 0, y: 20 }"
              :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 400 } }"
            >
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <div class="w-12 h-12 bg-purple-500 rounded-xl flex items-center justify-center shadow-lg">
                    <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                    </svg>
                  </div>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-purple-700 truncate">系统状态</dt>
                    <dd class="text-2xl font-bold text-purple-900">
                      {{ systemHealth.status }}
                    </dd>
                    <dd class="text-xs text-purple-600 mt-1">
                      运行时间: {{ systemHealth.uptime }}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <!-- 数据可视化图表区域 -->
          <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
            <!-- 用户增长趋势图 -->
            <div 
              class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 chart-container"
              v-motion
              :initial="{ opacity: 0, y: 30 }"
              :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 500 } }"
            >
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-gray-900">用户增长趋势</h3>
                <select v-model="chartTimeRange" @change="updateUserGrowthChart" class="text-sm border border-gray-300 rounded px-2 py-1">
                  <option value="7d">最近7天</option>
                  <option value="30d">最近30天</option>
                  <option value="90d">最近90天</option>
                </select>
              </div>
              <div class="h-64">
                <canvas ref="userGrowthChart" class="w-full h-full"></canvas>
              </div>
            </div>

            <!-- 资产状态分布饼图 -->
            <div 
              class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 chart-container"
              v-motion
              :initial="{ opacity: 0, y: 30 }"
              :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 600 } }"
            >
              <h3 class="text-lg font-semibold text-gray-900 mb-4">资产状态分布</h3>
              <div class="h-64 flex items-center justify-center">
                <div class="grid grid-cols-2 gap-4 w-full">
                  <div class="space-y-3">
                    <div v-for="(item, index) in assetStatusData" :key="index" class="flex items-center">
                      <div :class="item.color" class="w-4 h-4 rounded-full mr-3"></div>
                      <span class="text-sm text-gray-600">{{ item.label }}</span>
                    </div>
                  </div>
                  <div class="flex items-center justify-center">
                    <div class="relative w-32 h-32">
                      <svg class="w-32 h-32 transform -rotate-90" viewBox="0 0 32 32">
                        <circle cx="16" cy="16" r="14" fill="none" stroke="#e5e7eb" stroke-width="2"></circle>
                        <circle 
                          v-for="(item, index) in assetStatusData" 
                          :key="index"
                          cx="16" cy="16" r="14" fill="none" 
                          :stroke="item.strokeColor" 
                          stroke-width="2"
                          :stroke-dasharray="`${item.percentage * 0.88} 88`"
                          :stroke-dashoffset="item.offset"
                          class="transition-all duration-1000 ease-out pie-circle"
                          :style="{ animationDelay: `${index * 200}ms` }"
                        ></circle>
                      </svg>
                      <div class="absolute inset-0 flex items-center justify-center">
                        <div class="text-center">
                          <div class="text-lg font-bold text-gray-900">{{ totalAssets }}</div>
                          <div class="text-xs text-gray-500">总计</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 最近活动时间线 -->
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
            v-motion
            :initial="{ opacity: 0, y: 30 }"
            :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 700 } }"
          >
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-lg font-semibold text-gray-900">最近活动</h3>
              <button 
                @click="loadRecentActivities" 
                class="text-sm text-blue-600 hover:text-blue-800 transition-colors">
                刷新
              </button>
            </div>
            
            <div class="space-y-4 max-h-96 overflow-y-auto custom-scrollbar">
              <div 
                v-for="(activity, index) in recentActivities" 
                :key="activity.id"
                class="flex items-start space-x-4 p-3 rounded-lg hover:bg-gray-50 transition-colors timeline-item"
                v-motion
                :initial="{ opacity: 0, x: -20 }"
                :enter="{ opacity: 1, x: 0, transition: { duration: 400, delay: index * 100 } }"
              >
                <div class="flex-shrink-0">
                  <div :class="getActivityIconClass(activity.type)" class="w-8 h-8 rounded-full flex items-center justify-center">
                    <component :is="getActivityIcon(activity.type)" class="w-4 h-4 text-white" />
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900">{{ activity.description }}</p>
                  <div class="flex items-center space-x-2 mt-1">
                    <p class="text-xs text-gray-500">{{ activity.user }}</p>
                    <span class="text-xs text-gray-400">•</span>
                    <p class="text-xs text-gray-500">{{ formatTimeAgo(activity.timestamp) }}</p>
                  </div>
                </div>
                <div class="flex-shrink-0">
                  <span :class="getActivityStatusClass(activity.status)" class="inline-flex px-2 py-1 text-xs font-medium rounded-full">
                    {{ activity.status }}
                  </span>
                </div>
              </div>
              
              <div v-if="recentActivities.length === 0" class="text-center py-8 text-gray-500">
                暂无最近活动
              </div>
            </div>
          </div>

          <!-- 快速操作面板 -->
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
            v-motion
            :initial="{ opacity: 0, y: 30 }"
            :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 800 } }"
          >
            <h3 class="text-lg font-semibold text-gray-900 mb-4">快速操作</h3>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <button 
                @click="activeTab = 'review'"
                class="flex flex-col items-center p-4 rounded-lg border-2 border-dashed border-gray-300 hover:border-blue-400 hover:bg-blue-50 transition-all duration-200 group quick-action-btn"
              >
                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center group-hover:bg-blue-200 transition-colors">
                  <svg class="w-4 h-4 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <span class="text-sm font-medium text-gray-700 mt-2 group-hover:text-blue-700">审核资产</span>
                <span v-if="assetStats.review?.pending > 0" class="text-xs text-blue-600 mt-1">{{ assetStats.review.pending }} 待处理</span>
              </button>

              <button 
                @click="activeTab = 'publish'"
                class="flex flex-col items-center p-4 rounded-lg border-2 border-dashed border-gray-300 hover:border-green-400 hover:bg-green-50 transition-all duration-200 group quick-action-btn"
              >
                <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center group-hover:bg-green-200 transition-colors">
                  <svg class="w-4 h-4 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16l-4-4m0 0l4-4m-4 4h18" />
                  </svg>
                </div>
                <span class="text-sm font-medium text-gray-700 mt-2 group-hover:text-green-700">发布资产</span>
              </button>

              <button 
                @click="openCreateAdminModal"
                class="flex flex-col items-center p-4 rounded-lg border-2 border-dashed border-gray-300 hover:border-purple-400 hover:bg-purple-50 transition-all duration-200 group quick-action-btn"
              >
                <div class="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center group-hover:bg-purple-200 transition-colors">
                  <svg class="w-4 h-4 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                  </svg>
                </div>
                <span class="text-sm font-medium text-gray-700 mt-2 group-hover:text-purple-700">创建管理员</span>
              </button>

              <button 
                @click="exportSystemReport"
                class="flex flex-col items-center p-4 rounded-lg border-2 border-dashed border-gray-300 hover:border-yellow-400 hover:bg-yellow-50 transition-all duration-200 group quick-action-btn"
              >
                <div class="w-8 h-8 bg-yellow-100 rounded-full flex items-center justify-center group-hover:bg-yellow-200 transition-colors">
                  <svg class="w-4 h-4 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3M3 17V7a2 2 0 012-2h6l2 2h6a2 2 0 012 2v10a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
                  </svg>
                </div>
                <span class="text-sm font-medium text-gray-700 mt-2 group-hover:text-yellow-700">导出报告</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeTab === 'users'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-gray-900">用户管理</h2>
            <div class="flex space-x-4">
              <!-- 创建管理员按钮 -->
              <button 
                @click="openCreateAdminModal"
                class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors">
                创建管理员
              </button>
              <select v-model="userFilters.role" @change="loadUsers" class="border rounded px-3 py-2">
                <option value="">所有角色</option>
                <option value="USER">普通用户</option>
                <option value="REVIEWER">审核管理员</option>
                <option value="PUBLISHER">发布管理员</option>
                <option value="OPERATOR">运营管理员</option>
              </select>
              <select v-model="userFilters.status" @change="loadUsers" class="border rounded px-3 py-2">
                <option value="">所有状态</option>
                <option value="ACTIVE">正常</option>
                <option value="SUSPENDED">暂停</option>
                <option value="BANNED">禁用</option>
              </select>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading.users" class="flex items-center justify-center py-8">
            <div class="text-gray-500">
              <svg class="animate-spin h-6 w-6 mx-auto mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              正在加载用户列表...
            </div>
          </div>
          
          <!-- 错误状态 -->
          <div v-else-if="errors.users" class="bg-red-50 border border-red-200 rounded-lg p-4">
            <div class="text-red-800">
              <h3 class="font-semibold mb-2">加载失败</h3>
              <p>{{ errors.users }}</p>
              <button 
                @click="loadUsers" 
                class="mt-2 px-3 py-1 bg-red-100 text-red-700 rounded hover:bg-red-200">
                重试
              </button>
            </div>
          </div>

          <!-- 用户列表 -->
          <div v-else class="bg-white shadow rounded-lg overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">用户</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">角色</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">注册时间</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="user in users.content" :key="user.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="flex-shrink-0 h-10 w-10">
                        <img class="h-10 w-10 rounded-full" :src="user.avatar || '/default-avatar.svg'" alt="">
                      </div>
                      <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">{{ user.username }}</div>
                        <div class="text-sm text-gray-500">{{ user.email }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="getRoleBadgeClass(user.role)" class="inline-flex px-2 text-xs font-semibold rounded-full">
                      {{ getRoleText(user.role) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="getStatusBadgeClass(user.status)" class="inline-flex px-2 text-xs font-semibold rounded-full">
                      {{ getStatusText(user.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatDate(user.createTime) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button @click="openUserModal(user)" class="text-indigo-600 hover:text-indigo-900">编辑</button>
                  </td>
                </tr>
              </tbody>
            </table>
            
            <!-- 空状态 -->
            <div v-if="!users.content || users.content.length === 0" class="text-center py-8 text-gray-500">
              暂无用户数据
            </div>
          </div>
        </div>

        <!-- 资产审核 -->
        <div v-if="activeTab === 'review'" class="space-y-6">
          <h2 class="text-2xl font-bold text-gray-900">资产审核</h2>
          
          <!-- 审核列表 -->
          <div class="bg-white shadow rounded-lg overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">资产名称</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">所有者</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">价格</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">提交时间</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="asset in pendingAssets.content" :key="asset.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm font-medium text-gray-900">{{ asset.name }}</div>
                    <div class="text-sm text-gray-500">{{ asset.description?.substring(0, 50) }}...</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ asset.ownerName }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    ¥{{ asset.price }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatDate(asset.createTime) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    <div class="flex space-x-2">
                      <button 
                        @click="viewAssetDetail(asset)"
                        class="text-blue-600 hover:text-blue-900 text-sm"
                        title="查看详情"
                      >
                        详情
                      </button>
                      <button 
                        @click="approveAsset(asset.id)"
                        class="text-green-600 hover:text-green-900 text-sm"
                      >
                        通过
                      </button>
                      <button 
                        @click="rejectAsset(asset.id)"
                        class="text-red-600 hover:text-red-900 text-sm"
                      >
                        拒绝
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 资产发布 -->
        <div v-if="activeTab === 'publish'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-gray-900">资产发布</h2>
            <div class="flex space-x-4">
              <select v-model="publishFilter" @change="loadPublishAssets" class="border rounded px-3 py-2">
                <option value="approved">待发布资产</option>
                <option value="published">已发布资产</option>
              </select>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading.publishAssets" class="flex items-center justify-center py-8">
            <div class="text-gray-500">
              <svg class="animate-spin h-6 w-6 mx-auto mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              正在加载资产列表...
            </div>
          </div>
          
          <!-- 错误状态 -->
          <div v-else-if="errors.publishAssets" class="bg-red-50 border border-red-200 rounded-lg p-4">
            <div class="text-red-800">
              <h3 class="font-semibold mb-2">加载失败</h3>
              <p>{{ errors.publishAssets }}</p>
              <button 
                @click="loadPublishAssets" 
                class="mt-2 px-3 py-1 bg-red-100 text-red-700 rounded hover:bg-red-200">
                重试
              </button>
            </div>
          </div>

          <!-- 资产列表 -->
          <div v-else class="bg-white shadow rounded-lg overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">资产名称</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">所有者</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">价格</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">审核状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">发布状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">时间</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="asset in publishAssets.content" :key="asset.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm font-medium text-gray-900">{{ asset.name }}</div>
                    <div class="text-sm text-gray-500">{{ asset.description?.substring(0, 50) }}...</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ asset.ownerName }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    ¥{{ asset.price }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="getReviewStatusBadgeClass(asset.reviewStatus)" class="inline-flex px-2 text-xs font-semibold rounded-full">
                      {{ getReviewStatusText(asset.reviewStatus) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="getPublishStatusBadgeClass(asset.publishStatus)" class="inline-flex px-2 text-xs font-semibold rounded-full">
                      {{ getPublishStatusText(asset.publishStatus) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div v-if="asset.reviewTime">审核：{{ formatDate(asset.reviewTime) }}</div>
                    <div v-if="asset.publishTime">发布：{{ formatDate(asset.publishTime) }}</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    <div class="flex space-x-2">
                      <button 
                        @click="viewAssetDetail(asset)"
                        class="text-blue-600 hover:text-blue-900 text-sm"
                        title="查看详情"
                      >
                        详情
                      </button>
                      <button 
                        v-if="asset.publishStatus === 'DRAFT'"
                        @click="publishAsset(asset.id)"
                        class="text-green-600 hover:text-green-900 text-sm"
                      >
                        发布
                      </button>
                      <button 
                        v-if="asset.publishStatus === 'PUBLISHED'"
                        @click="offlineAsset(asset.id)"
                        class="text-red-600 hover:text-red-900 text-sm"
                      >
                        下线
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
            
            <!-- 空状态 -->
            <div v-if="!publishAssets.content || publishAssets.content.length === 0" class="text-center py-8 text-gray-500">
              <div v-if="publishFilter === 'approved'">暂无待发布资产</div>
              <div v-else>暂无已发布资产</div>
            </div>
          </div>
        </div>

        <!-- 其他标签内容... -->
      </div>
    </div>

    <!-- 用户编辑模态框 -->
    <div v-if="showUserModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">编辑用户</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">用户名</label>
              <input v-model="editingUser.username" type="text" class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2" readonly>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">状态</label>
              <select v-model="editingUser.status" class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
                <option value="ACTIVE">正常</option>
                <option value="SUSPENDED">暂停</option>
                <option value="BANNED">禁用</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">角色</label>
              <select v-model="editingUser.role" class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
                <option value="USER">普通用户</option>
                <option value="REVIEWER">审核管理员</option>
                <option value="PUBLISHER">发布管理员</option>
                <option value="OPERATOR">运营管理员</option>
              </select>
            </div>
          </div>
          <div class="flex justify-end space-x-3 mt-6">
            <button @click="closeUserModal" class="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400">
              取消
            </button>
            <button @click="saveUser" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
              保存
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建管理员模态框 -->
    <div v-if="showCreateAdminModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">创建管理员</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">用户名</label>
              <input 
                v-model="newAdmin.username" 
                type="text" 
                class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                placeholder="请输入用户名">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">密码</label>
              <input 
                v-model="newAdmin.password" 
                type="password" 
                class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                placeholder="请输入密码">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">管理员角色</label>
              <select v-model="newAdmin.role" class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
                <option value="">请选择角色</option>
                <option value="REVIEWER">审核管理员</option>
                <option value="PUBLISHER">发布管理员</option>
                <option value="OPERATOR">运营管理员</option>
              </select>
            </div>
          </div>
          <div class="flex justify-end space-x-3 mt-6">
            <button @click="closeCreateAdminModal" class="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400">
              取消
            </button>
            <button 
              @click="createAdmin" 
              :disabled="!isNewAdminValid"
              class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400 disabled:cursor-not-allowed">
              创建
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 资产详情模态框 -->
    <AssetDetailModal 
      :show="showAssetModal"
      :asset="selectedAsset"
      @close="closeAssetModal"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AssetDetailModal from '@/components/admin/AssetDetailModal.vue'
import api from '@/services/api'

// 引入图标组件
import { 
  UserPlusIcon, 
  CheckCircleIcon, 
  XCircleIcon, 
  ClockIcon,
  DocumentTextIcon,
  CogIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()
const authStore = useAuthStore()

const currentUser = computed(() => authStore.user)
const activeTab = ref('dashboard')

// 响应式数据
const loading = reactive({
  stats: false,
  assetStats: false,
  users: false,
  pendingAssets: false,
  publishAssets: false
})

const errors = reactive({
  stats: null,
  assetStats: null,
  users: null,
  pendingAssets: null,
  publishAssets: null
})

const stats = ref({})
const assetStats = ref({})
const users = ref({ content: [] })
const pendingAssets = ref({ content: [] })
const publishAssets = ref({ content: [] })

// 新增的响应式数据
const currentTime = ref('')
const refreshing = ref(false)
const systemHealth = ref({
  status: '正常',
  uptime: '0小时'
})
const chartTimeRange = ref('7d')
const userGrowthChart = ref(null)
const recentActivities = ref([])

// 计数动画组件
const CountUp = {
  props: {
    endVal: {
      type: Number,
      default: 0
    },
    duration: {
      type: Number,
      default: 2000
    }
  },
  setup(props) {
    const displayValue = ref(0)
    
    onMounted(() => {
      const increment = props.endVal / (props.duration / 16)
      let current = 0
      
      const timer = setInterval(() => {
        current += increment
        if (current >= props.endVal) {
          displayValue.value = props.endVal
          clearInterval(timer)
        } else {
          displayValue.value = Math.floor(current)
        }
      }, 16)
    })
    
    return { displayValue }
  },
  template: '<span>{{ displayValue.toLocaleString() }}</span>'
}

// 资产状态分布数据
const assetStatusData = computed(() => {
  const published = assetStats.value.publish?.published || 0
  const pending = assetStats.value.review?.pending || 0
  const approved = assetStats.value.review?.approved || 0
  const rejected = assetStats.value.review?.rejected || 0
  
  const total = published + pending + approved + rejected || 1
  
  let offset = 0
  const data = [
    {
      label: '已发布',
      value: published,
      percentage: (published / total) * 100,
      color: 'bg-green-500',
      strokeColor: '#10b981',
      offset: offset
    },
    {
      label: '待审核',
      value: pending,
      percentage: (pending / total) * 100,
      color: 'bg-yellow-500',
      strokeColor: '#f59e0b',
      offset: offset += (published / total) * 88
    },
    {
      label: '已通过',
      value: approved,
      percentage: (approved / total) * 100,
      color: 'bg-blue-500',
      strokeColor: '#3b82f6',
      offset: offset += (pending / total) * 88
    },
    {
      label: '已拒绝',
      value: rejected,
      percentage: (rejected / total) * 100,
      color: 'bg-red-500',
      strokeColor: '#ef4444',
      offset: offset += (approved / total) * 88
    }
  ]
  
  return data.filter(item => item.value > 0)
})

const totalAssets = computed(() => {
  return assetStatusData.value.reduce((sum, item) => sum + item.value, 0)
})

// 筛选器
const userFilters = reactive({
  role: '',
  status: ''
})

const publishFilter = ref('approved')

// 模态框
const showUserModal = ref(false)
const editingUser = ref({})
const showCreateAdminModal = ref(false)
const newAdmin = ref({})

// 加载数据的方法
const loadStats = async () => {
  try {
    loading.stats = true
    errors.stats = null
    const response = await api.get('/admin/stats')
    if (response.data.success) {
      stats.value = response.data.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    errors.stats = error.response?.data?.message || '加载失败'
  } finally {
    loading.stats = false
  }
}

const loadAssetStats = async () => {
  try {
    loading.assetStats = true
    errors.assetStats = null
    // 修复API路径，使用后端实际提供的接口
    const response = await api.get('/admin/assets/stats')
    
    if (response.data.success) {
      // 后端返回的数据结构是嵌套的，需要重新组织
      const statsData = response.data.data
      assetStats.value = {
        review: {
          pending: statsData.review?.pending || 0,
          approved: statsData.review?.approved || 0,
          rejected: statsData.review?.rejected || 0
        },
        publish: {
          published: statsData.publish?.published || 0,
          draft: statsData.publish?.draft || 0,
          offline: statsData.publish?.offline || 0
        }
      }
    }
  } catch (error) {
    console.error('加载资产统计失败:', error)
    errors.assetStats = error.response?.data?.message || '加载失败'
  } finally {
    loading.assetStats = false
  }
}

const loadUsers = async () => {
  try {
    loading.users = true
    errors.users = null
    
    const params = new URLSearchParams()
    if (userFilters.role) params.append('role', userFilters.role)
    if (userFilters.status) params.append('status', userFilters.status)
    
    const response = await api.get('/admin/users?' + params.toString())
    if (response.data.success) {
      users.value = response.data.data
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    errors.users = error.response?.data?.message || '加载失败'
  } finally {
    loading.users = false
  }
}

const loadPendingAssets = async () => {
  try {
    loading.pendingAssets = true
    errors.pendingAssets = null
    const response = await api.get('/admin/assets/pending')
    if (response.data.success) {
      pendingAssets.value = response.data.data
    }
  } catch (error) {
    console.error('加载待审核资产失败:', error)
    errors.pendingAssets = error.response?.data?.message || '加载失败'
  } finally {
    loading.pendingAssets = false
  }
}

const loadPublishAssets = async () => {
  try {
    loading.publishAssets = true
    errors.publishAssets = null
    
    let endpoint
    if (publishFilter.value === 'approved') {
      // 修复API路径
      endpoint = '/admin/assets/approved-unpublished'
    } else {
      // 修复API路径
      endpoint = '/admin/assets/published'
    }
    
    const response = await api.get(endpoint)
    if (response.data.success) {
      publishAssets.value = response.data.data
    }
  } catch (error) {
    console.error('加载发布资产失败:', error)
    errors.publishAssets = error.response?.data?.message || '加载失败'
  } finally {
    loading.publishAssets = false
  }
}

// 新增功能方法
const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const refreshDashboard = async () => {
  refreshing.value = true
  try {
    await Promise.all([
      loadStats(),
      loadAssetStats(),
      loadRecentActivities()
    ])
  } finally {
    refreshing.value = false
  }
}

const loadRecentActivities = async () => {
  try {
    // 模拟获取最近活动数据
    const activities = [
      {
        id: 1,
        type: 'user_register',
        description: '新用户注册',
        user: '张三',
        timestamp: new Date(Date.now() - 5 * 60 * 1000),
        status: '成功'
      },
      {
        id: 2,
        type: 'asset_approve',
        description: '资产审核通过',
        user: '审核员李四',
        timestamp: new Date(Date.now() - 15 * 60 * 1000),
        status: '已完成'
      },
      {
        id: 3,
        type: 'asset_publish',
        description: '资产发布上线',
        user: '发布员王五',
        timestamp: new Date(Date.now() - 30 * 60 * 1000),
        status: '已发布'
      },
      {
        id: 4,
        type: 'asset_reject',
        description: '资产审核拒绝',
        user: '审核员李四',
        timestamp: new Date(Date.now() - 45 * 60 * 1000),
        status: '已拒绝'
      },
      {
        id: 5,
        type: 'system_config',
        description: '系统配置更新',
        user: '管理员',
        timestamp: new Date(Date.now() - 60 * 60 * 1000),
        status: '已完成'
      }
    ]
    
    recentActivities.value = activities
  } catch (error) {
    console.error('加载最近活动失败:', error)
  }
}

const updateUserGrowthChart = () => {
  // 这里可以添加Chart.js或其他图表库的实现
  console.log('更新用户增长图表，时间范围:', chartTimeRange.value)
}

const exportSystemReport = () => {
  // 导出系统报告
  alert('系统报告导出功能正在开发中...')
}

// 活动相关的工具函数
const getActivityIcon = (type) => {
  const iconMap = {
    'user_register': UserPlusIcon,
    'asset_approve': CheckCircleIcon,
    'asset_reject': XCircleIcon,
    'asset_publish': DocumentTextIcon,
    'system_config': CogIcon
  }
  return iconMap[type] || ClockIcon
}

const getActivityIconClass = (type) => {
  const classMap = {
    'user_register': 'bg-blue-500',
    'asset_approve': 'bg-green-500',
    'asset_reject': 'bg-red-500',
    'asset_publish': 'bg-purple-500',
    'system_config': 'bg-gray-500'
  }
  return classMap[type] || 'bg-gray-500'
}

const getActivityStatusClass = (status) => {
  const classMap = {
    '成功': 'bg-green-100 text-green-800',
    '已完成': 'bg-blue-100 text-blue-800',
    '已发布': 'bg-purple-100 text-purple-800',
    '已拒绝': 'bg-red-100 text-red-800',
    '失败': 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

const formatTimeAgo = (timestamp) => {
  const now = new Date()
  const diff = now - new Date(timestamp)
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  return `${days}天前`
}

// 系统健康状态更新
const updateSystemHealth = () => {
  const startTime = Date.now() - Math.random() * 86400000 * 30 // 模拟运行时间
  const uptime = Math.floor((Date.now() - startTime) / 3600000)
  systemHealth.value = {
    status: '正常',
    uptime: `${uptime}小时`
  }
}

// 审核通过
const approveAsset = async (assetId) => {
  try {
    const comment = prompt('请输入审核意见（可选）:')
    const response = await api.post(`/admin/assets/${assetId}/approve`, { comment })
    if (response.data.success) {
      alert('审核通过成功')
      loadPendingAssets()
      loadAssetStats()
    }
  } catch (error) {
    console.error('审核失败:', error)
    alert('审核失败')
  }
}

// 审核拒绝
const rejectAsset = async (assetId) => {
  try {
    const comment = prompt('请输入拒绝原因:')
    if (!comment) return
    
    const response = await api.post(`/admin/assets/${assetId}/reject`, { comment })
    if (response.data.success) {
      alert('审核拒绝成功')
      loadPendingAssets()
      loadAssetStats()
    }
  } catch (error) {
    console.error('审核失败:', error)
    alert('审核失败')
  }
}

// 打开用户编辑模态框
const openUserModal = (user) => {
  editingUser.value = { 
    ...user, 
    originalStatus: user.status, 
    originalRole: user.role 
  }
  showUserModal.value = true
}

// 关闭用户编辑模态框
const closeUserModal = () => {
  showUserModal.value = false
  editingUser.value = {}
}

// 保存用户
const saveUser = async () => {
  try {
    // 更新用户状态
    if (editingUser.value.status !== editingUser.value.originalStatus) {
      await api.put(`/admin/users/${editingUser.value.id}/status?status=${editingUser.value.status}`)
    }
    
    // 更新用户角色
    if (editingUser.value.role !== editingUser.value.originalRole) {
      await api.put(`/admin/users/${editingUser.value.id}/role?role=${editingUser.value.role}`)
    }
    
    alert('保存成功')
    closeUserModal()
    loadUsers()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败：' + (error.response?.data?.message || error.message))
  }
}

// 退出登录
const logout = () => {
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('user')
  router.push('/login')
}

// 工具函数
const getRoleText = (role) => {
  const roleMap = {
    'USER': '普通用户',
    'REVIEWER': '审核管理员',
    'PUBLISHER': '发布管理员',
    'OPERATOR': '运营管理员',
    'SUPER_ADMIN': '超级管理员'
  }
  return roleMap[role] || role
}

const getStatusText = (status) => {
  const statusMap = {
    'ACTIVE': '正常',
    'SUSPENDED': '暂停',
    'BANNED': '禁用'
  }
  return statusMap[status] || status
}

const getReviewStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || status
}

const getPublishStatusText = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'OFFLINE': '已下线'
  }
  return statusMap[status] || status
}

const getRoleBadgeClass = (role) => {
  const classMap = {
    'USER': 'bg-gray-100 text-gray-800',
    'REVIEWER': 'bg-blue-100 text-blue-800',
    'PUBLISHER': 'bg-green-100 text-green-800',
    'OPERATOR': 'bg-purple-100 text-purple-800',
    'SUPER_ADMIN': 'bg-red-100 text-red-800'
  }
  return classMap[role] || 'bg-gray-100 text-gray-800'
}

const getStatusBadgeClass = (status) => {
  const classMap = {
    'ACTIVE': 'bg-green-100 text-green-800',
    'SUSPENDED': 'bg-yellow-100 text-yellow-800',
    'BANNED': 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

const getReviewStatusBadgeClass = (status) => {
  const classMap = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'APPROVED': 'bg-green-100 text-green-800',
    'REJECTED': 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

const getPublishStatusBadgeClass = (status) => {
  const classMap = {
    'DRAFT': 'bg-gray-100 text-gray-800',
    'PUBLISHED': 'bg-blue-100 text-blue-800',
    'OFFLINE': 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 打开创建管理员模态框
const openCreateAdminModal = () => {
  newAdmin.value = {
    username: '',
    password: '',
    role: ''
  }
  showCreateAdminModal.value = true
}

// 创建管理员
const createAdmin = async () => {
  try {
    const requestData = {
      username: newAdmin.value.username,
      password: newAdmin.value.password,
      email: `${newAdmin.value.username}@admin.com`,
      role: newAdmin.value.role
    }

    const response = await api.post('/admin/manage/create-admin-user', requestData)
    if (response.data.success) {
      alert('管理员创建成功')
      closeCreateAdminModal()
      loadUsers()
    } else {
      alert('创建失败：' + response.data.message)
    }
  } catch (error) {
    console.error('创建管理员失败:', error)
    alert('创建管理员失败：' + (error.response?.data?.message || error.message))
  }
}

// 关闭创建管理员模态框
const closeCreateAdminModal = () => {
  showCreateAdminModal.value = false
  newAdmin.value = {}
}

// 发布资产
const publishAsset = async (assetId) => {
  try {
    const comment = prompt('请输入发布说明（可选）:')
    const response = await api.post(`/admin/assets/${assetId}/publish`, { comment })
    if (response.data.success) {
      alert('资产发布成功')
      loadPublishAssets()
      loadAssetStats()
    } else {
      alert('发布失败：' + response.data.message)
    }
  } catch (error) {
    console.error('发布资产失败:', error)
    alert('发布失败：' + (error.response?.data?.message || error.message))
  }
}

// 下线资产
const offlineAsset = async (assetId) => {
  try {
    const reason = prompt('请输入下线原因:')
    if (!reason) return
    
    const response = await api.post(`/admin/assets/${assetId}/offline`, { reason })
    if (response.data.success) {
      alert('资产下线成功')
      loadPublishAssets()
      loadAssetStats()
    } else {
      alert('下线失败：' + response.data.message)
    }
  } catch (error) {
    console.error('下线资产失败:', error)
    alert('下线失败：' + (error.response?.data?.message || error.message))
  }
}

// 资产详情模态框
const showAssetModal = ref(false)
const selectedAsset = ref(null)

const viewAssetDetail = (asset) => {
  selectedAsset.value = asset
  showAssetModal.value = true
}

const closeAssetModal = () => {
  showAssetModal.value = false
  selectedAsset.value = null
}

// 定时器
let timeTimer = null
let healthTimer = null

onMounted(async () => {
  // 初始化数据
  await Promise.all([
    loadStats(),
    loadAssetStats(),
    loadUsers(),
    loadPendingAssets(),
    loadPublishAssets(),
    loadRecentActivities()
  ])
  
  // 启动定时器
  updateCurrentTime()
  updateSystemHealth()
  
  timeTimer = setInterval(updateCurrentTime, 1000)
  healthTimer = setInterval(updateSystemHealth, 60000)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  if (healthTimer) clearInterval(healthTimer)
})
</script>

<style scoped>
/* 统计卡片悬停效果 */
.stat-card {
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

/* 数字计数动画 */
@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.count-number {
  animation: countUp 0.6s ease-out;
}

/* 脉冲动画 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 旋转动画 */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* 进度条动画 */
.progress-bar {
  background: linear-gradient(45deg, 
    rgba(255, 255, 255, 0.1) 25%, 
    transparent 25%, 
    transparent 50%, 
    rgba(255, 255, 255, 0.1) 50%, 
    rgba(255, 255, 255, 0.1) 75%, 
    transparent 75%);
  background-size: 40px 40px;
  animation: progress 2s linear infinite;
}

@keyframes progress {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 40px 40px;
  }
}

/* 饼图圆环动画 */
.pie-circle {
  stroke-dasharray: 0 88;
  animation: fillPie 1.5s ease-out forwards;
}

@keyframes fillPie {
  to {
    stroke-dasharray: var(--dash-array) 88;
  }
}

/* 活动时间线动画 */
.timeline-item {
  opacity: 0;
  transform: translateX(-20px);
  animation: slideInFromLeft 0.6s ease-out forwards;
}

.timeline-item:nth-child(1) { animation-delay: 0.1s; }
.timeline-item:nth-child(2) { animation-delay: 0.2s; }
.timeline-item:nth-child(3) { animation-delay: 0.3s; }
.timeline-item:nth-child(4) { animation-delay: 0.4s; }
.timeline-item:nth-child(5) { animation-delay: 0.5s; }

@keyframes slideInFromLeft {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 快速操作按钮悬停效果 */
.quick-action-btn {
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.quick-action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.quick-action-btn:hover::before {
  left: 100%;
}

/* 图表容器动画 */
.chart-container {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.8s ease-out forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 状态指示器动画 */
.status-indicator {
  position: relative;
}

.status-indicator::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: inherit;
  opacity: 0.3;
  animation: ping 2s cubic-bezier(0, 0, 0.2, 1) infinite;
}

@keyframes ping {
  75%, 100% {
    transform: translate(-50%, -50%) scale(2);
    opacity: 0;
  }
}

/* 加载动画 */
.loading-spinner {
  border: 2px solid #f3f4f6;
  border-top: 2px solid #3b82f6;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
}

/* 数据卡片渐变背景 */
.gradient-card {
  background: linear-gradient(135deg, var(--gradient-from), var(--gradient-to));
  position: relative;
  overflow: hidden;
}

.gradient-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  pointer-events: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 1rem;
  }
  
  .chart-container {
    height: 200px;
  }
}

/* 暗色主题适配 */
@media (prefers-color-scheme: dark) {
  .gradient-card {
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  }
  
  .status-indicator {
    filter: brightness(1.2);
  }
}

/* 可访问性增强 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* 焦点状态 */
button:focus,
select:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

/* 滚动条样式 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style> 