<template>
  <div class="min-h-screen bg-gray-50 pt-16">
    <!-- 搜索区域 -->
    <section class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div 
          class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6"
          v-motion
          :initial="{ opacity: 0, y: -20 }"
          :enter="{ opacity: 1, y: 0, transition: { duration: 600 } }"
        >
          <!-- 搜索框 -->
          <div class="flex-1 max-w-2xl">
            <SearchBar 
              ref="searchBarRef"
              :initial-value="searchQuery"
              @search="handleSearch"
              placeholder="搜索农业数据资产..."
            />
          </div>

          <!-- 搜索选项 -->
          <div class="flex items-center space-x-4">
            <!-- 排序选择 -->
            <div class="relative">
              <select 
                v-model="sortBy"
                @change="handleSortChange"
                class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-10 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
              >
                <option value="likes">按点赞排序</option>
                <option value="price_asc">价格从低到高</option>
                <option value="price_desc">价格从高到低</option>
                <option value="newest">最新发布</option>
              </select>
              <ChevronDownIcon class="absolute right-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            </div>

            <!-- 筛选按钮 -->
            <button 
              @click="showFilters = !showFilters"
              class="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              <FunnelIcon class="w-4 h-4" />
              <span>筛选</span>
            </button>
          </div>
        </div>

        <!-- 筛选面板 -->
        <div v-show="showFilters" class="mt-6 p-4 bg-gray-50 rounded-lg border">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <!-- 价格范围 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">价格范围</label>
              <div class="flex space-x-2">
                <input 
                  v-model="filters.minPrice"
                  type="number"
                  placeholder="最低价"
                  class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                />
                <span class="flex items-center text-gray-500">-</span>
                <input 
                  v-model="filters.maxPrice"
                  type="number"
                  placeholder="最高价"
                  class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                />
              </div>
            </div>

            <!-- 质量评级 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">质量评级</label>
              <div class="flex flex-wrap gap-2">
                <button 
                  v-for="rating in ['A+', 'A', 'B+', 'B']"
                  :key="rating"
                  @click="toggleFilter('qualityRating', rating)"
                  :class="[
                    'px-3 py-1 rounded-full text-sm border transition-colors',
                    filters.qualityRating.includes(rating) 
                      ? 'bg-blue-100 border-blue-300 text-blue-700' 
                      : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'
                  ]"
                >
                  {{ rating }}
                </button>
              </div>
            </div>

            <!-- 应用筛选按钮 -->
            <div class="flex items-end">
              <button 
                @click="applyFilters"
                class="w-full btn-primary"
              >
                应用筛选
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 搜索结果 -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 结果统计 -->
      <div 
        v-if="!loading"
        class="flex items-center justify-between mb-6"
        v-motion
        :initial="{ opacity: 0 }"
        :enter="{ opacity: 1, transition: { duration: 600, delay: 200 } }"
      >
        <p class="text-gray-600">
          <span v-if="searchQuery">搜索 "{{ searchQuery }}" 的结果：</span>
          <span class="font-medium">{{ totalResults }}</span> 个农业数据资产
        </p>
        <button 
          v-if="searchQuery"
          @click="clearSearch"
          class="text-blue-600 hover:text-blue-500 text-sm"
        >
          清除搜索
        </button>
      </div>

      <!-- 资产网格 -->
      <div v-if="!loading && assets.length > 0">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <AssetCard 
            v-for="(asset, index) in assets" 
            :key="asset.id"
            :asset="asset"
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
          />
        </div>

        <!-- 分页 -->
        <div v-if="totalPages > 1" class="mt-12 flex justify-center">
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
      <div v-else-if="!loading && assets.length === 0" class="text-center py-12">
        <div
          v-motion
          :initial="{ opacity: 0, y: 20 }"
          :enter="{ opacity: 1, y: 0, transition: { duration: 600 } }"
        >
          <MagnifyingGlassIcon class="mx-auto h-12 w-12 text-gray-400 mb-4" />
          <h3 class="text-lg font-medium text-gray-900 mb-2">
            {{ searchQuery ? '未找到相关结果' : '开始搜索农业数据资产' }}
          </h3>
          <p class="text-gray-500 mb-6">
            {{ searchQuery ? '试试其他关键词或调整筛选条件' : '输入关键词搜索您需要的农业数据' }}
          </p>
          <button 
            v-if="searchQuery"
            @click="clearSearch"
            class="btn-primary"
          >
            清除搜索条件
          </button>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div 
          v-for="i in 12" 
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
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SearchBar from '@/components/common/SearchBar.vue'
import AssetCard from '@/components/common/AssetCard.vue'
import { ChevronDownIcon, FunnelIcon, MagnifyingGlassIcon } from '@heroicons/vue/24/outline'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()
const searchBarRef = ref(null)

const loading = ref(false)
const showFilters = ref(false)
const searchQuery = ref('')
const sortBy = ref('likes')
const currentPage = ref(0)
const totalPages = ref(0)
const totalResults = ref(0)
const assets = ref([])

const filters = reactive({
  minPrice: '',
  maxPrice: '',
  qualityRating: []
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

// 搜索资产
const searchAssets = async (page = 0) => {
  try {
    loading.value = true
    
    const params = new URLSearchParams()
    if (searchQuery.value) params.append('keyword', searchQuery.value)
    params.append('page', page.toString())
    params.append('size', '12')
    params.append('sortBy', sortBy.value)
    
    const response = await api.get('/assets/search?' + params.toString())
    
    if (response.data.success) {
      const data = response.data.data
      assets.value = data.content
      currentPage.value = data.number
      totalPages.value = data.totalPages
      totalResults.value = data.totalElements
    }
  } catch (error) {
    console.error('搜索失败:', error)
    assets.value = []
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = (keyword) => {
  searchQuery.value = keyword
  currentPage.value = 0
  
  // 更新URL
  router.push({ 
    name: 'search', 
    query: { 
      ...route.query,
      q: keyword,
      page: undefined 
    } 
  })
  
  searchAssets(0)
}

// 处理排序变化
const handleSortChange = () => {
  currentPage.value = 0
  searchAssets(0)
}

// 切换筛选条件
const toggleFilter = (filterType, value) => {
  const filterArray = filters[filterType]
  const index = filterArray.indexOf(value)
  
  if (index > -1) {
    filterArray.splice(index, 1)
  } else {
    filterArray.push(value)
  }
}

// 应用筛选
const applyFilters = () => {
  currentPage.value = 0
  searchAssets(0)
  showFilters.value = false
}

// 清除搜索
const clearSearch = () => {
  searchQuery.value = ''
  filters.minPrice = ''
  filters.maxPrice = ''
  filters.qualityRating = []
  currentPage.value = 0
  
  router.push({ name: 'search' })
  searchAssets(0)
}

// 跳转页面
const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    searchAssets(page)
    
    // 滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 监听路由查询参数
watch(() => route.query, (newQuery) => {
  if (newQuery.q !== searchQuery.value) {
    searchQuery.value = newQuery.q || ''
  }
}, { immediate: true })

onMounted(() => {
  // 如果有搜索参数，执行搜索
  if (searchQuery.value) {
    searchAssets(0)
  } else {
    // 否则显示所有资产
    searchAssets(0)
  }
})
</script> 