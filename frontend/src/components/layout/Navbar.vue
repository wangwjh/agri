<template>
  <nav class="fixed top-0 left-0 right-0 z-50 glass-effect shadow-sm">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Logo和品牌名 -->
        <div class="flex items-center">
          <router-link to="/" class="flex items-center space-x-2">
            <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-sm">农</span>
            </div>
            <span class="text-xl font-bold text-gray-800 hidden sm:block">农业数据资产交易平台</span>
            <span class="text-lg font-bold text-gray-800 sm:hidden">农数平台</span>
          </router-link>
        </div>

        <!-- 导航菜单 -->
        <div class="hidden md:flex items-center space-x-8">
          <router-link 
            to="/" 
            class="text-gray-700 hover:text-blue-600 font-medium transition-colors"
            active-class="text-blue-600"
          >
            首页
          </router-link>
          <router-link 
            to="/search" 
            class="text-gray-700 hover:text-blue-600 font-medium transition-colors"
            active-class="text-blue-600"
          >
            搜索
          </router-link>
          
          <!-- 上传链接 -->
          <router-link 
            v-if="authStore.isAuthenticated"
            to="/upload" 
            class="text-gray-700 hover:text-green-600 font-medium transition-colors flex items-center space-x-1"
            active-class="text-green-600"
          >
            <CloudArrowUpIcon class="w-4 h-4" />
            <span>上传</span>
          </router-link>
          
          <!-- 交易信息下拉菜单 -->
          <div 
            v-if="authStore.isAuthenticated"
            class="relative"
            @mouseenter="handleTransactionMenuEnter"
            @mouseleave="handleTransactionMenuLeave"
          >
            <button 
              class="flex items-center space-x-1 text-gray-700 hover:text-blue-600 font-medium transition-colors"
              :class="{ 'text-blue-600': $route.path.startsWith('/transactions') }"
              @click="showTransactionMenu = !showTransactionMenu"
            >
              <span>交易信息</span>
              <ChevronDownIcon class="w-4 h-4" />
            </button>
            
            <!-- 下拉菜单 -->
            <div 
              v-show="showTransactionMenu"
              class="absolute top-full left-0 mt-1 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-50"
              @mouseenter="handleTransactionMenuEnter"
              @mouseleave="handleTransactionMenuLeave"
            >
              <router-link 
                to="/transactions?type=purchased"
                class="block px-4 py-2 text-gray-700 hover:bg-gray-50 transition-colors"
                @click="showTransactionMenu = false"
              >
                <div class="flex items-center space-x-2">
                  <ShoppingBagIcon class="w-4 h-4 text-blue-500" />
                  <span>已购买数据</span>
                </div>
              </router-link>
              <router-link 
                to="/transactions?type=uploaded"
                class="block px-4 py-2 text-gray-700 hover:bg-gray-50 transition-colors"
                @click="showTransactionMenu = false"
              >
                <div class="flex items-center space-x-2">
                  <CloudArrowUpIcon class="w-4 h-4 text-green-500" />
                  <span>已上传数据</span>
                </div>
              </router-link>
              <router-link 
                to="/transactions"
                class="block px-4 py-2 text-gray-700 hover:bg-gray-50 transition-colors"
                @click="showTransactionMenu = false"
              >
                <div class="flex items-center space-x-2">
                  <DocumentTextIcon class="w-4 h-4 text-purple-500" />
                  <span>所有交易</span>
                </div>
              </router-link>
            </div>
          </div>

          <!-- 账户管理 -->
          <router-link 
            to="/account"
            class="flex items-center space-x-1 text-gray-700 hover:text-blue-600 font-medium transition-colors"
            :class="{ 'text-blue-600': $route.path === '/account' }"
          >
            <CreditCardIcon class="w-5 h-5" />
            <span>账户</span>
          </router-link>
        </div>

        <!-- 用户操作区域 -->
        <div class="flex items-center space-x-4">
          <!-- 已登录状态 -->
          <div v-if="authStore.isAuthenticated" class="flex items-center space-x-3">
            <!-- 后台入口 -->
            <button 
              @click="goToAdmin"
              class="hidden sm:flex items-center space-x-1 px-3 py-2 bg-blue-100 text-blue-600 rounded-lg hover:bg-blue-200 transition-colors font-medium"
            >
              <CogIcon class="w-4 h-4" />
              <span>后台</span>
            </button>
            
            <div class="relative">
              <button 
                @click="showUserMenu = !showUserMenu"
                class="flex items-center space-x-2 p-2 rounded-lg hover:bg-white/50 transition-colors"
              >
                <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                  <span class="text-white text-sm font-medium">
                    {{ authStore.user?.username?.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <span class="text-gray-700 font-medium hidden sm:block">{{ authStore.user?.username }}</span>
                <ChevronDownIcon class="w-4 h-4 text-gray-500" />
              </button>
              
              <!-- 用户菜单下拉 -->
              <div 
                v-show="showUserMenu"
                class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-2"
                @click="showUserMenu = false"
              >
                <router-link 
                  to="/transactions"
                  class="flex items-center space-x-2 px-4 py-2 text-gray-700 hover:bg-gray-50"
                >
                  <DocumentTextIcon class="w-4 h-4" />
                  <span>我的交易</span>
                </router-link>
                <router-link 
                  to="/profile"
                  class="flex items-center space-x-2 px-4 py-2 text-gray-700 hover:bg-gray-50"
                >
                  <UserIcon class="w-4 h-4" />
                  <span>个人资料</span>
                </router-link>
                <!-- 后台入口（移动端） -->
                <button 
                  @click="goToAdmin"
                  class="sm:hidden flex items-center space-x-2 w-full text-left px-4 py-2 text-blue-600 hover:bg-blue-50"
                >
                  <CogIcon class="w-4 h-4" />
                  <span>后台</span>
                </button>
                <div class="border-t border-gray-100 my-1"></div>
                <button 
                  @click="handleLogout"
                  class="flex items-center space-x-2 w-full text-left px-4 py-2 text-red-600 hover:bg-gray-50"
                >
                  <ArrowRightOnRectangleIcon class="w-4 h-4" />
                  <span>退出登录</span>
                </button>
              </div>
            </div>
          </div>

          <!-- 未登录状态 -->
          <div v-else class="flex items-center space-x-3">
            <!-- 后台入口 -->
            <button 
              @click="goToAdmin"
              class="hidden sm:flex items-center space-x-1 px-3 py-2 bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 transition-colors text-sm"
            >
              <CogIcon class="w-4 h-4" />
              <span>后台</span>
            </button>
            
            <router-link 
              to="/login"
              class="btn-secondary text-sm"
            >
              登录
            </router-link>
            <router-link 
              to="/register"
              class="btn-primary text-sm"
            >
              注册
            </router-link>
          </div>

          <!-- 移动端菜单按钮 -->
          <button 
            @click="showMobileMenu = !showMobileMenu"
            class="md:hidden p-2 rounded-lg hover:bg-white/50"
          >
            <Bars3Icon v-if="!showMobileMenu" class="w-6 h-6 text-gray-700" />
            <XMarkIcon v-else class="w-6 h-6 text-gray-700" />
          </button>
        </div>
      </div>
    </div>

    <!-- 移动端菜单 -->
    <div 
      v-show="showMobileMenu"
      class="md:hidden bg-white border-t border-gray-200"
    >
      <div class="px-4 py-2 space-y-1">
        <router-link 
          to="/" 
          @click="showMobileMenu = false"
          class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-gray-100"
        >
          首页
        </router-link>
        <router-link 
          to="/search" 
          @click="showMobileMenu = false"
          class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-gray-100"
        >
          搜索
        </router-link>
        
        <!-- 移动端上传链接 -->
        <router-link 
          v-if="authStore.isAuthenticated"
          to="/upload" 
          @click="showMobileMenu = false"
          class="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-700 hover:bg-gray-100"
        >
          <CloudArrowUpIcon class="w-4 h-4 text-green-500" />
          <span>上传</span>
        </router-link>
        
        <!-- 移动端交易信息菜单 -->
        <div v-if="authStore.isAuthenticated">
          <button 
            @click="showMobileTransactionMenu = !showMobileTransactionMenu"
            class="flex items-center justify-between w-full px-3 py-2 rounded-lg text-gray-700 hover:bg-gray-100"
          >
            <span>交易信息</span>
            <ChevronDownIcon 
              class="w-4 h-4 transition-transform"
              :class="{ 'rotate-180': showMobileTransactionMenu }"
            />
          </button>
          
          <div v-show="showMobileTransactionMenu" class="ml-4 mt-1 space-y-1">
            <router-link 
              to="/transactions?type=purchased"
              @click="showMobileMenu = false"
              class="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-600 hover:bg-gray-100"
            >
              <ShoppingBagIcon class="w-4 h-4 text-blue-500" />
              <span>已购买数据</span>
            </router-link>
            <router-link 
              to="/transactions?type=uploaded"
              @click="showMobileMenu = false"
              class="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-600 hover:bg-gray-100"
            >
              <CloudArrowUpIcon class="w-4 h-4 text-green-500" />
              <span>已上传数据</span>
            </router-link>
            <router-link 
              to="/transactions"
              @click="showMobileMenu = false"
              class="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-600 hover:bg-gray-100"
            >
              <DocumentTextIcon class="w-4 h-4 text-purple-500" />
              <span>所有交易</span>
            </router-link>
          </div>
        </div>

        <!-- 账户管理 -->
        <router-link 
          to="/account"
          @click="showMobileMenu = false"
          class="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-700 hover:bg-gray-100"
        >
          <CreditCardIcon class="w-5 h-5 text-orange-500" />
          <span>账户管理</span>
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  ChevronDownIcon, 
  Bars3Icon, 
  XMarkIcon,
  ShoppingBagIcon,
  CloudArrowUpIcon,
  DocumentTextIcon,
  UserIcon,
  ArrowRightOnRectangleIcon,
  CogIcon,
  CreditCardIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()
const authStore = useAuthStore()

const showUserMenu = ref(false)
const showMobileMenu = ref(false)
const showTransactionMenu = ref(false)
const showMobileTransactionMenu = ref(false)

// 交易菜单定时器
let transactionMenuTimer = null

// 检查是否是管理员
const isAdmin = computed(() => {
  const adminRoles = ['REVIEWER', 'PUBLISHER', 'OPERATOR', 'SUPER_ADMIN']
  return authStore.user?.role && adminRoles.includes(authStore.user.role)
})

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}

// 快速管理员登录
const quickAdminLogin = async () => {
  try {
    const result = await authStore.login({
      username: 'admin',
      password: 'admin123'
    })
    
    if (result.success) {
      // 登录成功后直接跳转到管理后台
      router.push('/admin')
    } else {
      alert('管理员登录失败：' + result.message)
    }
  } catch (error) {
    console.error('管理员登录失败:', error)
    alert('管理员登录失败，请稍后重试')
  }
}

// 后台入口处理
const goToAdmin = () => {
  if (!authStore.isAuthenticated) {
    // 未登录用户，提示需要先登录
    alert('请先登录后再访问后台管理系统。')
  } else if (isAdmin.value) {
    // 已登录且是管理员，直接跳转
    router.push('/admin')
  } else {
    // 已登录但不是管理员，提示权限不足
    alert('抱歉，您没有访问后台的权限。只有管理员用户才能访问后台管理系统。')
  }
}

// 点击外部关闭菜单
const handleClickOutside = (event) => {
  if (!event.target.closest('.relative')) {
    showUserMenu.value = false
  }
}

// 交易菜单鼠标进入处理
const handleTransactionMenuEnter = () => {
  // 清除可能存在的定时器
  if (transactionMenuTimer) {
    clearTimeout(transactionMenuTimer)
    transactionMenuTimer = null
  }
  showTransactionMenu.value = true
}

// 交易菜单鼠标离开处理
const handleTransactionMenuLeave = () => {
  // 设置延时关闭，给用户300ms时间移动鼠标到菜单项
  transactionMenuTimer = setTimeout(() => {
    showTransactionMenu.value = false
    transactionMenuTimer = null
  }, 300)
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  // 清理交易菜单定时器
  if (transactionMenuTimer) {
    clearTimeout(transactionMenuTimer)
    transactionMenuTimer = null
  }
})
</script> 