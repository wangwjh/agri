<template>
  <div class="min-h-screen bg-gray-50 pt-16">
    <!-- 页面标题 -->
    <div class="bg-white shadow-sm">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div 
          class="flex items-center space-x-6"
          v-motion
          :initial="{ opacity: 0, y: -20 }"
          :enter="{ opacity: 1, y: 0, transition: { duration: 600 } }"
        >
          <!-- 头像 -->
          <div class="relative">
            <div class="w-24 h-24 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center shadow-lg">
              <span class="text-white text-3xl font-bold">
                {{ authStore.user?.username?.charAt(0).toUpperCase() }}
              </span>
            </div>
            <button 
              class="absolute bottom-0 right-0 w-8 h-8 bg-white rounded-full shadow-lg flex items-center justify-center hover:bg-gray-50 transition-colors"
              title="更换头像"
            >
              <CameraIcon class="w-4 h-4 text-gray-600" />
            </button>
          </div>
          
          <div>
            <h1 class="text-3xl font-bold text-gray-900">{{ authStore.user?.username || '用户' }}</h1>
            <p class="mt-2 text-gray-600">管理您的个人信息和偏好设置</p>
            <div class="mt-3 flex items-center space-x-4 text-sm text-gray-500">
              <span class="flex items-center">
                <CalendarIcon class="w-4 h-4 mr-1" />
                加入时间: {{ formatDate(authStore.user?.createdAt) }}
              </span>
              <span class="flex items-center">
                <ShieldCheckIcon class="w-4 h-4 mr-1" />
                身份已验证
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- 左侧菜单 -->
        <div class="lg:col-span-1">
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-100 p-6"
            v-motion
            :initial="{ opacity: 0, x: -30 }"
            :enter="{ opacity: 1, x: 0, transition: { duration: 600, delay: 200 } }"
          >
            <nav class="space-y-2">
              <button 
                v-for="tab in tabs"
                :key="tab.key"
                @click="activeTab = tab.key"
                :class="[
                  'w-full flex items-center space-x-3 px-4 py-3 rounded-lg text-left transition-colors',
                  activeTab === tab.key 
                    ? 'bg-blue-50 text-blue-700 border border-blue-200' 
                    : 'text-gray-700 hover:bg-gray-50'
                ]"
              >
                <component :is="tab.icon" class="w-5 h-5" />
                <span class="font-medium">{{ tab.label }}</span>
              </button>
            </nav>
          </div>
        </div>

        <!-- 右侧内容 -->
        <div class="lg:col-span-2">
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-100"
            v-motion
            :initial="{ opacity: 0, x: 30 }"
            :enter="{ opacity: 1, x: 0, transition: { duration: 600, delay: 400 } }"
          >
            <!-- 基本信息 -->
            <div v-if="activeTab === 'profile'" class="p-6">
              <h2 class="text-xl font-semibold text-gray-900 mb-6">基本信息</h2>
              
              <form @submit.prevent="updateProfile" class="space-y-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
                    <input
                      v-model="profileForm.username"
                      type="text"
                      class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                      placeholder="请输入用户名"
                    />
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">邮箱</label>
                    <input
                      v-model="profileForm.email"
                      type="email"
                      class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                      placeholder="请输入邮箱"
                    />
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">手机号</label>
                    <input
                      v-model="profileForm.phone"
                      type="tel"
                      class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                      placeholder="请输入手机号"
                    />
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">所在地区</label>
                    <input
                      v-model="profileForm.location"
                      type="text"
                      class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                      placeholder="请输入所在地区"
                    />
                  </div>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">个人简介</label>
                  <textarea
                    v-model="profileForm.bio"
                    rows="4"
                    class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                    placeholder="介绍一下自己..."
                  ></textarea>
                </div>
                
                <div class="flex justify-end">
                  <button 
                    type="submit"
                    :disabled="updating"
                    class="btn-primary"
                  >
                    {{ updating ? '保存中...' : '保存更改' }}
                  </button>
                </div>
              </form>
            </div>

            <!-- 安全设置 -->
            <div v-if="activeTab === 'security'" class="p-6">
              <h2 class="text-xl font-semibold text-gray-900 mb-6">安全设置</h2>
              
              <div class="space-y-6">
                <!-- 修改密码 -->
                <div class="border border-gray-200 rounded-lg p-4">
                  <h3 class="text-lg font-medium text-gray-900 mb-4">修改密码</h3>
                  <form @submit.prevent="changePassword" class="space-y-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-2">当前密码</label>
                      <input
                        v-model="passwordForm.currentPassword"
                        type="password"
                        class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                        placeholder="请输入当前密码"
                      />
                    </div>
                    
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-2">新密码</label>
                      <input
                        v-model="passwordForm.newPassword"
                        type="password"
                        class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                        placeholder="请输入新密码"
                      />
                    </div>
                    
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-2">确认新密码</label>
                      <input
                        v-model="passwordForm.confirmPassword"
                        type="password"
                        class="block w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                        placeholder="请再次输入新密码"
                      />
                    </div>
                    
                    <div class="flex justify-end">
                      <button 
                        type="submit"
                        :disabled="changingPassword"
                        class="btn-primary"
                      >
                        {{ changingPassword ? '修改中...' : '修改密码' }}
                      </button>
                    </div>
                  </form>
                </div>
                
                <!-- 两步验证 -->
                <div class="border border-gray-200 rounded-lg p-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <h3 class="text-lg font-medium text-gray-900">两步验证</h3>
                      <p class="text-sm text-gray-600">为您的账户添加额外的安全保护</p>
                    </div>
                    <button class="btn-secondary">
                      启用
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 通知设置 -->
            <div v-if="activeTab === 'notifications'" class="p-6">
              <h2 class="text-xl font-semibold text-gray-900 mb-6">通知设置</h2>
              
              <div class="space-y-6">
                <div 
                  v-for="notification in notificationSettings"
                  :key="notification.key"
                  class="flex items-center justify-between py-4 border-b border-gray-100 last:border-b-0"
                >
                  <div>
                    <h3 class="text-base font-medium text-gray-900">{{ notification.title }}</h3>
                    <p class="text-sm text-gray-600">{{ notification.description }}</p>
                  </div>
                  <label class="relative inline-flex items-center cursor-pointer">
                    <input 
                      v-model="notification.enabled"
                      type="checkbox"
                      class="sr-only peer"
                    />
                    <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                  </label>
                </div>
              </div>
              
              <div class="mt-6 flex justify-end">
                <button 
                  @click="saveNotificationSettings"
                  :disabled="savingNotifications"
                  class="btn-primary"
                >
                  {{ savingNotifications ? '保存中...' : '保存设置' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { 
  UserIcon,
  ShieldCheckIcon,
  BellIcon,
  CameraIcon,
  CalendarIcon
} from '@heroicons/vue/24/outline'

const authStore = useAuthStore()
const router = useRouter()

const activeTab = ref('profile')
const updating = ref(false)
const changingPassword = ref(false)
const savingNotifications = ref(false)

const tabs = [
  { key: 'profile', label: '基本信息', icon: UserIcon },
  { key: 'security', label: '安全设置', icon: ShieldCheckIcon },
  { key: 'notifications', label: '通知设置', icon: BellIcon }
]

const profileForm = reactive({
  username: '',
  email: '',
  phone: '',
  location: '',
  bio: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const notificationSettings = reactive([
  {
    key: 'transaction',
    title: '交易通知',
    description: '当有新的交易活动时通知我',
    enabled: true
  },
  {
    key: 'message',
    title: '消息通知',
    description: '当收到新消息时通知我',
    enabled: true
  },
  {
    key: 'asset_update',
    title: '资产更新',
    description: '当关注的资产有更新时通知我',
    enabled: false
  },
  {
    key: 'marketing',
    title: '营销信息',
    description: '接收产品更新和优惠信息',
    enabled: false
  }
])

const updateProfile = async () => {
  try {
    updating.value = true
    
    // 调用后端API更新用户信息
    const response = await api.put('/auth/profile', {
      username: profileForm.username,
      email: profileForm.email,
      phone: profileForm.phone,
      location: profileForm.location,
      bio: profileForm.bio
    })
    
    if (response.data.success) {
      // 更新本地用户信息
      if (response.data.data && response.data.data.user) {
        authStore.user = response.data.data.user
      }
      
      alert('个人资料更新成功！')
    } else {
      alert(response.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新失败:', error)
    const errorMessage = error.response?.data?.message || '更新失败，请重试'
    alert(errorMessage)
  } finally {
    updating.value = false
  }
}

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    alert('新密码和确认密码不匹配')
    return
  }
  
  if (passwordForm.newPassword.length < 6) {
    alert('新密码长度至少6个字符')
    return
  }
  
  try {
    changingPassword.value = true
    
    // 调用后端API修改密码
    const response = await api.post('/auth/change-password', {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.data.success) {
      alert('密码修改成功！请重新登录')
      
      // 清空表单
      passwordForm.currentPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      
      // 登出用户，要求重新登录
      authStore.logout()
      router.push('/login')
    } else {
      alert(response.data.message || '密码修改失败')
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    const errorMessage = error.response?.data?.message || '密码修改失败，请重试'
    alert(errorMessage)
  } finally {
    changingPassword.value = false
  }
}

const saveNotificationSettings = async () => {
  try {
    savingNotifications.value = true
    // TODO: 调用API保存通知设置
    console.log('保存通知设置:', notificationSettings)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    alert('设置保存成功！')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  } finally {
    savingNotifications.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

onMounted(() => {
  // 初始化表单数据
  if (authStore.user) {
    profileForm.username = authStore.user.username || ''
    profileForm.email = authStore.user.email || ''
    profileForm.phone = authStore.user.phone || ''
    profileForm.location = authStore.user.location || ''
    profileForm.bio = authStore.user.bio || ''
  }
})
</script> 