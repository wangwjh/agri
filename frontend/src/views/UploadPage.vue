<template>
  <div class="min-h-screen bg-gray-50 pt-16">
    <!-- 顶部横幅 -->
    <div class="bg-gradient-to-r from-green-600 to-blue-600 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-bold mb-4">上传农业数据资产</h1>
        <p class="text-xl opacity-90">将您的农业数据转化为价值，与全球用户分享</p>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <!-- 未登录提示 -->
      <div v-if="!authStore.isAuthenticated" class="bg-yellow-50 border border-yellow-200 rounded-xl p-6 mb-8">
        <div class="flex items-center">
          <div class="w-12 h-12 bg-yellow-500 rounded-full flex items-center justify-center mr-4">
            <InformationCircleIcon class="h-6 w-6 text-white" />
          </div>
          <div>
            <h3 class="text-lg font-semibold text-yellow-800 mb-2">请先登录</h3>
            <p class="text-yellow-700 mb-4">您需要登录后才能上传农业数据资产</p>
            <button 
              @click="router.push('/login')"
              class="px-4 py-2 bg-yellow-500 text-white rounded-lg font-medium hover:bg-yellow-600 transition-colors"
            >
              立即登录
            </button>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-lg overflow-hidden" :class="{ 'opacity-50 pointer-events-none': !authStore.isAuthenticated }">
        <!-- 表单头部 -->
        <div class="bg-gradient-to-r from-green-50 to-blue-50 px-8 py-6 border-b">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-green-500 rounded-full flex items-center justify-center mr-4">
              <CloudArrowUpIcon class="h-6 w-6 text-white" />
            </div>
            <div>
              <h2 class="text-2xl font-bold text-gray-900">数据资产信息</h2>
              <p class="text-gray-600">请详细填写您的农业数据资产信息</p>
            </div>
          </div>
        </div>

        <!-- 上传表单 -->
        <form @submit.prevent="submitUpload" class="p-8">
          <div class="space-y-8">
            <!-- 基本信息 -->
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <DocumentTextIcon class="w-5 h-5 mr-2 text-blue-500" />
                基本信息
              </h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    资产名称 <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="uploadForm.name"
                    type="text"
                    required
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                    placeholder="请输入简洁明确的资产名称"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    价格 (¥) <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model.number="uploadForm.price"
                    type="number"
                    min="0"
                    step="0.01"
                    required
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                    placeholder="请输入合理的价格"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    作物类型 <span class="text-red-500">*</span>
                  </label>
                  <select
                    v-model="uploadForm.cropType"
                    required
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                  >
                    <option value="">请选择作物类型</option>
                    <option value="小麦">小麦</option>
                    <option value="玉米">玉米</option>
                    <option value="水稻">水稻</option>
                    <option value="大豆">大豆</option>
                    <option value="蔬菜">蔬菜</option>
                    <option value="水果">水果</option>
                    <option value="其他">其他</option>
                  </select>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    种植地点 <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="uploadForm.location"
                    type="text"
                    required
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                    placeholder="省市区域，如：山东省济南市"
                  />
                </div>
              </div>

              <div class="mt-6">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  详细描述 <span class="text-red-500">*</span>
                </label>
                <textarea
                  v-model="uploadForm.description"
                  rows="4"
                  required
                  class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all resize-none"
                  placeholder="请详细描述您的农业数据资产，包括数据来源、采集方法、数据特点等"
                ></textarea>
              </div>
            </div>

            <!-- 数据信息 -->
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <ChartBarIcon class="w-5 h-5 mr-2 text-green-500" />
                数据信息
              </h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    数据规模 <span class="text-sm font-normal text-gray-500">（可选）</span>
                  </label>
                  <div class="relative">
                    <input
                      v-model.number="uploadForm.dataScale"
                      type="number"
                      min="0"
                      step="0.1"
                      class="w-full border border-gray-300 rounded-lg px-4 py-3 pr-12 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                      placeholder="如：100"
                    />
                    <span class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 text-sm">亩</span>
                  </div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    采集日期 <span class="text-sm font-normal text-gray-500">（可选）</span>
                  </label>
                  <input
                    v-model="uploadForm.collectionDate"
                    type="date"
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                  />
                </div>
              </div>

              <!-- 文件上传区域 -->
              <div class="mt-6">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  数据文件 <span class="text-sm font-normal text-gray-500">（可选）</span>
                </label>
                <div class="space-y-4">
                  <!-- 文件上传拖拽区域 -->
                  <div 
                    @drop="handleDrop"
                    @dragover.prevent
                    @dragenter="handleDragEnter"
                    @dragleave="handleDragLeave"
                    class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-green-400 transition-colors cursor-pointer"
                    :class="{ 'border-green-400 bg-green-50': isDragging }"
                    @click="$refs.fileInput.click()"
                  >
                    <DocumentArrowUpIcon class="w-12 h-12 text-gray-400 mx-auto mb-4" />
                    <p class="text-lg font-medium text-gray-700 mb-2">拖拽文件到此处或点击上传</p>
                    <p class="text-sm text-gray-500 mb-4">
                      支持多种格式：CSV、Excel、JSON、XML、PDF、图片等
                    </p>
                    <div class="text-xs text-gray-400">
                      最大文件大小：5GB
                    </div>
                  </div>

                  <!-- 隐藏的文件输入 -->
                  <input
                    ref="fileInput"
                    type="file"
                    multiple
                    class="hidden"
                    accept=".csv,.xlsx,.xls,.json,.xml,.pdf,.txt,.doc,.docx,.png,.jpg,.jpeg,.gif,.bmp,.tiff,.zip,.rar,.7z"
                    @change="handleFileSelect"
                  />

                  <!-- 支持的文件格式提示 -->
                  <div class="bg-blue-50 rounded-lg p-4">
                    <h5 class="text-sm font-medium text-blue-800 mb-2">支持的文件格式：</h5>
                    <div class="grid grid-cols-2 md:grid-cols-4 gap-2 text-xs text-blue-700">
                      <div class="flex items-center space-x-2">
                        <span class="w-2 h-2 bg-green-500 rounded-full"></span>
                        <span>数据文件：CSV, Excel, JSON, XML</span>
                      </div>
                      <div class="flex items-center space-x-2">
                        <span class="w-2 h-2 bg-blue-500 rounded-full"></span>
                        <span>文档：PDF, Word, TXT</span>
                      </div>
                      <div class="flex items-center space-x-2">
                        <span class="w-2 h-2 bg-purple-500 rounded-full"></span>
                        <span>图片：PNG, JPG, GIF, TIFF</span>
                      </div>
                      <div class="flex items-center space-x-2">
                        <span class="w-2 h-2 bg-orange-500 rounded-full"></span>
                        <span>压缩包：ZIP, RAR, 7Z</span>
                      </div>
                    </div>
                  </div>

                  <!-- 已选择的文件列表 -->
                  <div v-if="uploadForm.files && uploadForm.files.length > 0" class="space-y-2">
                    <h5 class="text-sm font-medium text-gray-700">已选择的文件：</h5>
                    <div class="space-y-2">
                      <div 
                        v-for="(file, index) in uploadForm.files"
                        :key="index"
                        class="flex items-center justify-between bg-gray-50 rounded-lg p-3"
                      >
                        <div class="flex items-center space-x-3">
                          <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                            <DocumentTextIcon class="w-4 h-4 text-blue-600" />
                          </div>
                          <div>
                            <div class="text-sm font-medium text-gray-900">{{ file.name }}</div>
                            <div class="text-xs text-gray-500">{{ formatFileSize(file.size) }}</div>
                          </div>
                        </div>
                        <button 
                          @click="removeFile(index)"
                          class="p-1 text-red-500 hover:text-red-700 transition-colors"
                          title="删除文件"
                        >
                          <XMarkIcon class="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 环境数据 -->
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <BeakerIcon class="w-5 h-5 mr-2 text-blue-500" />
                环境数据 <span class="text-sm font-normal text-gray-500">（可选，有助于提升数据价值）</span>
              </h3>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">平均温度 (°C)</label>
                  <input
                    v-model.number="uploadForm.avgTemperature"
                    type="number"
                    step="0.1"
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                    placeholder="如：25.5"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">平均湿度 (%)</label>
                  <input
                    v-model.number="uploadForm.avgHumidity"
                    type="number"
                    min="0"
                    max="100"
                    step="0.1"
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                    placeholder="如：65"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">土壤pH值</label>
                  <input
                    v-model.number="uploadForm.soilPh"
                    type="number"
                    min="0"
                    max="14"
                    step="0.1"
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                    placeholder="如：6.8"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">光照时长 (小时)</label>
                  <input
                    v-model.number="uploadForm.sunlightHours"
                    type="number"
                    min="0"
                    max="24"
                    step="0.1"
                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                    placeholder="如：8.5"
                  />
                </div>
              </div>
            </div>

            <!-- 上传须知 -->
            <div class="bg-blue-50 border border-blue-200 rounded-lg p-6">
              <h4 class="text-sm font-semibold text-blue-800 mb-3 flex items-center">
                <InformationCircleIcon class="w-4 h-4 mr-2" />
                上传须知
              </h4>
              <ul class="text-sm text-blue-700 space-y-2">
                <li>• 上传的数据将经过平台审核，审核通过后方可发布</li>
                <li>• 请确保数据的真实性和准确性，虚假数据将被拒绝</li>
                <li>• 数据应具有一定的商业价值和实用性</li>
                <li>• <strong>支持大文件上传：</strong>单个文件最大支持5GB，适合大型农业数据集</li>
                <li>• <strong>多文件批量上传：</strong>可同时上传多个文件，系统会自动处理</li>
                <li>• 大文件上传可能需要较长时间，请耐心等待并保持网络连接稳定</li>
                <li>• 上传即表示您同意平台的使用条款和隐私政策</li>
              </ul>
            </div>

            <!-- 提交按钮 -->
            <div class="flex justify-center pt-6">
              <div class="w-full max-w-md">
                <button
                  type="submit"
                  :disabled="uploading || !isFormValid"
                  class="w-full px-8 py-4 bg-gradient-to-r from-green-600 to-blue-600 text-white rounded-lg font-semibold text-lg shadow-lg hover:from-green-700 hover:to-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 transform hover:scale-105"
                >
                  <span v-if="uploading" class="flex items-center justify-center">
                    <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <div>
                      <div>{{ uploadStatus || '正在处理...' }}</div>
                      <div v-if="uploadProgress > 0 && uploadProgress < 100" class="text-xs">
                        {{ uploadProgress }}%
                      </div>
                    </div>
                  </span>
                  <span v-else class="flex items-center justify-center">
                    <CloudArrowUpIcon class="w-5 h-5 mr-2" />
                    提交审核
                  </span>
                </button>
                
                <!-- 上传进度条 -->
                <div v-if="uploading && uploadProgress > 0" class="mt-4">
                  <div class="flex justify-between text-xs text-gray-600 mb-1">
                    <span>上传进度</span>
                    <span>{{ uploadProgress }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-gradient-to-r from-green-500 to-blue-500 h-2 rounded-full transition-all duration-300"
                      :style="{ width: uploadProgress + '%' }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  CloudArrowUpIcon,
  DocumentTextIcon,
  ChartBarIcon,
  BeakerIcon,
  InformationCircleIcon,
  DocumentArrowUpIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import api, { uploadApi } from '@/services/api'

const router = useRouter()
const authStore = useAuthStore()

const uploading = ref(false)
const isDragging = ref(false)
const uploadProgress = ref(0)
const uploadStatus = ref('')
const uploadStartTime = ref(null)

// 上传表单数据
const uploadForm = reactive({
  name: '',
  description: '',
  price: null,
  cropType: '',
  location: '',
  collectionDate: '',
  dataScale: null,
  avgTemperature: null,
  avgHumidity: null,
  soilPh: null,
  sunlightHours: null,
  files: []
})

// 表单验证
const isFormValid = computed(() => {
  return uploadForm.name && 
         uploadForm.description && 
         uploadForm.price && 
         uploadForm.price > 0 &&
         uploadForm.cropType &&
         uploadForm.location
})

// 提交上传
const submitUpload = async () => {
  // 检查登录状态
  if (!authStore.isAuthenticated) {
    alert('请先登录后再上传数据资产！')
    router.push('/login')
    return
  }

  try {
    uploading.value = true
    
    let uploadedFiles = []
    
    // 如果有文件需要上传，先上传文件
    if (uploadForm.files && uploadForm.files.length > 0) {
      console.log(`开始上传 ${uploadForm.files.length} 个文件...`)
      uploadStatus.value = `正在上传 ${uploadForm.files.length} 个文件...`
      uploadProgress.value = 0
      
      // 创建FormData来上传文件
      const formData = new FormData()
      uploadForm.files.forEach((fileObj) => {
        formData.append('files', fileObj.file)
      })
      
      try {
        // 调用文件上传API
        const uploadResponse = await uploadApi.post('/upload/multiple', formData, {
          // 移除Content-Type，让浏览器自动设置
          // headers: {
          //   'Content-Type': 'multipart/form-data',
          // },
          // 添加上传进度回调
          onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
            uploadProgress.value = percentCompleted
            
            // 计算上传速度和预估剩余时间
            const currentTime = Date.now()
            if (!uploadStartTime.value) {
              uploadStartTime.value = currentTime
            }
            
            const elapsed = (currentTime - uploadStartTime.value) / 1000 // 秒
            const uploadedBytes = progressEvent.loaded
            const totalBytes = progressEvent.total
            const speed = uploadedBytes / elapsed // bytes/second
            const remainingBytes = totalBytes - uploadedBytes
            const remainingTime = remainingBytes / speed // 秒
            
            if (elapsed > 5) { // 5秒后开始显示速度信息
              const speedMB = (speed / (1024 * 1024)).toFixed(2)
              const remainingMin = Math.ceil(remainingTime / 60)
              uploadStatus.value = `文件上传中... ${percentCompleted}% (${speedMB} MB/s, 约${remainingMin}分钟剩余)`
            } else {
              uploadStatus.value = `文件上传中... ${percentCompleted}%`
            }
            
            console.log(`文件上传进度: ${percentCompleted}%`)
          },
          // 设置大文件上传的额外配置
          timeout: 1800000, // 30分钟
          maxContentLength: 5 * 1024 * 1024 * 1024,
          maxBodyLength: 5 * 1024 * 1024 * 1024,
        })
        
        if (uploadResponse.data.success) {
          uploadedFiles = uploadResponse.data.data.uploadResults || []
          uploadStatus.value = '文件上传完成，正在创建数据资产...'
          console.log('文件上传成功:', uploadedFiles)
          
          // 添加调试信息
          console.log('上传响应完整数据:', uploadResponse.data)
          console.log('uploadResults详细信息:', uploadResponse.data.data.uploadResults)
          
          if (uploadResponse.data.data.failedFiles && uploadResponse.data.data.failedFiles.length > 0) {
            console.warn('部分文件上传失败:', uploadResponse.data.data.failedFiles)
            alert('部分文件上传失败，但将继续创建数据资产')
          }
        } else {
          throw new Error(uploadResponse.data.message || '文件上传失败')
        }
      } catch (uploadError) {
        console.error('文件上传失败:', uploadError)
        uploadStatus.value = '文件上传失败'
        
        // 改进错误提示
        let errorMessage = '文件上传失败'
        if (uploadError.code === 'ECONNABORTED' || uploadError.message.includes('timeout')) {
          errorMessage = '文件上传超时。大文件上传需要较长时间，请检查网络连接并重试。'
        } else if (uploadError.response?.status === 413) {
          errorMessage = '文件过大，超过服务器限制。'
        } else if (uploadError.response?.status === 500) {
          errorMessage = '服务器处理文件时发生错误，请稍后重试。'
        } else if (uploadError.message.includes('Network Error')) {
          errorMessage = '网络连接错误。请检查网络连接，确保网络稳定后重试。'
        } else {
          errorMessage = '文件上传失败: ' + (uploadError.response?.data?.message || uploadError.message)
        }
        
        alert(errorMessage)
        return
      }
    } else {
      uploadStatus.value = '正在创建数据资产...'
    }
    
    // 构建请求数据，匹配后端AgriculturalAsset模型
    const assetData = {
      name: uploadForm.name,
      description: uploadForm.description,
      price: uploadForm.price,
      category: uploadForm.cropType, // 使用category字段而不是tags
      // 将环境数据存储在metrics中
      metrics: {
        temperature: uploadForm.avgTemperature,
        humidity: uploadForm.avgHumidity,
        soilPh: uploadForm.soilPh,
        sunlightHours: uploadForm.sunlightHours,
        dataScale: uploadForm.dataScale,
        collectionDate: uploadForm.collectionDate,
        location: uploadForm.location
      },
      // 设置默认图片
      imageUrls: ['https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=400&q=80'],
      // 默认状态为可用
      status: 'AVAILABLE',
      // 审核状态由后端自动设置为PENDING
      qualityRating: 'A', // 默认质量评级
      // 使用上传后的文件信息
      attachedFiles: uploadedFiles.map(file => ({
        name: file.originalName,
        savedName: file.savedName,
        size: parseInt(file.size) || 0, // 确保size是整数
        type: file.contentType,
        relativePath: file.relativePath,
        uploadTime: file.uploadTime ? formatDateTimeForBackend(file.uploadTime) : formatDateTimeForBackend(new Date()) // 格式化为后端期望的格式
      }))
    }
    
    // 添加调试信息
    console.log('准备创建的资产数据:', assetData)
    console.log('附件文件信息:', assetData.attachedFiles)
    
    const response = await api.post('/assets', assetData)
    
    if (response.data.success) {
      // 成功提示
      let successMessage = '🎉 数据上传成功！\n\n您的数据资产已提交审核，审核通过后将在平台上展示供用户购买。\n\n您可以在"交易信息"页面查看审核状态。'
      
      if (uploadedFiles.length > 0) {
        successMessage += `\n\n📁 成功上传 ${uploadedFiles.length} 个文件`
      }
      
      alert(successMessage)
      
      // 重置表单
      Object.assign(uploadForm, {
        name: '',
        description: '',
        price: null,
        cropType: '',
        location: '',
        collectionDate: '',
        dataScale: null,
        avgTemperature: null,
        avgHumidity: null,
        soilPh: null,
        sunlightHours: null,
        files: []
      })
      
      // 跳转到搜索页面查看所有数据
      router.push('/search')
    } else {
      throw new Error(response.data.message || '创建资产失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    if (error.response?.status === 401) {
      alert('❌ 登录已过期，请重新登录！')
      authStore.logout()
      router.push('/login')
    } else {
      alert('❌ 上传失败：' + (error.response?.data?.message || error.message))
    }
  } finally {
    uploading.value = false
    uploadStatus.value = ''
    uploadProgress.value = 0
  }
}

// 处理文件拖拽
const handleDrop = (event) => {
  event.preventDefault()
  isDragging.value = false
  
  console.log('文件拖拽事件:', event)
  
  // 获取文件列表
  const files = Array.from(event.dataTransfer.files)
  console.log('拖拽的文件数量:', files.length)
  console.log('拖拽的文件详情:', files.map(f => ({
    name: f.name,
    size: f.size,
    type: f.type,
    lastModified: f.lastModified
  })))
  
  if (files.length === 0) {
    console.warn('没有检测到任何文件')
    alert('没有检测到任何文件，请重试')
    return
  }
  
  addFiles(files)
}

const handleDragEnter = (event) => {
  event.preventDefault()
  isDragging.value = true
}

const handleDragLeave = (event) => {
  event.preventDefault()
  // 只有当真正离开拖拽区域时才设置为false
  if (!event.currentTarget.contains(event.relatedTarget)) {
    isDragging.value = false
  }
}

// 处理文件选择
const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  console.log('选择的文件数量:', files.length)
  console.log('选择的文件详情:', files.map(f => ({
    name: f.name,
    size: f.size,
    type: f.type,
    lastModified: f.lastModified
  })))
  
  addFiles(files)
  // 清空input，允许重复选择同一文件
  event.target.value = ''
}

