<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <!-- 页面标题 -->
      <div 
        class="text-center"
        v-motion
        :initial="{ opacity: 0, y: -20 }"
        :enter="{ opacity: 1, y: 0, transition: { duration: 600 } }"
      >
        <div class="flex justify-center">
          <div class="w-16 h-16 bg-blue-600 rounded-xl flex items-center justify-center mb-4">
            <span class="text-white font-bold text-2xl">农</span>
          </div>
        </div>
        <h2 class="text-3xl font-bold text-gray-900">登录您的账户</h2>
        <p class="mt-2 text-gray-600">
          还没有账户？
          <router-link to="/register" class="text-blue-600 hover:text-blue-500 font-medium">
            立即注册
          </router-link>
        </p>
      </div>

      <!-- 登录表单 -->
      <div 
        class="bg-white rounded-2xl shadow-xl p-8"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 200 } }"
      >
        <form @submit.prevent="handleLogin" class="space-y-6">
          <!-- 用户名输入 -->
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700 mb-2">
              用户名
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <UserIcon class="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="username"
                v-model="form.username"
                type="text"
                required
                autocomplete="username"
                :class="[
                  'input-field pl-10',
                  errors.username ? 'border-red-300 focus:ring-red-500' : ''
                ]"
                placeholder="请输入用户名"
              />
            </div>
            <p v-if="errors.username" class="mt-1 text-sm text-red-600">
              {{ errors.username }}
            </p>
          </div>

          <!-- 密码输入 -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
              密码
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <LockClosedIcon class="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                required
                autocomplete="current-password"
                :class="[
                  'input-field pl-10 pr-10',
                  errors.password ? 'border-red-300 focus:ring-red-500' : ''
                ]"
                placeholder="请输入密码"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center"
              >
                <EyeIcon v-if="!showPassword" class="h-5 w-5 text-gray-400 hover:text-gray-600" />
                <EyeSlashIcon v-else class="h-5 w-5 text-gray-400 hover:text-gray-600" />
              </button>
            </div>
            <p v-if="errors.password" class="mt-1 text-sm text-red-600">
              {{ errors.password }}
            </p>
          </div>

          <!-- 记住我和忘记密码 -->
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input
                id="remember-me"
                v-model="form.rememberMe"
                type="checkbox"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label for="remember-me" class="ml-2 block text-sm text-gray-700">
                记住我
              </label>
            </div>
            <div class="text-sm">
              <a href="#" class="text-blue-600 hover:text-blue-500">
                忘记密码？
              </a>
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="loginError" class="bg-red-50 border border-red-200 rounded-lg p-3">
            <div class="flex">
              <ExclamationTriangleIcon class="h-5 w-5 text-red-400" />
              <div class="ml-3">
                <p class="text-sm text-red-800">{{ loginError }}</p>
              </div>
            </div>
          </div>

          <!-- 登录按钮 -->
          <div>
            <button
              type="submit"
              :disabled="loading"
              class="w-full btn-primary py-3 text-lg relative"
            >
              <span v-if="!loading">登录</span>
              <span v-else class="flex items-center justify-center">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                登录中...
              </span>
            </button>
          </div>

          <!-- 分割线 -->
          <div class="relative my-6">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-300"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-white text-gray-500">或</span>
            </div>
          </div>

          <!-- 第三方登录 -->
          <div class="grid grid-cols-2 gap-3">
            <button
              type="button"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 transition-colors"
            >
              <span class="text-lg mr-2">🐧</span>
              QQ登录
            </button>
            <button
              type="button"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 transition-colors"
            >
              <span class="text-lg mr-2">💬</span>
              微信登录
            </button>
          </div>

          <!-- 管理员快速登录 -->
          <div class="mt-4">
            <button
              type="button"
              @click="adminQuickLogin"
              class="w-full inline-flex justify-center py-2 px-4 border border-blue-300 rounded-lg shadow-sm bg-blue-50 text-sm font-medium text-blue-600 hover:bg-blue-100 transition-colors"
            >
              <span class="text-lg mr-2">⚙️</span>
              管理员快速登录
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  UserIcon, 
  LockClosedIcon, 
  EyeIcon, 
  EyeSlashIcon,
  ExclamationTriangleIcon 
} from '@heroicons/vue/24/outline'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const showPassword = ref(false)
const loginError = ref('')

const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const errors = reactive({
  username: '',
  password: ''
})

// 表单验证
const validateForm = () => {
  errors.username = ''
  errors.password = ''
  
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    return false
  }
  
  if (form.username.length < 3) {
    errors.username = '用户名至少3个字符'
    return false
  }
  
  if (!form.password) {
    errors.password = '请输入密码'
    return false
  }
  
  if (form.password.length < 6) {
    errors.password = '密码至少6个字符'
    return false
  }
  
  return true
}

// 处理登录
const handleLogin = async () => {
  if (!validateForm()) return
  
  try {
    loading.value = true
    loginError.value = ''
    
    const result = await authStore.login({
      username: form.username.trim(),
      password: form.password
    })
    
    if (result.success) {
      // 登录成功，跳转到目标页面或首页
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } else {
      loginError.value = result.message
    }
  } catch (error) {
    loginError.value = '登录失败，请稍后重试'
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 管理员快速登录
const adminQuickLogin = async () => {
  try {
    loading.value = true
    loginError.value = ''
    
    const result = await authStore.login({
      username: 'admin',
      password: 'admin123'
    })
    
    if (result.success) {
      // 登录成功后直接跳转到管理后台
      router.push('/admin')
    } else {
      loginError.value = '管理员登录失败：' + result.message
    }
  } catch (error) {
    loginError.value = '管理员登录失败，请稍后重试'
    console.error('管理员登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script> 