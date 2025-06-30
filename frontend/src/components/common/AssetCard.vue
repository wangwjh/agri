<template>
  <div class="card hover-lift cursor-pointer group" @click="navigateToDetail">
    <!-- 资产图片 -->
    <div class="relative h-48 overflow-hidden">
      <img 
        :src="assetImage" 
        :alt="asset.name"
        class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
        @error="handleImageError"
      />
      
      <!-- 质量评级徽章 -->
      <div class="absolute top-3 left-3">
        <span :class="qualityBadgeClass" class="px-2 py-1 rounded-full text-xs font-medium">
          {{ asset.qualityRating }}
        </span>
      </div>
      
      <!-- 点赞按钮 -->
      <div class="absolute top-3 right-3">
        <button 
          @click.stop="toggleLike"
          :disabled="likeLoading"
          class="bg-white/80 backdrop-blur-sm hover:bg-white p-2 rounded-full transition-all transform hover:scale-110 shadow-lg"
        >
          <HeartIcon 
            :class="[
              'w-5 h-5 transition-colors',
              isLiked ? 'text-red-500 fill-current' : 'text-gray-600'
            ]"
          />
        </button>
      </div>
      
      <!-- 渐变遮罩 -->
      <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
    </div>

    <!-- 卡片内容 -->
    <div class="p-4">
      <!-- 标题和描述 -->
      <div class="mb-3">
        <h3 class="text-lg font-semibold text-gray-800 mb-1 line-clamp-1">
          {{ asset.name }}
        </h3>
        <p class="text-gray-600 text-sm line-clamp-2">
          {{ asset.description }}
        </p>
      </div>

      <!-- 关键指标 -->
      <div class="mb-3">
        <div class="flex flex-wrap gap-1">
          <span 
            v-for="(value, key) in displayMetrics" 
            :key="key"
            class="text-xs bg-blue-50 text-blue-600 px-2 py-1 rounded-full"
          >
            {{ key }}: {{ value }}
          </span>
        </div>
      </div>

      <!-- 培养技术标签 -->
      <div class="mb-3">
        <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
          <span class="w-1.5 h-1.5 bg-green-400 rounded-full mr-1"></span>
          {{ asset.cultivationTechnique }}
        </span>
      </div>

      <!-- 底部信息 -->
      <div class="flex items-center justify-between">
        <!-- 价格 -->
        <div class="flex items-center space-x-1">
          <span class="text-2xl font-bold text-blue-600">
            ¥{{ formatPrice(asset.price) }}
          </span>
        </div>

        <!-- 点赞数和所有者 -->
        <div class="flex items-center space-x-3 text-sm text-gray-500">
          <div class="flex items-center space-x-1">
            <HeartIcon class="w-4 h-4" />
            <span>{{ formatNumber(currentLikeCount) }}</span>
          </div>
          <div class="flex items-center space-x-1">
            <UserIcon class="w-4 h-4" />
            <span class="truncate max-w-16">{{ asset.ownerName }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { HeartIcon, UserIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { defaultImages, handleImageError as baseHandleImageError } from '@/utils/imageUtils'

const props = defineProps({
  asset: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const authStore = useAuthStore()

const currentLikeCount = ref(props.asset.likeCount || 0)
const isLiked = ref(false)
const likeLoading = ref(false)

// 默认图片
const defaultImage = 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&h=300&fit=crop'

// 资产图片
const assetImage = computed(() => {
  if (props.asset.imageUrls && props.asset.imageUrls.length > 0) {
    return props.asset.imageUrls[0]
  }
  return defaultImages.farmland
})

// 质量评级徽章样式
const qualityBadgeClass = computed(() => {
  const rating = props.asset.qualityRating || 'B'
  const baseClass = 'px-2 py-1 rounded-full text-xs font-medium'
  
  if (rating.includes('A+')) {
    return `${baseClass} bg-green-100 text-green-800`
  } else if (rating.includes('A')) {
    return `${baseClass} bg-blue-100 text-blue-800`
  } else if (rating.includes('B')) {
    return `${baseClass} bg-yellow-100 text-yellow-800`
  } else {
    return `${baseClass} bg-gray-100 text-gray-800`
  }
})

// 显示的关键指标（最多3个）
const displayMetrics = computed(() => {
  const metrics = props.asset.metrics || {}
  const entries = Object.entries(metrics)
  const limited = entries.slice(0, 3)
  return Object.fromEntries(limited)
})

// 格式化价格
const formatPrice = (price) => {
  if (!price) return '0'
  return price.toLocaleString()
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 处理图片加载错误
const handleImageError = (event) => {
  baseHandleImageError(event, 'assetCard')
}

// 点赞切换
const toggleLike = async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  if (likeLoading.value) return

  try {
    likeLoading.value = true
    const response = await api.post(`/assets/${props.asset.id}/like`)
    
    if (response.data.success) {
      isLiked.value = !isLiked.value
      currentLikeCount.value += isLiked.value ? 1 : -1
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
  } finally {
    likeLoading.value = false
  }
}

// 导航到详情页
const navigateToDetail = () => {
  router.push(`/asset/${props.asset.id}`)
}

// 检查是否已点赞
const checkLikeStatus = () => {
  if (authStore.isAuthenticated && authStore.user) {
    isLiked.value = props.asset.likedBy?.includes(authStore.user.id) || false
  }
}

onMounted(() => {
  checkLikeStatus()
})
</script>

<style scoped>
.line-clamp-1 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.line-clamp-2 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.max-w-16 {
  max-width: 4rem;
}
</style> 