// 添加文件到列表
const addFiles = (files) => {
  const maxSize = 5 * 1024 * 1024 * 1024 // 5GB
  
  files.forEach(file => {
    console.log(`处理文件: ${file.name}, 大小: ${file.size} bytes, 类型: ${file.type}`)
    
    // 检查文件对象是否有效
    if (!file || !file.name) {
      console.error('无效的文件对象:', file)
      alert('检测到无效文件，请重试')
      return
    }
    
    // 检查文件大小是否有效
    if (file.size === undefined || file.size === null) {
      console.warn('文件大小未知:', file.name)
      // 对于大小未知的文件，我们仍然允许添加，但会显示警告
      if (!confirm(`文件 "${file.name}" 的大小无法确定，是否继续添加？`)) {
        return
      }
    } else if (file.size === 0) {
      console.warn('文件大小为0:', file.name)
      if (!confirm(`文件 "${file.name}" 的大小为0字节，可能是空文件或快捷方式，是否继续添加？`)) {
        return
      }
    } else if (file.size > maxSize) {
      alert(`文件 "${file.name}" 超过5GB大小限制`)
      return
    }
    
    // 检查是否已存在同名文件
    const existingFile = uploadForm.files.find(f => f.name === file.name)
    if (existingFile) {
      alert(`文件 "${file.name}" 已存在`)
      return
    }
    
    // 添加文件
    const fileObj = {
      name: file.name,
      size: file.size || 0, // 确保size有默认值
      type: file.type || 'application/octet-stream', // 确保type有默认值
      file: file,
      lastModified: file.lastModified || Date.now()
    }
    
    console.log('添加文件对象:', fileObj)
    uploadForm.files.push(fileObj)
  })
  
  console.log('当前文件列表:', uploadForm.files)
}

