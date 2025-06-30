import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(sessionStorage.getItem('user') || 'null'))
  const token = ref(sessionStorage.getItem('token'))

  const isAuthenticated = computed(() => {
    const hasToken = !!token.value
    const hasUser = !!user.value
    const hasValidUser = user.value && user.value.username
    
    return hasToken && hasUser && hasValidUser
  })

  const login = async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials)
      if (response.data.success) {
        token.value = response.data.data.token
        user.value = response.data.data.user
        sessionStorage.setItem('token', token.value)
        sessionStorage.setItem('user', JSON.stringify(user.value))
        
        // 设置axios默认Authorization头
        api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
        
        return { success: true, message: response.data.message }
      }
      return { success: false, message: response.data.message }
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || '登录失败' 
      }
    }
  }

  const register = async (userData) => {
    try {
      const response = await api.post('/auth/register', userData)
      if (response.data.success) {
        token.value = response.data.data.token
        user.value = response.data.data.user
        sessionStorage.setItem('token', token.value)
        sessionStorage.setItem('user', JSON.stringify(user.value))
        
        // 设置axios默认Authorization头
        api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
        
        return { success: true, message: response.data.message }
      }
      return { success: false, message: response.data.message }
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || '注册失败' 
      }
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('user')
    delete api.defaults.headers.common['Authorization']
  }

  const fetchCurrentUser = async () => {
    if (!token.value) return
    
    try {
      const response = await api.get('/auth/me')
      if (response.data.success) {
        user.value = response.data.data
        sessionStorage.setItem('user', JSON.stringify(user.value))
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      logout()
    }
  }

  // 初始化时设置token
  if (token.value) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
    if (!user.value) {
      fetchCurrentUser()
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    register,
    logout,
    fetchCurrentUser
  }
}) 