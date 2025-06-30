<template>
  <div class="min-h-screen">
    <!-- 英雄区域 -->
    <section class="relative h-screen flex items-center justify-center overflow-hidden">
      <!-- 背景图片 -->
      <div class="absolute inset-0 z-0">
        <img 
          src="https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=1920&h=1080&fit=crop"
          alt="农业科技背景"
          class="w-full h-full object-cover"
        />
        <div class="absolute inset-0 bg-gradient-to-r from-blue-900/70 to-blue-600/50"></div>
      </div>

      <!-- 内容区域 -->
      <div 
        class="relative z-10 text-center text-white max-w-4xl mx-auto px-4"
        v-motion
        :initial="{ opacity: 0, y: 50 }"
        :enter="{ opacity: 1, y: 0, transition: { duration: 1000 } }"
      >
        <h1 class="text-5xl md:text-7xl font-bold mb-6 leading-tight">
          <span class="block">农业数据</span>
          <span class="block text-blue-300">资产交易平台</span>
        </h1>
        <p class="text-xl md:text-2xl mb-8 opacity-90 max-w-2xl mx-auto">
          专注于前沿科技研发与创新的综合性研究机构，为现代农业发展提供数据支持
        </p>
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <router-link 
            to="/search"
            class="bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-xl font-semibold text-lg transition-all transform hover:scale-105 shadow-lg"
          >
            开始探索
          </router-link>
          <button 
            @click="scrollToSearch"
            class="bg-white/20 backdrop-blur-sm hover:bg-white/30 text-white px-8 py-4 rounded-xl font-semibold text-lg transition-all border border-white/30"
          >
            了解更多
          </button>
        </div>
      </div>

      <!-- 浮动动画元素 -->
      <div class="absolute inset-0 z-5">
        <div 
          v-for="i in 6" 
          :key="i"
          :class="`absolute w-2 h-2 bg-blue-300/30 rounded-full animate-pulse`"
          :style="{
            left: Math.random() * 100 + '%',
            top: Math.random() * 100 + '%',
            animationDelay: Math.random() * 3 + 's'
          }"
        ></div>
      </div>
    </section>

    <!-- 搜索区域 -->
    <section ref="searchSection" class="py-16 bg-white">
      <div class="max-w-4xl mx-auto px-4 text-center">
        <div
          v-motion
          :initial="{ opacity: 0, y: 30 }"
          :visible-once="{ opacity: 1, y: 0, transition: { duration: 600 } }"
        >
          <h2 class="text-3xl font-bold text-gray-800 mb-4">发现优质农业数据</h2>
          <p class="text-gray-600 mb-8">搜索您需要的农业数据资产，助力农业科技创新</p>
          
          <SearchBar @search="handleSearch" class="max-w-2xl mx-auto" />
        </div>
      </div>
    </section>

    <!-- 热门推荐区域 -->
    <section class="py-16 bg-gray-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div
          v-motion
          :initial="{ opacity: 0, y: 30 }"
          :visible-once="{ opacity: 1, y: 0, transition: { duration: 600 } }"
          class="text-center mb-12"
        >
          <h2 class="text-3xl font-bold text-gray-800 mb-4">热门农业数据资产</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">
            精选高质量的农业数据资产，为您的科研和生产提供可靠的数据支持
          </p>
        </div>

        <!-- 资产网格 -->
        <div 
          v-if="hotAssets.length > 0"
          class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
        >
          <AssetCard 
            v-for="(asset, index) in hotAssets" 
            :key="asset.id"
            :asset="asset"
            v-motion
            :initial="{ opacity: 0, y: 30 }"
            :visible-once="{ 
              opacity: 1, 
              y: 0, 
              transition: { 
                duration: 600, 
                delay: index * 100 
              } 
            }"
          />
        </div>

        <!-- 加载状态 -->
        <div v-else-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div 
            v-for="i in 9" 
            :key="i"
            class="bg-white rounded-xl shadow-lg border border-gray-100 overflow-hidden animate-pulse"
          >
            <div class="h-48 bg-gray-200"></div>
            <div class="p-4">
              <div class="h-4 bg-gray-200 rounded mb-2"></div>
              <div class="h-3 bg-gray-200 rounded w-2/3 mb-2"></div>
              <div class="h-6 bg-gray-200 rounded w-1/3"></div>
            </div>
          </div>
        </div>

        <!-- 查看更多按钮 -->
        <div class="text-center mt-12">
          <router-link 
            to="/search"
            class="btn-primary text-lg px-8 py-3"
          >
            查看更多资产
          </router-link>
        </div>
      </div>
    </section>

    <!-- 统计数据区域 -->
    <section class="py-16 bg-blue-600 text-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8 text-center">
          <div
            v-motion
            :initial="{ opacity: 0, scale: 0.8 }"
            :visible-once="{ opacity: 1, scale: 1, transition: { duration: 600 } }"
          >
            <div class="text-4xl font-bold mb-2">{{ stats.totalAssets }}+</div>
            <div class="text-blue-200">农业数据资产</div>
          </div>
          <div
            v-motion
            :initial="{ opacity: 0, scale: 0.8 }"
            :visible-once="{ opacity: 1, scale: 1, transition: { duration: 600, delay: 100 } }"
          >
            <div class="text-4xl font-bold mb-2">{{ stats.totalUsers }}+</div>
            <div class="text-blue-200">注册用户</div>
          </div>
          <div
            v-motion
            :initial="{ opacity: 0, scale: 0.8 }"
            :visible-once="{ opacity: 1, scale: 1, transition: { duration: 600, delay: 200 } }"
          >
            <div class="text-4xl font-bold mb-2">{{ stats.totalTransactions }}+</div>
            <div class="text-blue-200">成功交易</div>
          </div>
          <div
            v-motion
            :initial="{ opacity: 0, scale: 0.8 }"
            :visible-once="{ opacity: 1, scale: 1, transition: { duration: 600, delay: 300 } }"
          >
            <div class="text-4xl font-bold mb-2">{{ stats.satisfaction }}%</div>
            <div class="text-blue-200">用户满意度</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import SearchBar from '@/components/common/SearchBar.vue'
import AssetCard from '@/components/common/AssetCard.vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const searchSection = ref(null)

const hotAssets = ref([])
const loading = ref(true)

const stats = ref({
  totalAssets: 1200,
  totalUsers: 850,
  totalTransactions: 640,
  satisfaction: 96
})

const fetchHotAssets = async () => {
  try {
    loading.value = true
    const response = await api.get('/assets/hot?page=0&size=9')
    if (response.data.success) {
      hotAssets.value = response.data.data.content
    }
  } catch (error) {
    console.error('获取热门资产失败:', error)
    // 在失败时不影响页面显示，只是不展示热门资产
    hotAssets.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = (keyword) => {
  router.push({ name: 'search', query: { q: keyword } })
}

const scrollToSearch = () => {
  searchSection.value?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  fetchHotAssets()
})
</script> 