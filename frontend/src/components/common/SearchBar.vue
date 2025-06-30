<template>
  <div class="relative w-full max-w-2xl">
    <!-- 搜索框 -->
    <div class="relative">
      <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
        <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
      </div>
      <input
        ref="searchInput"
        v-model="searchQuery"
        @input="handleInput"
        @focus="showSuggestions = true"
        @keydown.enter="handleSearch"
        @keydown.down.prevent="navigateSuggestions(1)"
        @keydown.up.prevent="navigateSuggestions(-1)"
        @keydown.escape="hideSuggestions"
        type="text"
        :placeholder="placeholder"
        class="w-full pl-12 pr-12 py-4 text-lg border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-white shadow-lg"
      />
      <button
        v-if="searchQuery"
        @click="clearSearch"
        class="absolute inset-y-0 right-12 flex items-center pr-2"
      >
        <XMarkIcon class="h-5 w-5 text-gray-400 hover:text-gray-600" />
      </button>
      <button
        @click="handleSearch"
        class="absolute inset-y-0 right-0 pr-4 flex items-center"
      >
        <div class="bg-blue-600 hover:bg-blue-700 p-2 rounded-lg transition-colors">
          <MagnifyingGlassIcon class="h-5 w-5 text-white" />
        </div>
      </button>
    </div>

    <!-- 搜索建议下拉列表 -->
    <div
      v-show="showSuggestions && suggestions.length > 0"
      class="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-xl shadow-lg z-50 max-h-60 overflow-y-auto"
    >
      <div
        v-for="(suggestion, index) in suggestions"
        :key="index"
        @click="selectSuggestion(suggestion)"
        @mouseenter="selectedIndex = index"
        :class="[
          'px-4 py-3 cursor-pointer transition-colors flex items-center space-x-3',
          selectedIndex === index ? 'bg-blue-50 text-blue-600' : 'hover:bg-gray-50'
        ]"
      >
        <MagnifyingGlassIcon class="h-4 w-4 text-gray-400" />
        <span>{{ suggestion }}</span>
      </div>
    </div>

    <!-- 遮罩层，点击关闭建议 -->
    <div
      v-show="showSuggestions"
      @click="hideSuggestions"
      class="fixed inset-0 z-40"
    ></div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { MagnifyingGlassIcon, XMarkIcon } from '@heroicons/vue/24/outline'
import api from '@/services/api'

const props = defineProps({
  placeholder: {
    type: String,
    default: '搜索农业数据资产...'
  },
  initialValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['search'])

const searchInput = ref(null)
const searchQuery = ref(props.initialValue)
const suggestions = ref([])
const showSuggestions = ref(false)
const selectedIndex = ref(-1)
const debounceTimer = ref(null)

// 监听初始值变化
watch(() => props.initialValue, (newValue) => {
  searchQuery.value = newValue
})

// 处理输入事件
const handleInput = () => {
  if (debounceTimer.value) {
    clearTimeout(debounceTimer.value)
  }
  
  debounceTimer.value = setTimeout(() => {
    if (searchQuery.value.trim()) {
      fetchSuggestions()
    } else {
      suggestions.value = []
      showSuggestions.value = false
    }
  }, 300)
}

// 获取搜索建议
const fetchSuggestions = async () => {
  try {
    const response = await api.get(`/search/suggest?q=${encodeURIComponent(searchQuery.value.trim())}&limit=5`)
    if (response.data.success) {
      suggestions.value = response.data.data
      showSuggestions.value = suggestions.value.length > 0
      selectedIndex.value = -1
    }
  } catch (error) {
    console.error('获取搜索建议失败:', error)
  }
}

// 执行搜索
const handleSearch = () => {
  const query = searchQuery.value.trim()
  if (query) {
    emit('search', query)
    hideSuggestions()
  }
}

// 选择建议项
const selectSuggestion = (suggestion) => {
  searchQuery.value = suggestion
  emit('search', suggestion)
  hideSuggestions()
}

// 键盘导航建议列表
const navigateSuggestions = (direction) => {
  if (suggestions.value.length === 0) return
  
  const newIndex = selectedIndex.value + direction
  if (newIndex >= 0 && newIndex < suggestions.value.length) {
    selectedIndex.value = newIndex
  } else if (newIndex < 0) {
    selectedIndex.value = suggestions.value.length - 1
  } else {
    selectedIndex.value = 0
  }
  
  // 如果按回车键，选择当前高亮项
  nextTick(() => {
    if (selectedIndex.value >= 0) {
      const suggestion = suggestions.value[selectedIndex.value]
      // 可以在这里添加预览功能
    }
  })
}

// 隐藏建议列表
const hideSuggestions = () => {
  showSuggestions.value = false
  selectedIndex.value = -1
}

// 清空搜索
const clearSearch = () => {
  searchQuery.value = ''
  suggestions.value = []
  showSuggestions.value = false
  searchInput.value?.focus()
}

// 聚焦搜索框
const focus = () => {
  nextTick(() => {
    searchInput.value?.focus()
  })
}

// 暴露方法给父组件
defineExpose({
  focus,
  clearSearch
})
</script> 