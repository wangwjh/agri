<template>
  <div class="min-h-screen bg-gray-50 pt-16">
    <!-- 页面标题 -->
    <div class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div 
          class="flex items-center justify-between"
          v-motion
          :initial="{ opacity: 0, y: -20 }"
          :enter="{ opacity: 1, y: 0, transition: { duration: 600 } }"
        >
          <div>
            <h1 class="text-3xl font-bold text-gray-900">{{ pageTitle }}</h1>
            <p class="mt-2 text-gray-600">{{ pageDescription }}</p>
          </div>
          
          <!-- 交易类型标签 -->
          <div class="flex space-x-4 mb-4">
            <button 
              @click="setTransactionType('')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                currentType === '' 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              所有交易
            </button>
            <button 
              @click="setTransactionType('purchased')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                currentType === 'purchased' 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              已购买数据
            </button>
            <button 
              @click="setTransactionType('uploaded')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                currentType === 'uploaded' 
                  ? 'bg-green-600 text-white' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              ]"
            >
              已上传数据
            </button>
          </div>
          
          <!-- 统计信息 -->
          <div class="grid grid-cols-3 gap-6 text-center">
            <div class="bg-blue-50 rounded-lg p-4">
              <div class="text-2xl font-bold text-blue-600">{{ stats.totalTransactions }}</div>
              <div class="text-sm text-gray-600">{{ getStatsLabel('total') }}</div>
            </div>
            <div class="bg-green-50 rounded-lg p-4">
              <div class="text-2xl font-bold text-green-600">¥{{ stats.totalAmount.toLocaleString() }}</div>
              <div class="text-sm text-gray-600">{{ getStatsLabel('amount') }}</div>
            </div>
            <div class="bg-purple-50 rounded-lg p-4">
              <div class="text-2xl font-bold text-purple-600">{{ stats.assetsOwned }}</div>
              <div class="text-sm text-gray-600">{{ getStatsLabel('assets') }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 交易列表 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 筛选器 -->
      <div 
        class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 200 } }"
      >
        <div class="flex flex-col sm:flex-row gap-4">
          <!-- 交易类型筛选 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">交易类型</label>
            <select 
              v-model="filters.type"
              @change="applyFilters"
              class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            >
              <option value="">全部</option>
              <option value="purchase">购买</option>
              <option value="sale">销售</option>
            </select>
          </div>

          <!-- 状态筛选 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">交易状态</label>
            <select 
              v-model="filters.status"
              @change="applyFilters"
              class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            >
              <option value="">全部</option>
              <option value="COMPLETED">已完成</option>
              <option value="PENDING">待处理</option>
              <option value="CANCELLED">已取消</option>
            </select>
          </div>

          <!-- 时间范围 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">时间范围</label>
            <select 
              v-model="filters.timeRange"
              @change="applyFilters"
              class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            >
              <option value="">全部</option>
              <option value="7d">最近7天</option>
              <option value="30d">最近30天</option>
              <option value="90d">最近90天</option>
            </select>
          </div>

          <!-- 搜索框 -->
          <div class="flex-1">
            <label class="block text-sm font-medium text-gray-700 mb-2">搜索</label>
            <div class="relative">
              <input
                v-model="searchQuery"
                @input="handleSearch"
                type="text"
                placeholder="搜索资产名称..."
                class="block w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
              />
              <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            </div>
          </div>
        </div>
      </div>

      <!-- 交易列表 -->
      <div v-if="!loading && transactions.length > 0" class="space-y-4">
        <div 
          v-for="(transaction, index) in transactions" 
          :key="transaction.id"
          class="bg-white rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-all cursor-pointer"
          v-motion
          :initial="{ opacity: 0, y: 30 }"
          :enter="{ 
            opacity: 1, 
            y: 0, 
            transition: { 
              duration: 600, 
              delay: index * 50 
            } 
          }"
          @click="viewTransactionDetail(transaction)"
        >
          <div class="p-6">
            <div class="flex items-center justify-between">
              <!-- 交易信息 -->
              <div class="flex items-center space-x-4">
                <!-- 资产图片 -->
                <div class="w-16 h-16 rounded-lg overflow-hidden bg-gray-100">
                  <img 
                    :src="getAssetImage(transaction.asset)"
                    :alt="transaction.asset?.name"
                    class="w-full h-full object-cover"
                    @error="handleImageError"
                  />
                </div>

                <!-- 基本信息 -->
                <div>
                  <h3 class="text-lg font-semibold text-gray-900">
                    {{ transaction.asset?.name || '未知资产' }}
                  </h3>
                  <div class="flex items-center space-x-3 text-sm text-gray-600 mt-1">
                    <span class="flex items-center">
                      <CalendarIcon class="w-4 h-4 mr-1" />
                      {{ formatDate(transaction.createTime || transaction.createdAt) }}
                    </span>
                    <span class="flex items-center" v-if="currentType !== 'uploaded'">
                      <UserIcon class="w-4 h-4 mr-1" />
                      {{ getOtherParty(transaction) }}
                    </span>
                    <span class="flex items-center" v-else>
                      <UserIcon class="w-4 h-4 mr-1" />
                      {{ transaction.seller?.username || '我的资产' }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 交易详情 -->
              <div class="flex items-center space-x-6">
                <!-- 交易类型 -->
                <div class="text-center">
                  <div :class="getTypeStyle(transaction)" class="px-3 py-1 rounded-full text-sm font-medium">
                    {{ getTypeLabel(transaction) }}
                  </div>
                </div>

                <!-- 交易金额 -->
                <div class="text-right">
                  <div :class="getPriceColor(transaction)" class="text-xl font-bold">
                    {{ getPricePrefix(transaction) }}¥{{ transaction.price.toLocaleString() }}
                  </div>
                  <div class="text-sm text-gray-500">交易金额</div>
                </div>

                <!-- 交易状态 -->
                <div class="text-center">
                  <div :class="getStatusStyle(transaction.status)" class="px-3 py-1 rounded-full text-sm font-medium">
                    {{ getStatusLabel(transaction.status) }}
                  </div>
                </div>

                <!-- 操作按钮 -->
                <div class="flex space-x-2">
                  <button 
                    @click.stop="viewAssetDetail(transaction.asset)"
                    class="p-2 text-gray-400 hover:text-blue-600 transition-colors"
                    title="查看详情"
                  >
                    <EyeIcon class="w-5 h-5" />
                  </button>
                  <button 
                    @click.stop="downloadReceipt(transaction)"
                    class="p-2 text-gray-400 hover:text-green-600 transition-colors"
                    title="下载凭证"
                  >
                    <DocumentArrowDownIcon class="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="totalPages > 1" class="mt-8 flex justify-center">
          <nav class="flex items-center space-x-2">
            <button 
              @click="goToPage(currentPage - 1)"
              :disabled="currentPage === 0"
              class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              上一页
            </button>
            
            <div class="flex space-x-1">
              <button 
                v-for="page in visiblePages"
                :key="page"
                @click="goToPage(page - 1)"
                :class="[
                  'px-3 py-2 text-sm font-medium border rounded-lg',
                  currentPage === page - 1 
                    ? 'bg-blue-600 text-white border-blue-600' 
                    : 'text-gray-700 bg-white border-gray-300 hover:bg-gray-50'
                ]"
              >
                {{ page }}
              </button>
            </div>

            <button 
              @click="goToPage(currentPage + 1)"
              :disabled="currentPage >= totalPages - 1"
              class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              下一页
            </button>
          </nav>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!loading && transactions.length === 0" class="text-center py-12">
        <div
          v-motion
          :initial="{ opacity: 0, y: 20 }"
          :enter="{ opacity: 1, y: 0, transition: { duration: 600 } }"
        >
          <CloudArrowUpIcon class="h-16 w-16 text-gray-300 mx-auto mb-4" />
          <h3 class="text-lg font-medium text-gray-900 mb-2">暂无交易记录</h3>
          <p class="text-gray-500 mb-6">{{ getEmptyStateDescription() }}</p>
          <router-link 
            v-if="currentType === 'uploaded'"
            to="/upload"
            class="btn-primary">
            开始上传
          </router-link>
          <router-link 
            v-else
            to="/search" 
            class="btn-primary">
            开始购买
          </router-link>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="space-y-4">
        <div 
          v-for="i in 5" 
          :key="i"
          class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 animate-pulse"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-4">
              <div class="w-16 h-16 bg-gray-200 rounded-lg"></div>
              <div>
                <div class="h-4 bg-gray-200 rounded w-32 mb-2"></div>
                <div class="h-3 bg-gray-200 rounded w-24"></div>
              </div>
            </div>
            <div class="flex items-center space-x-6">
              <div class="h-6 bg-gray-200 rounded w-16"></div>
              <div class="h-6 bg-gray-200 rounded w-20"></div>
              <div class="h-6 bg-gray-200 rounded w-16"></div>
            </div>
          </div>
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
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  MagnifyingGlassIcon,
  CalendarIcon,
  UserIcon,
  EyeIcon,
  DocumentArrowDownIcon,
  CloudArrowUpIcon
} from '@heroicons/vue/24/outline'
import AssetDetailModal from '@/components/admin/AssetDetailModal.vue'
import api from '@/services/api'
import { placeholders, handleImageError as baseHandleImageError } from '@/utils/imageUtils'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(true)
const transactions = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const searchQuery = ref('')
const currentType = ref('')

