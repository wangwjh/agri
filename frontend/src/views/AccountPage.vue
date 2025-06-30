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
            <h1 class="text-3xl font-bold text-gray-900">账户管理</h1>
            <p class="mt-2 text-gray-600">管理您的账户余额和交易设置</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- 左侧：余额信息 -->
        <div class="lg:col-span-2 space-y-6">
          <!-- 余额概览卡片 -->
          <div 
            class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg p-8 text-white"
            v-motion
            :initial="{ opacity: 0, y: 20 }"
            :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 200 } }"
          >
            <div class="flex items-center justify-between mb-6">
              <div>
                <h2 class="text-2xl font-bold">账户余额</h2>
                <p class="text-blue-100">余额信息更新于 {{ formatTime(new Date()) }}</p>
              </div>
              <div class="p-3 bg-white/20 rounded-lg">
                <CreditCardIcon class="w-8 h-8" />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              <!-- 总余额 -->
              <div class="text-center">
                <div class="text-3xl font-bold mb-2">
                  ¥{{ balanceInfo?.totalBalance?.toLocaleString() || '0' }}
                </div>
                <div class="text-blue-100">总余额</div>
              </div>

              <!-- 可用余额 -->
              <div class="text-center">
                <div class="text-3xl font-bold mb-2">
                  ¥{{ balanceInfo?.availableBalance?.toLocaleString() || '0' }}
                </div>
                <div class="text-blue-100">可用余额</div>
              </div>

              <!-- 冻结余额 -->
              <div class="text-center">
                <div class="text-3xl font-bold mb-2">
                  ¥{{ balanceInfo?.frozenBalance?.toLocaleString() || '0' }}
                </div>
                <div class="text-blue-100">冻结余额</div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="flex flex-wrap gap-4 mt-8">
              <button 
                @click="showRechargeModal = true"
                class="flex items-center space-x-2 px-6 py-3 bg-white/20 hover:bg-white/30 rounded-lg transition-colors"
              >
                <PlusIcon class="w-5 h-5" />
                <span>充值</span>
              </button>
              
              <button 
                @click="refreshBalance"
                :disabled="refreshing"
                class="flex items-center space-x-2 px-6 py-3 bg-white/20 hover:bg-white/30 rounded-lg transition-colors disabled:opacity-50"
              >
                <ArrowPathIcon class="w-5 h-5" :class="{ 'animate-spin': refreshing }" />
                <span>刷新</span>
              </button>
            </div>
          </div>

          <!-- 用户信息卡片 -->
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-100 p-6"
            v-motion
            :initial="{ opacity: 0, y: 20 }"
            :enter="{ opacity: 1, y: 0, transition: { duration: 600, delay: 400 } }"
          >
            <h3 class="text-lg font-semibold text-gray-900 mb-4">个人信息</h3>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6" v-if="userInfo">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
                <div class="text-gray-900">{{ userInfo.username }}</div>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
                <div class="text-gray-900">{{ userInfo.email }}</div>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">角色</label>
                <div class="flex items-center space-x-2">
                  <span :class="getRoleStyle(userInfo.role)" class="px-2 py-1 rounded text-sm font-medium">
                    {{ getRoleLabel(userInfo.role) }}
                  </span>
                </div>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
                <div class="flex items-center space-x-2">
                  <span :class="getStatusStyle(userInfo.status)" class="px-2 py-1 rounded text-sm font-medium">
                    {{ getStatusLabel(userInfo.status) }}
                  </span>
                </div>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">注册时间</label>
                <div class="text-gray-900">{{ formatDate(userInfo.createTime) }}</div>
              </div>
              
              <div v-if="userInfo.lastLoginTime">
                <label class="block text-sm font-medium text-gray-700 mb-1">最后登录</label>
                <div class="text-gray-900">{{ formatDate(userInfo.lastLoginTime) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：快捷操作 -->
        <div class="space-y-6">
          <!-- 快捷充值 -->
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-100 p-6"
            v-motion
            :initial="{ opacity: 0, x: 20 }"
            :enter="{ opacity: 1, x: 0, transition: { duration: 600, delay: 300 } }"
          >
            <h3 class="text-lg font-semibold text-gray-900 mb-4">快捷充值</h3>
            
            <div class="grid grid-cols-2 gap-3 mb-4">
              <button 
                v-for="amount in quickRechargeAmounts" 
                :key="amount"
                @click="quickRecharge(amount)"
                class="p-3 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors text-center"
              >
                <div class="text-lg font-semibold">¥{{ amount }}</div>
              </button>
            </div>
            
            <button 
              @click="showRechargeModal = true"
              class="w-full btn-primary"
            >
              自定义金额充值
            </button>
          </div>

          <!-- 账户统计 -->
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-100 p-6"
            v-motion
            :initial="{ opacity: 0, x: 20 }"
            :enter="{ opacity: 1, x: 0, transition: { duration: 600, delay: 500 } }"
          >
            <h3 class="text-lg font-semibold text-gray-900 mb-4">账户统计</h3>
            
            <div class="space-y-4">
              <div class="flex justify-between items-center">
                <span class="text-gray-600">本月消费</span>
                <span class="font-semibold">¥0</span>
              </div>
              
              <div class="flex justify-between items-center">
                <span class="text-gray-600">累计充值</span>
                <span class="font-semibold">¥0</span>
              </div>
              
              <div class="flex justify-between items-center">
                <span class="text-gray-600">交易次数</span>
                <span class="font-semibold">0</span>
              </div>
            </div>
          </div>

          <!-- 快速链接 -->
          <div 
            class="bg-white rounded-xl shadow-sm border border-gray-100 p-6"
            v-motion
            :initial="{ opacity: 0, x: 20 }"
            :enter="{ opacity: 1, x: 0, transition: { duration: 600, delay: 700 } }"
          >
            <h3 class="text-lg font-semibold text-gray-900 mb-4">快速链接</h3>
            
            <div class="space-y-2">
              <router-link 
                to="/transactions"
                class="flex items-center space-x-3 p-3 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <DocumentTextIcon class="w-5 h-5 text-blue-500" />
                <span>交易记录</span>
              </router-link>
              
              <router-link 
                to="/search"
                class="flex items-center space-x-3 p-3 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <MagnifyingGlassIcon class="w-5 h-5 text-green-500" />
                <span>浏览数据</span>
              </router-link>
              
              <router-link 
                to="/upload"
                class="flex items-center space-x-3 p-3 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <CloudArrowUpIcon class="w-5 h-5 text-purple-500" />
                <span>上传数据</span>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 充值模态框 -->
  <div v-if="showRechargeModal" class="fixed inset-0 z-50 overflow-y-auto">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="showRechargeModal = false"></div>
      
      <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
        <div>
          <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-green-100">
            <CreditCardIcon class="h-6 w-6 text-green-600" />
          </div>
          
          <div class="mt-3 text-center sm:mt-5">
            <h3 class="text-lg leading-6 font-medium text-gray-900">
              账户充值
            </h3>
            <div class="mt-2">
              <p class="text-sm text-gray-500">
                选择充值金额和支付方式
              </p>
            </div>
          </div>
        </div>

        <div class="mt-6 space-y-4">
          <!-- 充值金额 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">充值金额</label>
            <div class="relative">
              <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">¥</span>
              <input 
                v-model="rechargeForm.amount" 
                type="number" 
                step="0.01"
                min="1"
                max="10000"
                placeholder="请输入充值金额"
                class="block w-full pl-8 pr-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
              >
            </div>
            <p class="mt-1 text-xs text-gray-500">单次充值金额限制：1-10,000 元</p>
          </div>

          <!-- 支付方式 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">支付方式</label>
            <div class="grid grid-cols-2 gap-3">
              <button 
                v-for="method in paymentMethods" 
                :key="method.value"
                @click="rechargeForm.paymentMethod = method.value"
                :class="[
                  'flex items-center justify-center space-x-2 p-3 border rounded-lg transition-colors',
                  rechargeForm.paymentMethod === method.value 
                    ? 'border-blue-500 bg-blue-50 text-blue-700' 
                    : 'border-gray-200 hover:border-gray-300'
                ]"
              >
                <component :is="method.icon" class="w-5 h-5" />
                <span>{{ method.label }}</span>
              </button>
            </div>
          </div>
        </div>

        <div class="mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense">
          <button
            @click="submitRecharge"
            :disabled="recharging || !rechargeForm.amount || rechargeForm.amount <= 0"
            class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-green-600 text-base font-medium text-white hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 sm:col-start-2 sm:text-sm disabled:opacity-50"
          >
            <span v-if="!recharging">确认充值</span>
            <span v-else class="flex items-center space-x-2">
              <ArrowPathIcon class="w-4 h-4 animate-spin" />
              <span>处理中...</span>
            </span>
          </button>
          <button
            @click="showRechargeModal = false"
            class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:col-start-1 sm:text-sm"
          >
            取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  CreditCardIcon,
  PlusIcon,
  ArrowPathIcon,
  DocumentTextIcon,
  MagnifyingGlassIcon,
  CloudArrowUpIcon
} from '@heroicons/vue/24/outline'
import { CreditCardIcon as CreditCardSolid } from '@heroicons/vue/24/solid'
import api from '@/services/api'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const refreshing = ref(false)
const recharging = ref(false)
const showRechargeModal = ref(false)
const balanceInfo = ref(null)
const userInfo = ref(null)

