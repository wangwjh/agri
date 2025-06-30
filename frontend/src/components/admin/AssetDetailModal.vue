<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 transition-opacity" @click="closeModal">
        <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
      </div>

      <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full">
        <!-- 模态框头部 -->
        <div class="bg-white px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h3 class="text-lg leading-6 font-medium text-gray-900">
              数据资产详情
            </h3>
            <button
              @click="closeModal"
              class="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>
        </div>

        <!-- 模态框内容 -->
        <div class="bg-white px-6 py-4 max-h-96 overflow-y-auto">
          <div v-if="asset" class="space-y-6">
            <!-- 基本信息 -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- 资产图片 -->
              <div>
                <h4 class="text-sm font-medium text-gray-700 mb-2">资产图片</h4>
                <div class="aspect-video overflow-hidden rounded-lg bg-gray-100">
                  <img 
                    :src="assetImage" 
                    :alt="asset.name"
                    class="w-full h-full object-cover"
                    @error="handleImageError"
                  />
                </div>
              </div>

              <!-- 基本信息 -->
              <div class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700">资产名称</label>
                  <p class="mt-1 text-sm text-gray-900">{{ asset.name }}</p>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700">价格</label>
                  <p class="mt-1 text-lg font-semibold text-blue-600">¥{{ asset.price?.toLocaleString() }}</p>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700">所有者</label>
                  <p class="mt-1 text-sm text-gray-900">{{ asset.ownerName }}</p>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700">质量评级</label>
                  <span :class="qualityBadgeClass" class="inline-flex px-2 py-1 rounded-full text-xs font-medium mt-1">
                    {{ asset.qualityRating }}
                  </span>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700">分类</label>
                  <p class="mt-1 text-sm text-gray-900">{{ asset.category || '未分类' }}</p>
                </div>
              </div>
            </div>

            <!-- 详细描述 -->
            <div>
              <h4 class="text-sm font-medium text-gray-700 mb-2">详细描述</h4>
              <p class="text-sm text-gray-900 bg-gray-50 p-3 rounded-lg">
                {{ asset.description || '暂无描述' }}
              </p>
            </div>

            <!-- 环境数据 -->
            <div v-if="asset.metrics" class="grid grid-cols-2 gap-4 text-sm">
              <div v-for="(value, key) in asset.metrics" :key="key" class="flex justify-between">
                <span class="font-medium text-gray-700">{{ getMetricLabel(key) }}:</span>
                <span class="text-gray-900">{{ formatMetricValue(key, value) }}</span>
              </div>
            </div>

            <!-- 附件信息 -->
            <div v-if="asset.attachedFiles && asset.attachedFiles.length > 0">
              <h4 class="text-sm font-medium text-gray-700 mb-3 flex items-center">
                <DocumentArrowDownIcon class="w-4 h-4 mr-2" />
                附件文件
              </h4>
              <div class="space-y-2">
                <div 
                  v-for="(file, index) in asset.attachedFiles"
                  :key="index"
                  class="flex items-center justify-between bg-gray-50 rounded-lg p-3"
                >
                  <div class="flex items-center space-x-3">
                    <div class="w-8 h-8 rounded-lg flex items-center justify-center"
                         :class="getFileTypeStyle(file.name)">
                      <PhotoIcon v-if="isImageFile(file.name)" class="w-4 h-4 text-blue-600" />
                      <VideoCameraIcon v-else-if="isVideoFile(file.name)" class="w-4 h-4 text-green-600" />
                      <MicrophoneIcon v-else-if="isAudioFile(file.name)" class="w-4 h-4 text-pink-600" />
                      <DocumentIcon v-else class="w-4 h-4 text-gray-600" />
                    </div>
                    <div>
                      <div class="text-sm font-medium text-gray-900">{{ file.name }}</div>
                      <div class="text-xs text-gray-500">
                        {{ formatFileSize(file.size) }} • {{ getFileTypeLabel(file.name) }}
                      </div>
                    </div>
                  </div>
                  <button
                    @click="downloadFile(file)"
                    class="p-2 text-blue-600 hover:text-blue-800 transition-colors"
                    title="下载文件"
                  >
                    <ArrowDownTrayIcon class="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>

            <!-- 状态信息 -->
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">审核状态</label>
                <span :class="getReviewStatusStyle(asset.reviewStatus)" class="inline-flex px-2 py-1 rounded-full text-xs font-medium mt-1">
                  {{ getReviewStatusLabel(asset.reviewStatus) }}
                </span>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700">发布状态</label>
                <span :class="getPublishStatusStyle(asset.publishStatus)" class="inline-flex px-2 py-1 rounded-full text-xs font-medium mt-1">
                  {{ getPublishStatusLabel(asset.publishStatus) }}
                </span>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700">创建时间</label>
                <p class="mt-1 text-sm text-gray-900">{{ formatDate(asset.createTime) }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700">更新时间</label>
                <p class="mt-1 text-sm text-gray-900">{{ formatDate(asset.updateTime) }}</p>
              </div>
            </div>

            <!-- 技术信息 -->
            <div v-if="asset.cultivationTechnique">
              <h4 class="text-sm font-medium text-gray-700 mb-2">种植技术</h4>
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                <span class="w-2 h-2 bg-green-400 rounded-full mr-2"></span>
                {{ asset.cultivationTechnique }}
              </span>
            </div>

            <!-- 其他信息 -->
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <label class="block font-medium text-gray-700">点赞数</label>
                <p class="text-gray-900">{{ asset.likeCount || 0 }}</p>
              </div>
              <div>
                <label class="block font-medium text-gray-700">查看次数</label>
                <p class="text-gray-900">{{ asset.viewCount || 0 }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 模态框底部 -->
        <div class="bg-gray-50 px-6 py-3 flex justify-end space-x-3">
          <button
            @click="closeModal"
            class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            关闭
          </button>
          <button
            v-if="showViewButton"
            @click="viewInFrontend"
            class="px-4 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            前台查看
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ref, watch } from 'vue'
import { defaultImages, handleImageError as baseHandleImageError } from '@/utils/imageUtils'
import { downloadApi } from '@/services/api'
import { 
  XMarkIcon, 
  DocumentArrowDownIcon, 
  ArrowDownTrayIcon,
  PhotoIcon,
  VideoCameraIcon,
  DocumentIcon,
  MicrophoneIcon
} from '@heroicons/vue/24/outline'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  asset: {
    type: Object,
    default: null
  },
  showViewButton: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['close'])

