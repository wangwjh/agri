import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // 只有在用户原本已有token的情况下才跳转到登录页
      // 这样可以避免在新标签页或首次访问时不必要的跳转
      const hadToken = sessionStorage.getItem('token')
      if (hadToken) {
        // Token过期或无效，清除本地存储并跳转到登录页
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('user')
        
        // 只在不是当前就在登录页的情况下才跳转
        if (!window.location.pathname.includes('/login')) {
          window.location.href = '/login'
        }
      }
    }
    return Promise.reject(error)
  }
)

export default api 