// 移除文件
const removeFile = (index) => {
  console.log('移除文件索引:', index)
  uploadForm.files.splice(index, 1)
}

// 格式化文件大小
const formatFileSize = (size) => {
  // 处理undefined、null或0的情况
  if (size === undefined || size === null || size === 0) {
    return '0 B'
  }
  
  // 确保size是数字
  const numSize = Number(size)
  if (isNaN(numSize)) {
    return '未知大小'
  }
  
  if (numSize < 1024) {
    return numSize + ' B'
  } else if (numSize < 1024 * 1024) {
    return (numSize / 1024).toFixed(2) + ' KB'
  } else if (numSize < 1024 * 1024 * 1024) {
    return (numSize / (1024 * 1024)).toFixed(2) + ' MB'
  } else {
    return (numSize / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
  }
}

// 格式化日期为后端期望的格式
const formatDateTimeForBackend = (dateInput) => {
  const date = new Date(dateInput)
  if (isNaN(date.getTime())) {
    return new Date().toISOString().replace('T', ' ').split('.')[0]
  }
  return date.toISOString().replace('T', ' ').split('.')[0]
}
</script>

<style scoped>
/* 自定义滚动条 */
textarea::-webkit-scrollbar {
  width: 6px;
}

textarea::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

textarea::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

textarea::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 输入框聚焦动画 */
input:focus, select:focus, textarea:focus {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.15);
}

/* 按钮悬停效果 */
button:not(:disabled):hover {
  box-shadow: 0 8px 25px rgba(34, 197, 94, 0.3);
}
</style> 