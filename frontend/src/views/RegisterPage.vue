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
        <h2 class="text-3xl font-bold text-gray-900">创建新账户</h2>
        <p class="mt-2 text-gray-600">
          已有账户？
          <router-link to="/login" class="text-blue-600 hover:text-blue-500 font-medium">
            立即登录
          </router-link>
        </p>
      </div>

      <!-- 注册表单 -->
      <div 
        class="bg-white rounded-2xl shadow-xl p-8"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 200 } }"
      >
        <form @submit.prevent="handleRegister" class="space-y-6">
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
            <p v-else class="mt-1 text-xs text-gray-500">
              用户名长度在3-20字符之间
            </p>
          </div>

          <!-- 邮箱输入 -->
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
              邮箱地址
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <AtSymbolIcon class="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="email"
                v-model="form.email"
                type="email"
                required
                autocomplete="email"
                :class="[
                  'input-field pl-10',
                  errors.email ? 'border-red-300 focus:ring-red-500' : ''
                ]"
                placeholder="请输入邮箱地址"
              />
            </div>
            <p v-if="errors.email" class="mt-1 text-sm text-red-600">
              {{ errors.email }}
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
                autocomplete="new-password"
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
            <p v-else class="mt-1 text-xs text-gray-500">
              密码长度在6-40字符之间
            </p>
          </div>

          <!-- 确认密码输入 -->
          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-2">
              确认密码
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <LockClosedIcon class="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                required
                autocomplete="new-password"
                :class="[
                  'input-field pl-10 pr-10',
                  errors.confirmPassword ? 'border-red-300 focus:ring-red-500' : ''
                ]"
                placeholder="请再次输入密码"
              />
              <button
                type="button"
                @click="showConfirmPassword = !showConfirmPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center"
              >
                <EyeIcon v-if="!showConfirmPassword" class="h-5 w-5 text-gray-400 hover:text-gray-600" />
                <EyeSlashIcon v-else class="h-5 w-5 text-gray-400 hover:text-gray-600" />
              </button>
            </div>
            <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-600">
              {{ errors.confirmPassword }}
            </p>
          </div>

          <!-- 同意条款 -->
          <div class="flex items-start">
            <div class="flex items-center h-5">
              <input
                id="agreeTos"
                v-model="form.agreeTos"
                type="checkbox"
                required
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
            </div>
            <div class="ml-3 text-sm">
              <label for="agreeTos" class="text-gray-700">
                我已阅读并同意
                <a href="#" class="text-blue-600 hover:text-blue-500">《用户协议》</a>
                和
                <a href="#" class="text-blue-600 hover:text-blue-500">《隐私政策》</a>
              </label>
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="registerError" class="bg-red-50 border border-red-200 rounded-lg p-3">
            <div class="flex">
              <ExclamationTriangleIcon class="h-5 w-5 text-red-400" />
              <div class="ml-3">
                <p class="text-sm text-red-800">{{ registerError }}</p>
              </div>
            </div>
          </div>

          <!-- 注册按钮 -->
          <div>
            <button
              type="submit"
              :disabled="loading || !form.agreeTos"
              class="w-full btn-primary py-3 text-lg relative disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="!loading">创建账户</span>
              <span v-else class="flex items-center justify-center">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                注册中...
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

          <!-- 第三方注册 -->
          <div class="grid grid-cols-2 gap-3">
            <button
              type="button"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 transition-colors"
            >
              <span class="text-lg mr-2">🐧</span>
              QQ注册
            </button>
            <button
              type="button"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 transition-colors"
            >
              <span class="text-lg mr-2">💬</span>
              微信注册
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  UserIcon, 
  LockClosedIcon, 
  AtSymbolIcon,
  EyeIcon, 
  EyeSlashIcon,
  ExclamationTriangleIcon 
} from '@heroicons/vue/24/outline'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const registerError = ref('')

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreeTos: false
})

const errors = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 邮箱验证正则
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// 表单验证
const validateForm = () => {
  errors.username = ''
  errors.email = ''
  errors.password = ''
  errors.confirmPassword = ''
  
  let isValid = true
  
  // 用户名验证
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    isValid = false
  } else if (form.username.length < 3 || form.username.length > 20) {
    errors.username = '用户名长度必须在3-20字符之间'
    isValid = false
  }
  
  // 邮箱验证
  if (!form.email.trim()) {
    errors.email = '请输入邮箱地址'
    isValid = false
  } else if (!emailRegex.test(form.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  }
  
  // 密码验证
  if (!form.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (form.password.length < 6 || form.password.length > 40) {
    errors.password = '密码长度必须在6-40字符之间'
    isValid = false
  }
  
  // 确认密码验证
  if (!form.confirmPassword) {
    errors.confirmPassword = '请确认密码'
    isValid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }
  
  return isValid
}

// 处理注册
const handleRegister = async () => {
  if (!validateForm()) return
  
  if (!form.agreeTos) {
    registerError.value = '请同意用户协议和隐私政策'
    return
  }
  
  try {
    loading.value = true
    registerError.value = ''
    
    const result = await authStore.register({
      username: form.username.trim(),
      email: form.email.trim(),
      password: form.password
    })
    
    if (result.success) {
      // 注册成功，跳转到首页
      router.push('/')
    } else {
      registerError.value = result.message
    }
  } catch (error) {
    registerError.value = '注册失败，请稍后重试'
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script> 