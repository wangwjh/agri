<template>
  <div v-if="loading" class="min-h-screen bg-gray-50 pt-16">
    <!-- 加载骨架屏 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="animate-pulse">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
          <div class="h-96 bg-gray-200 rounded-xl"></div>
          <div class="space-y-4">
            <div class="h-8 bg-gray-200 rounded w-3/4"></div>
            <div class="h-4 bg-gray-200 rounded w-1/2"></div>
            <div class="h-20 bg-gray-200 rounded"></div>
            <div class="h-12 bg-gray-200 rounded w-1/3"></div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else-if="asset" class="min-h-screen bg-gray-50 pt-16">
    <!-- 面包屑导航 -->
    <nav class="bg-white border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <ol class="flex items-center space-x-2 text-sm">
          <li>
            <router-link to="/" class="text-blue-600 hover:text-blue-500">首页</router-link>
          </li>
          <ChevronRightIcon class="w-4 h-4 text-gray-400" />
          <li>
            <router-link to="/search" class="text-blue-600 hover:text-blue-500">搜索</router-link>
          </li>
          <ChevronRightIcon class="w-4 h-4 text-gray-400" />
          <li class="text-gray-500">{{ asset.name }}</li>
        </ol>
      </div>
    </nav>

    <!-- 主要内容 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-12">
        <!-- 资产图片 -->
        <div 
          class="space-y-4"
          v-motion
          :initial="{ opacity: 0, x: -30 }"
          :enter="{ opacity: 1, x: 0, transition: { duration: 600 } }"
        >
          <div class="aspect-square overflow-hidden rounded-xl shadow-lg">
            <img 
              :src="assetImage" 
              :alt="asset.name"
              class="w-full h-full object-cover"
              @error="handleImageError"
            />
          </div>
          
          <!-- 缩略图 -->
          <div class="flex space-x-2">
            <div 
              v-for="i in 4" 
              :key="i"
              class="w-20 h-20 bg-gray-200 rounded-lg overflow-hidden opacity-50 hover:opacity-100 cursor-pointer transition-opacity"
            >
              <img 
                :src="assetImage" 
                :alt="`${asset.name} ${i}`"
                class="w-full h-full object-cover"
              />
            </div>
          </div>
        </div>

        <!-- 资产信息 -->
        <div 
          class="space-y-6"
          v-motion
          :initial="{ opacity: 0, x: 30 }"
          :enter="{ opacity: 1, x: 0, transition: { duration: 600, delay: 200 } }"
        >
          <!-- 标题和价格 -->
          <div>
            <div class="flex items-center gap-3 mb-2">
              <h1 class="text-3xl font-bold text-gray-900">{{ asset.name }}</h1>
              <span :class="qualityBadgeClass" class="px-2 py-1 rounded-full text-sm font-medium">
                {{ asset.qualityRating }}
              </span>
            </div>
            <div class="flex items-center gap-4 text-sm text-gray-600 mb-4">
              <span class="flex items-center">
                <HeartIcon class="w-4 h-4 mr-1" />
                {{ asset.likes }} 点赞
              </span>
              <span class="flex items-center">
                <EyeIcon class="w-4 h-4 mr-1" />
                {{ asset.views || 128 }} 查看
              </span>
              <span>发布时间：{{ formatDate(asset.createdAt) }}</span>
            </div>
            <div class="text-4xl font-bold text-blue-600 mb-4">
              ¥{{ asset.price.toLocaleString() }}
            </div>
          </div>

          <!-- 描述 -->
          <div>
            <h3 class="text-lg font-semibold mb-2">数据描述</h3>
            <p class="text-gray-700 leading-relaxed">
              {{ asset.description || `这是关于${asset.name}的完整种植数据集，包含从播种到收获的全过程监测数据，为您的农业生产提供科学参考。` }}
            </p>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-4">
            <button 
              @click="buyAsset"
              :disabled="purchasing"
              class="flex-1 btn-primary py-4 text-lg font-semibold relative"
            >
              <span v-if="!purchasing">立即购买</span>
              <span v-else class="flex items-center justify-center">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                处理中...
              </span>
            </button>
            
            <button 
              @click="toggleLike"
              :disabled="likeLoading"
              :class="[
                'px-6 py-4 rounded-xl border-2 transition-all font-semibold',
                asset.isLiked 
                  ? 'bg-red-50 border-red-200 text-red-700 hover:bg-red-100' 
                  : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'
              ]"
            >
              <HeartIcon 
                :class="[
                  'w-5 h-5 inline mr-2',
                  asset.isLiked ? 'fill-current' : ''
                ]" 
              />
              {{ asset.isLiked ? '已点赞' : '点赞' }}
            </button>
          </div>

          <!-- 卖家信息 -->
          <div class="bg-gray-50 rounded-xl p-4">
            <h3 class="font-semibold mb-2">卖家信息</h3>
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                <span class="text-white font-medium">
                  {{ asset.sellerName?.charAt(0) || 'S' }}
                </span>
              </div>
              <div>
                <p class="font-medium">{{ asset.sellerName || '农户甲' }}</p>
                <p class="text-sm text-gray-600">信誉良好 · 已认证</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细信息标签页 -->
      <div class="bg-white rounded-xl shadow-lg border border-gray-100 overflow-hidden mb-8">
        <div class="border-b border-gray-200">
          <nav class="flex">
            <button 
              v-for="tab in tabs"
              :key="tab.key"
              @click="activeTab = tab.key"
              :class="[
                'px-6 py-4 text-sm font-medium border-b-2 transition-colors',
                activeTab === tab.key 
                  ? 'border-blue-500 text-blue-600' 
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              ]"
            >
              {{ tab.label }}
            </button>
          </nav>
        </div>

        <div class="p-6">
          <!-- 数据指标 -->
          <div v-if="activeTab === 'metrics'">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div 
                v-for="(value, key) in asset.environmentalData"
                :key="key"
                class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-4"
              >
                <h4 class="font-semibold text-gray-900 mb-2">{{ getMetricLabel(key) }}</h4>
                <p class="text-2xl font-bold text-blue-600">{{ value }}</p>
              </div>
            </div>
          </div>

          <!-- 种植技术 -->
          <div v-else-if="activeTab === 'technology'">
            <div class="space-y-4">
              <div 
                v-for="(tech, index) in asset.cultivationTechnology"
                :key="index"
                class="bg-gray-50 rounded-lg p-4"
              >
                <h4 class="font-semibold text-gray-900 mb-2">{{ tech.stage || `阶段 ${index + 1}` }}</h4>
                <p class="text-gray-700">{{ tech.description || tech }}</p>
              </div>
            </div>
          </div>

          <!-- 产量质量 -->
          <div v-else-if="activeTab === 'quality'">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div 
                v-for="(value, key) in asset.yieldQuality"
                :key="key"
                class="bg-gradient-to-br from-green-50 to-green-100 rounded-lg p-4"
              >
                <h4 class="font-semibold text-gray-900 mb-2">{{ getQualityLabel(key) }}</h4>
                <p class="text-2xl font-bold text-green-600">{{ value }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 推荐相关资产 -->
      <section>
        <h2 class="text-2xl font-bold text-gray-900 mb-6">相关推荐</h2>
        <div v-if="relatedAssets.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <AssetCard 
            v-for="(relatedAsset, index) in relatedAssets" 
            :key="relatedAsset.id"
            :asset="relatedAsset"
            v-motion
            :initial="{ opacity: 0, y: 30 }"
            :enter="{ 
              opacity: 1, 
              y: 0, 
              transition: { 
                duration: 600, 
                delay: index * 100 
              } 
            }"
          />
        </div>
        <div v-else class="text-center py-8 text-gray-500">
          暂无相关推荐
        </div>
      </section>
    </div>

    <!-- 购买确认模态框 -->
    <div v-if="showPurchaseModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 transition-opacity" @click="showPurchaseModal = false">
          <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
        </div>

        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div>
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-blue-100">
              <CreditCardIcon class="h-6 w-6 text-blue-600" />
            </div>
            <div class="mt-3 text-center sm:mt-5">
              <h3 class="text-lg leading-6 font-medium text-gray-900">
                确认购买
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500 mb-4">
                  您将购买 <span class="font-medium">{{ asset.name }}</span>
                </p>
                
                <!-- 费用明细 -->
                <div class="bg-gray-50 rounded-lg p-4 text-left">
                  <div class="space-y-2 text-sm">
                    <div class="flex justify-between">
                      <span class="text-gray-600">商品价格：</span>
                      <span class="font-medium">¥{{ asset.price.toLocaleString() }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-gray-600">手续费 (5%)：</span>
                      <span class="font-medium">¥{{ (asset.price * 0.05).toLocaleString() }}</span>
                    </div>
                    <div class="border-t border-gray-200 pt-2 mt-2">
                      <div class="flex justify-between">
                        <span class="font-medium text-gray-900">总计：</span>
                        <span class="font-bold text-blue-600 text-lg">¥{{ (asset.price * 1.05).toLocaleString() }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense">
            <button
              @click="confirmPurchase"
              :disabled="purchasing"
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:col-start-2 sm:text-sm disabled:opacity-50"
            >
              确认购买
            </button>
            <button
              @click="showPurchaseModal = false"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:col-start-1 sm:text-sm"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 余额不足模态框 -->
    <div v-if="showInsufficientBalanceModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="showInsufficientBalanceModal = false"></div>
        
        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div>
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100">
              <ExclamationTriangleIcon class="h-6 w-6 text-red-600" />
            </div>
            <div class="mt-3 text-center sm:mt-5">
              <h3 class="text-lg leading-6 font-medium text-gray-900">
                余额不足
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500 mb-4">
                  您的账户余额不足以购买此数据资产
                </p>
                
                <div v-if="purchaseCheckResult" class="bg-gray-50 rounded-lg p-4 text-left">
                  <div class="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <span class="text-gray-600">商品价格：</span>
                      <span class="font-medium">¥{{ asset.price.toLocaleString() }}</span>
                    </div>
                    <div>
                      <span class="text-gray-600">手续费：</span>
                      <span class="font-medium">¥{{ (asset.price * 0.05).toLocaleString() }}</span>
                    </div>
                    <div>
                      <span class="text-gray-600">总计：</span>
                      <span class="font-medium text-blue-600">¥{{ purchaseCheckResult.requiredAmount.toLocaleString() }}</span>
                    </div>
                    <div>
                      <span class="text-gray-600">当前余额：</span>
                      <span class="font-medium">¥{{ purchaseCheckResult.currentBalance.toLocaleString() }}</span>
                    </div>
                  </div>
                  <div class="mt-3 pt-3 border-t border-gray-200">
                    <div class="flex justify-between items-center">
                      <span class="text-gray-600">还需充值：</span>
                      <span class="font-bold text-red-600">¥{{ purchaseCheckResult.shortfall.toLocaleString() }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense">
            <button
              @click="goToRecharge"
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:col-start-2 sm:text-sm"
            >
              立即充值
            </button>
            <button
              @click="showInsufficientBalanceModal = false"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:col-start-1 sm:text-sm"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 404页面 -->
  <div v-else class="min-h-screen bg-gray-50 pt-16 flex items-center justify-center">
    <div class="text-center">
      <h1 class="text-4xl font-bold text-gray-900 mb-4">资产未找到</h1>
      <p class="text-gray-600 mb-6">抱歉，您访问的资产不存在或已被删除</p>
      <router-link to="/" class="btn-primary">
        返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AssetCard from '@/components/common/AssetCard.vue'
import { 
  ChevronRightIcon,
  HeartIcon, 
  EyeIcon,
  CreditCardIcon,
  ExclamationTriangleIcon
} from '@heroicons/vue/24/outline'
import api from '@/services/api'
import { placeholders, defaultImages, handleImageError as baseHandleImageError } from '@/utils/imageUtils'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const asset = ref(null)
const relatedAssets = ref([])
const activeTab = ref('metrics')
const showPurchaseModal = ref(false)
const purchasing = ref(false)
const showInsufficientBalanceModal = ref(false)
const likeLoading = ref(false)
const userBalance = ref(null)
const purchaseCheckResult = ref(null)

const tabs = [
  { key: 'metrics', label: '环境数据' },
  { key: 'technology', label: '种植技术' },
  { key: 'quality', label: '产量质量' }
]

// 计算属性
const assetImage = computed(() => {
  if (asset.value?.imageUrls && asset.value.imageUrls.length > 0) {
    return asset.value.imageUrls[0]
  }
  return defaultImages.farmland
})

const qualityBadgeClass = computed(() => {
  if (!asset.value) return ''
  
  const rating = asset.value.qualityRating
  const baseClass = 'px-2 py-1 rounded-full text-xs font-medium'
  
  switch (rating) {
    case 'A+':
      return `${baseClass} bg-green-100 text-green-800`
    case 'A':
      return `${baseClass} bg-blue-100 text-blue-800`
    case 'B+':
      return `${baseClass} bg-yellow-100 text-yellow-800`
    case 'B':
      return `${baseClass} bg-orange-100 text-orange-800`
    default:
      return `${baseClass} bg-gray-100 text-gray-800`
  }
})

// 获取资产详情
const fetchAssetDetail = async () => {
  try {
    loading.value = true
    const response = await api.get(`/assets/${route.params.id}`)
    
    if (response.data.success) {
      asset.value = response.data.data
      // 获取相关推荐
      fetchRelatedAssets()
    }
  } catch (error) {
    console.error('获取资产详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取相关推荐
const fetchRelatedAssets = async () => {
  try {
    const response = await api.get('/assets/popular', { params: { size: 4 } })
    if (response.data.success) {
      relatedAssets.value = response.data.data.content.filter(item => item.id !== asset.value.id)
    }
  } catch (error) {
    console.error('获取相关推荐失败:', error)
  }
}

// 检查购买能力
const checkPurchaseAbility = async () => {
  try {
    const response = await api.post('/account/check-purchase', {
      amount: asset.value.price + (asset.value.price * 0.05) // 价格 + 5% 手续费
    })
    
    if (response.data.success) {
      purchaseCheckResult.value = response.data.data
      return response.data.data.canPurchase
    }
    return false
  } catch (error) {
    console.error('检查购买能力失败:', error)
    return false
  }
}

// 购买资产
const buyAsset = async () => {
  if (!authStore.isAuthenticated) {
    router.push({
      name: 'login',
      query: { redirect: route.fullPath }
    })
    return
  }

  // 检查购买能力
  const canPurchase = await checkPurchaseAbility()
  if (!canPurchase) {
    // 余额不足，显示充值提示
    showInsufficientBalanceModal.value = true
    return
  }

  showPurchaseModal.value = true
}

// 确认购买
const confirmPurchase = async () => {
  try {
    purchasing.value = true
    showPurchaseModal.value = false

    const response = await api.post(`/transactions/buy/${asset.value.id}`)
    
    if (response.data.success) {
      // 购买成功，显示成功消息并询问是否查看交易记录
      const viewTransaction = confirm('🎉 购买成功！\n\n您已成功购买该农业数据资产。\n是否立即查看交易记录？')
      
      if (viewTransaction) {
        router.push('/transactions')
      } else {
        // 刷新资产状态
        await fetchAssetDetail()
      }
    } else {
      // 服务器返回错误
      alert('❌ 购买失败\n\n' + (response.data.message || '未知错误'))
    }
  } catch (error) {
    console.error('购买失败:', error)
    
    // 根据错误类型显示不同的提示信息
    let errorMessage = '❌ 购买失败\n\n'
    
    if (error.response?.status === 401) {
      errorMessage += '用户身份验证失败，请重新登录'
      // 跳转到登录页面
      router.push({
        name: 'login',
        query: { redirect: route.fullPath }
      })
      return
    } else if (error.response?.status === 400) {
      errorMessage += error.response.data?.message || '请求参数错误'
    } else if (error.response?.status === 500) {
      errorMessage += '服务器内部错误，请稍后重试'
    } else if (error.code === 'NETWORK_ERROR') {
      errorMessage += '网络连接失败，请检查网络连接'
    } else {
      errorMessage += error.response?.data?.message || error.message || '未知错误'
    }
    
    alert(errorMessage)
    
    // 如果是余额不足，建议用户充值
    if (error.response?.data?.message?.includes('余额不足')) {
      const shouldRecharge = confirm('\n💰 余额不足\n\n是否前往账户页面充值？')
      if (shouldRecharge) {
        router.push('/account')
      }
    }
  } finally {
    purchasing.value = false
  }
}

// 点赞功能
const toggleLike = async () => {
  if (!authStore.isAuthenticated) {
    router.push({
      name: 'login',
      query: { redirect: route.fullPath }
    })
    return
  }
  
  try {
    likeLoading.value = true
    
    const response = await api.post(`/assets/${asset.value.id}/like`)
    
    if (response.data.success) {
      asset.value.isLiked = !asset.value.isLiked
      asset.value.likes += asset.value.isLiked ? 1 : -1
    }
  } catch (error) {
    console.error('点赞失败:', error)
  } finally {
    likeLoading.value = false
  }
}

// 工具函数
const getMetricLabel = (key) => {
  const labels = {
    temperature: '温度 (°C)',
    humidity: '湿度 (%)',
    soilPh: '土壤pH值',
    sunlightHours: '光照时长 (h)',
    rainfall: '降雨量 (mm)'
  }
  return labels[key] || key
}

const getQualityLabel = (key) => {
  const labels = {
    yield: '产量 (kg/亩)',
    quality: '质量等级',
    moistureContent: '含水量 (%)',
    proteinContent: '蛋白质含量 (%)'
  }
  return labels[key] || key
}

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleImageError = (event) => {
  baseHandleImageError(event, 'assetDetail')
}

const goToRecharge = () => {
  showInsufficientBalanceModal.value = false
  router.push('/account')
}

onMounted(() => {
  fetchAssetDetail()
})
</script> 