const router = useRouter()

// 资产图片
const assetImage = computed(() => {
  if (props.asset?.imageUrls && props.asset.imageUrls.length > 0) {
    return props.asset.imageUrls[0]
  }
  return defaultImages.farmland
})

// 质量评级样式
const qualityBadgeClass = computed(() => {
  if (!props.asset) return ''
  
  const rating = props.asset.qualityRating
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

// 关闭模态框
const closeModal = () => {
  emit('close')
}

// 前台查看
const viewInFrontend = () => {
  if (props.asset?.id) {
    const routeData = router.resolve({ name: 'asset-detail', params: { id: props.asset.id } })
    window.open(routeData.href, '_blank')
  }
}

// 获取指标标签
const getMetricLabel = (key) => {
  const labels = {
    temperature: '温度 (°C)',
    humidity: '湿度 (%)',
    soilPh: '土壤pH值',
    sunlightHours: '光照时长 (h)',
    rainfall: '降雨量 (mm)',
    dataScale: '数据规模 (亩)',
    collectionDate: '采集日期',
    location: '采集地点'
  }
  return labels[key] || key
}

// 格式化指标值
const formatMetricValue = (key, value) => {
  if (key === 'collectionDate') {
    return new Date(value).toLocaleDateString('zh-CN')
  }
  if (typeof value === 'number') {
    return value.toLocaleString()
  }
  return value
}

// 审核状态标签
const getReviewStatusLabel = (status) => {
  const labels = {
    'PENDING': '待审核',
    'APPROVED': '审核通过',
    'REJECTED': '审核拒绝'
  }
  return labels[status] || status
}

// 审核状态样式
const getReviewStatusStyle = (status) => {
  const styles = {
    'PENDING': 'bg-yellow-100 text-yellow-700',
    'APPROVED': 'bg-green-100 text-green-700',
    'REJECTED': 'bg-red-100 text-red-700'
  }
  return styles[status] || 'bg-gray-100 text-gray-700'
}

// 发布状态标签
const getPublishStatusLabel = (status) => {
  const labels = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'OFFLINE': '已下线'
  }
  return labels[status] || status
}

// 发布状态样式
const getPublishStatusStyle = (status) => {
  const styles = {
    'DRAFT': 'bg-gray-100 text-gray-700',
    'PUBLISHED': 'bg-green-100 text-green-700',
    'OFFLINE': 'bg-red-100 text-red-700'
  }
  return styles[status] || 'bg-gray-100 text-gray-700'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 处理图片错误
const handleImageError = (event) => {
  baseHandleImageError(event, 'adminAsset')
}

// 获取文件类型样式
const getFileTypeStyle = (fileName) => {
  if (isImageFile(fileName)) return 'bg-blue-100'
  if (isVideoFile(fileName)) return 'bg-green-100'
  if (isAudioFile(fileName)) return 'bg-pink-100'
  return 'bg-gray-100'
}

// 获取文件类型标签
const getFileTypeLabel = (fileName) => {
  if (isImageFile(fileName)) return '图片'
  if (isVideoFile(fileName)) return '视频'
  if (isAudioFile(fileName)) return '音频'
  
  const extension = fileName.slice(fileName.lastIndexOf('.')).toLowerCase()
  const labels = {
    '.pdf': 'PDF文档',
    '.doc': 'Word文档',
    '.docx': 'Word文档',
    '.xls': 'Excel表格',
    '.xlsx': 'Excel表格',
    '.csv': 'CSV数据',
    '.json': 'JSON数据',
    '.xml': 'XML数据',
    '.txt': '文本文件',
    '.zip': '压缩包',
    '.rar': '压缩包',
    '.7z': '压缩包'
  }
  return labels[extension] || '文件'
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
  }
}

// 下载文件
const downloadFile = async (file) => {
  console.log('开始下载文件:', file)
  
  if (!file || !file.savedName) {
    console.error('文件信息不完整:', file)
    alert('文件信息不完整，无法下载')
    return
  }

  try {
    // 显示下载开始提示
    const downloadingMessage = `正在下载文件 "${file.name}"...`
    console.log(downloadingMessage)
    
    // 对于大文件，显示友好提示
    if (file.size > 100 * 1024 * 1024) { // 大于100MB
      const sizeInGB = (file.size / (1024 * 1024 * 1024)).toFixed(2)
      if (!confirm(`文件 "${file.name}" 大小为 ${sizeInGB}GB，下载可能需要较长时间。是否继续？`)) {
        return
      }
    }
    
    const response = await downloadApi.get(`/upload/download/${props.asset.id}/${file.savedName}`, {
      responseType: 'blob',
      timeout: 30 * 60 * 1000, // 30分钟超时，适合大文件下载
      // 添加下载进度回调
      onDownloadProgress: (progressEvent) => {
        if (progressEvent.lengthComputable) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          console.log(`下载进度: ${percentCompleted}%`)
          
          // 可以在这里更新UI显示下载进度
          // 暂时使用console.log，后续可以添加进度条
        }
      }
    })

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.download = file.name || file.savedName // 使用原始文件名
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    console.log('文件下载完成:', file.name)
    alert(`文件 "${file.name}" 下载完成！`)
  } catch (error) {
    console.error('文件下载失败:', error)
    
    let errorMessage = '文件下载失败'
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      errorMessage = `文件下载超时。文件 "${file.name}" 较大，请检查网络连接并重试。`
    } else if (error.response?.status === 404) {
      errorMessage = `文件 "${file.name}" 不存在或已被删除`
    } else if (error.response?.status === 401) {
      errorMessage = '没有权限下载此文件，请重新登录'
    } else if (error.response?.status === 500) {
      errorMessage = '服务器处理文件时发生错误，请稍后重试'
    } else {
      errorMessage = `文件下载失败: ${error.response?.data?.message || error.message}`
    }
    
    alert(errorMessage)
  }
}

// 判断文件是否为图片
const isImageFile = (fileName) => {
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  const extension = fileName.slice(fileName.lastIndexOf('.')).toLowerCase()
  return imageExtensions.includes(extension)
}

// 判断文件是否为视频
const isVideoFile = (fileName) => {
  const videoExtensions = ['.mp4', '.avi', '.mov', '.mkv', '.wmv']
  const extension = fileName.slice(fileName.lastIndexOf('.')).toLowerCase()
  return videoExtensions.includes(extension)
}

// 判断文件是否为音频
const isAudioFile = (fileName) => {
  const audioExtensions = ['.mp3', '.wav', '.ogg', '.flac', '.aac']
  const extension = fileName.slice(fileName.lastIndexOf('.')).toLowerCase()
  return audioExtensions.includes(extension)
}
</script> 