// 资产详情模态框
const showAssetModal = ref(false)
const selectedAsset = ref(null)

const stats = reactive({
  totalTransactions: 0,
  totalAmount: 0,
  assetsOwned: 0
})

const filters = reactive({
  type: '',
  status: '',
  timeRange: ''
})

// 计算属性
const pageTitle = computed(() => {
  switch (currentType.value) {
    case 'purchased':
      return '已购买数据'
    case 'uploaded':
      return '已上传数据'
    default:
      return '交易记录'
  }
})

const pageDescription = computed(() => {
  switch (currentType.value) {
    case 'purchased':
      return '查看您购买的农业数据资产'
    case 'uploaded':
      return '管理您上传的农业数据资产'
    default:
      return '查看您的农业数据资产交易历史'
  }
})

// 可见页码
const visiblePages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value + 1
  const range = []
  
  let start = Math.max(1, current - 2)
  let end = Math.min(total, current + 2)
  
  if (end - start < 4) {
    if (start === 1) {
      end = Math.min(total, start + 4)
    } else if (end === total) {
      start = Math.max(1, end - 4)
    }
  }
  
  for (let i = start; i <= end; i++) {
    range.push(i)
  }
  
  return range
})

// 获取交易记录或用户资产
const fetchTransactions = async (page = 0) => {
  try {
    loading.value = true
    
    // 如果是查看已上传数据，则调用资产API
    if (currentType.value === 'uploaded') {
      const response = await api.get('/assets/my')
      
      if (response.data.success) {
        const assets = response.data.data || []
        
        // 将资产数据转换为交易记录格式以便显示
        const convertedAssets = assets.map(asset => ({
          id: asset.id,
          asset: asset,
          type: 'UPLOAD',
          status: getAssetDisplayStatus(asset),
          createTime: asset.createTime,
          price: asset.price,
          // 为了兼容现有显示逻辑，添加这些字段
          seller: {
            username: asset.ownerName
          }
        }))
        
        // 手动分页处理（因为资产API不支持分页）
        const pageSize = 10
        const startIndex = page * pageSize
        const endIndex = startIndex + pageSize
        const paginatedAssets = convertedAssets.slice(startIndex, endIndex)
        
        transactions.value = paginatedAssets
        currentPage.value = page
        totalPages.value = Math.ceil(convertedAssets.length / pageSize)
        
        // 更新统计信息
        updateUploadedStats(assets)
      }
    } else {
      // 原有的交易记录获取逻辑
    const params = new URLSearchParams()
    params.append('page', page.toString())
    params.append('size', '10')
    
    if (searchQuery.value) params.append('search', searchQuery.value)
    if (filters.type) params.append('type', filters.type)
    if (filters.status) params.append('status', filters.status)
    if (filters.timeRange) params.append('timeRange', filters.timeRange)
    if (currentType.value) params.append('transactionType', currentType.value)
    
    const response = await api.get('/transactions/my?' + params.toString())
    
    if (response.data.success) {
      const data = response.data.data
      transactions.value = data.content
      currentPage.value = data.number
      totalPages.value = data.totalPages
      
      // 更新统计信息
      updateStats()
      }
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取资产的显示状态
const getAssetDisplayStatus = (asset) => {
  if (!asset.reviewStatus || !asset.publishStatus) {
    return 'PENDING'
  }
  
  // 根据审核和发布状态确定显示状态
  if (asset.reviewStatus === 'PENDING') {
    return 'PENDING' // 待审核
  } else if (asset.reviewStatus === 'REJECTED') {
    return 'REJECTED' // 审核拒绝
  } else if (asset.reviewStatus === 'APPROVED') {
    if (asset.publishStatus === 'DRAFT') {
      return 'APPROVED' // 审核通过待发布
    } else if (asset.publishStatus === 'PUBLISHED') {
      return 'PUBLISHED' // 已发布
    } else if (asset.publishStatus === 'OFFLINE') {
      return 'OFFLINE' // 已下线
    }
  }
  
  return 'UNKNOWN'
}

// 更新上传资产的统计信息
const updateUploadedStats = (assets) => {
  stats.totalTransactions = assets.length
  stats.totalAmount = assets.filter(asset => 
    asset.publishStatus === 'PUBLISHED'
  ).reduce((sum, asset) => sum + (asset.price || 0), 0)
  stats.assetsOwned = assets.length
}

// 更新统计信息
const updateStats = () => {
  stats.totalTransactions = transactions.value.length
  stats.totalAmount = transactions.value.reduce((sum, t) => sum + t.price, 0)
  stats.assetsOwned = new Set(transactions.value.map(t => t.asset?.id)).size
}

// 搜索处理
const handleSearch = () => {
  const timeoutId = setTimeout(() => {
    currentPage.value = 0
    fetchTransactions(0)
  }, 300)
  
  return () => clearTimeout(timeoutId)
}

// 应用筛选
const applyFilters = () => {
  currentPage.value = 0
  fetchTransactions(0)
}

// 跳转页面
const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchTransactions(page)
  }
}

// 查看交易详情
const viewTransactionDetail = (transaction) => {
  // 这里可以打开详情模态框或跳转到详情页
  console.log('查看交易详情:', transaction)
}

// 查看资产详情
const viewAssetDetail = (assetId) => {
  // 如果直接传入了资产对象，使用它
  if (typeof assetId === 'object') {
    selectedAsset.value = assetId
    showAssetModal.value = true
    return
  }
  
  // 否则尝试从当前交易记录中找到对应的资产
  const transaction = transactions.value.find(t => t.asset?.id === assetId)
  if (transaction?.asset) {
    selectedAsset.value = transaction.asset
    showAssetModal.value = true
  } else if (assetId) {
    // 如果找不到，跳转到前台详情页
    router.push({ name: 'asset-detail', params: { id: assetId } })
  }
}

// 下载凭证
const downloadReceipt = (transaction) => {
  // 这里实现下载功能
  console.log('下载凭证:', transaction)
}

// 设置交易类型
const setTransactionType = (type) => {
  currentType.value = type
  currentPage.value = 0
  
  // 更新URL
  const query = { ...route.query }
  if (type) {
    query.type = type
  } else {
    delete query.type
  }
  
  router.push({ query })
  fetchTransactions(0)
}

// 获取统计标签
const getStatsLabel = (type) => {
  const baseLabels = {
    'total': '总数量',
    'amount': '总金额',
    'assets': '资产数'
  }
  
  switch (currentType.value) {
    case 'purchased':
      return {
        'total': '购买数量',
        'amount': '购买金额',
        'assets': '拥有资产'
      }[type] || baseLabels[type]
    case 'uploaded':
      return {
        'total': '上传数量',
        'amount': '销售金额',
        'assets': '上传资产'
      }[type] || baseLabels[type]
    default:
      return {
        'total': '总交易数',
        'amount': '交易总额',
        'assets': '涉及资产'
      }[type] || baseLabels[type]
  }
}

// 工具函数
const getAssetImage = (asset) => {
  if (!asset) return placeholders.transactionIcon
  if (asset.imageUrls && asset.imageUrls.length > 0) {
    return asset.imageUrls[0]
  }
  return placeholders.transactionIcon
}

const getOtherParty = (transaction) => {
  // 根据当前用户判断对方是买家还是卖家
  const currentUsername = authStore.user?.username
  if (transaction.buyer?.username === currentUsername) {
    return transaction.seller?.username || '未知卖家'
  } else {
    return transaction.buyer?.username || '未知买家'
  }
}

const getTypeLabel = (transaction) => {
  // 如果是上传资产页面，显示"上传"
  if (currentType.value === 'uploaded' || transaction.type === 'UPLOAD') {
    return '上传'
  }
  
  // 原有的交易类型判断逻辑
  const currentUsername = authStore.user?.username
  return transaction.buyer?.username === currentUsername ? '购买' : '销售'
}

const getTypeStyle = (transaction) => {
  // 如果是上传资产页面，使用绿色样式
  if (currentType.value === 'uploaded' || transaction.type === 'UPLOAD') {
    return 'bg-green-100 text-green-700'
  }
  
  // 原有的交易类型样式逻辑
  const currentUsername = authStore.user?.username
  const isPurchase = transaction.buyer?.username === currentUsername
  
  return isPurchase 
    ? 'bg-blue-100 text-blue-700' 
    : 'bg-green-100 text-green-700'
}

const getPriceColor = (transaction) => {
  // 如果是上传资产页面，显示为绿色（收入）
  if (currentType.value === 'uploaded' || transaction.type === 'UPLOAD') {
    return 'text-green-600'
  }
  
  // 原有的交易价格颜色逻辑
  const currentUsername = authStore.user?.username
  const isPurchase = transaction.buyer?.username === currentUsername
  
  return isPurchase ? 'text-red-600' : 'text-green-600'
}

const getPricePrefix = (transaction) => {
  // 如果是上传资产页面，不显示前缀
  if (currentType.value === 'uploaded' || transaction.type === 'UPLOAD') {
    return ''
  }
  
  // 原有的交易价格前缀逻辑
  const currentUsername = authStore.user?.username
  const isPurchase = transaction.buyer?.username === currentUsername
  
  return isPurchase ? '-' : '+'
}

const getStatusLabel = (status) => {
  // 交易状态
  const transactionLabels = {
    'COMPLETED': '已完成',
    'PENDING': '待处理',
    'CANCELLED': '已取消'
  }
  
  // 资产状态
  const assetLabels = {
    'PENDING': '待审核',
    'APPROVED': '审核通过',
    'REJECTED': '审核拒绝',
    'PUBLISHED': '已发布',
    'OFFLINE': '已下线',
    'UNKNOWN': '状态未知'
  }
  
  return assetLabels[status] || transactionLabels[status] || status
}

const getStatusStyle = (status) => {
  // 交易状态样式
  const transactionStyles = {
    'COMPLETED': 'bg-green-100 text-green-700',
    'PENDING': 'bg-yellow-100 text-yellow-700',
    'CANCELLED': 'bg-red-100 text-red-700'
  }
  
  // 资产状态样式
  const assetStyles = {
    'PENDING': 'bg-yellow-100 text-yellow-700',
    'APPROVED': 'bg-blue-100 text-blue-700',
    'REJECTED': 'bg-red-100 text-red-700',
    'PUBLISHED': 'bg-green-100 text-green-700',
    'OFFLINE': 'bg-gray-100 text-gray-700',
    'UNKNOWN': 'bg-gray-100 text-gray-700'
  }
  
  return assetStyles[status] || transactionStyles[status] || 'bg-gray-100 text-gray-700'
}

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleImageError = (event) => {
  baseHandleImageError(event, 'transactionIcon')
}

const getEmptyStateDescription = () => {
  switch (currentType.value) {
    case 'purchased':
      return '您还没有购买过任何数据资产'
    case 'uploaded':
      return '您还没有上传过任何数据资产'
    default:
      return '您还没有进行过任何交易'
  }
}

// 关闭资产详情模态框
const closeAssetModal = () => {
  showAssetModal.value = false
  selectedAsset.value = null
}

// 监听路由参数变化
watch(() => route.query.type, (newType) => {
  currentType.value = newType || ''
}, { immediate: true })

onMounted(() => {
  // 从URL参数设置初始类型
  currentType.value = route.query.type || ''
  fetchTransactions()
})
</script> 