// 快捷充值金额
const quickRechargeAmounts = [50, 100, 200, 500, 1000, 2000]

// 支付方式
const paymentMethods = [
  { value: '支付宝', label: '支付宝', icon: CreditCardSolid },
  { value: '微信支付', label: '微信支付', icon: CreditCardSolid }
]

// 充值表单
const rechargeForm = reactive({
  amount: '',
  paymentMethod: '支付宝'
})

// 获取账户信息
const fetchAccountInfo = async () => {
  try {
    loading.value = true
    
    // 获取用户信息（包含余额）
    const response = await api.get('/account/info')
    if (response.data.success) {
      userInfo.value = response.data.data
      balanceInfo.value = response.data.data.balance
    }
  } catch (error) {
    console.error('获取账户信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 刷新余额
const refreshBalance = async () => {
  try {
    refreshing.value = true
    
    const response = await api.get('/account/balance')
    if (response.data.success) {
      balanceInfo.value = response.data.data
    }
  } catch (error) {
    console.error('刷新余额失败:', error)
  } finally {
    refreshing.value = false
  }
}

// 快捷充值
const quickRecharge = (amount) => {
  rechargeForm.amount = amount
  showRechargeModal.value = true
}

// 提交充值
const submitRecharge = async () => {
  try {
    recharging.value = true
    
    const response = await api.post('/account/recharge', {
      amount: parseFloat(rechargeForm.amount),
      paymentMethod: rechargeForm.paymentMethod
    })
    
    if (response.data.success) {
      showRechargeModal.value = false
      rechargeForm.amount = ''
      
      // 刷新余额信息
      await fetchAccountInfo()
      
      // 显示成功消息
      alert('充值成功！')
    } else {
      alert('充值失败：' + response.data.message)
    }
  } catch (error) {
    console.error('充值失败:', error)
    alert('充值失败，请稍后重试')
  } finally {
    recharging.value = false
  }
}

// 工具函数
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatTime = (date) => {
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getRoleLabel = (role) => {
  const labels = {
    'USER': '普通用户',
    'REVIEWER': '审核管理员',
    'PUBLISHER': '发布管理员',
    'OPERATOR': '运营管理员',
    'SUPER_ADMIN': '超级管理员'
  }
  return labels[role] || role
}

const getRoleStyle = (role) => {
  const styles = {
    'USER': 'bg-gray-100 text-gray-700',
    'REVIEWER': 'bg-blue-100 text-blue-700',
    'PUBLISHER': 'bg-green-100 text-green-700',
    'OPERATOR': 'bg-purple-100 text-purple-700',
    'SUPER_ADMIN': 'bg-red-100 text-red-700'
  }
  return styles[role] || 'bg-gray-100 text-gray-700'
}

const getStatusLabel = (status) => {
  const labels = {
    'ACTIVE': '正常',
    'SUSPENDED': '暂停',
    'BANNED': '禁用'
  }
  return labels[status] || status
}

const getStatusStyle = (status) => {
  const styles = {
    'ACTIVE': 'bg-green-100 text-green-700',
    'SUSPENDED': 'bg-yellow-100 text-yellow-700',
    'BANNED': 'bg-red-100 text-red-700'
  }
  return styles[status] || 'bg-gray-100 text-gray-700'
}

onMounted(() => {
  fetchAccountInfo()
})
</script> 