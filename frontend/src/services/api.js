import axios from 'axios'

// 根据环境变量决定API基础URL
const getBaseURL = () => {
  // 在生产环境中使用环境变量，开发环境使用代理
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL
  }
  return '/api'
}

const api = axios.create({
  baseURL: getBaseURL(),
  timeout: 60000, // 增加到60秒，适合一般请求
  headers: {
    'Content-Type': 'application/json'
  }
})

// 创建专用于文件上传的API实例，具有更长的超时时间
const uploadApi = axios.create({
  baseURL: getBaseURL(),
  timeout: 1800000, // 30分钟超时，适用于超大文件上传
  headers: {
    'Content-Type': 'multipart/form-data'
  },
  // 增加最大内容长度限制
  maxContentLength: 5 * 1024 * 1024 * 1024, // 5GB
  maxBodyLength: 5 * 1024 * 1024 * 1024, // 5GB
})

// 创建专用于文件下载的API实例
const downloadApi = axios.create({
  baseURL: getBaseURL(),
  timeout: 1800000, // 30分钟超时，适用于大文件下载
  headers: {
    'Content-Type': 'application/json'
  },
  // 增加最大内容长度限制
  maxContentLength: 5 * 1024 * 1024 * 1024, // 5GB
  maxBodyLength: 5 * 1024 * 1024 * 1024, // 5GB
})

// 请求拦截器 - 普通API
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

// 请求拦截器 - 文件上传API
uploadApi.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 对于文件上传，移除Content-Type让浏览器自动设置boundary
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 请求拦截器 - 文件下载API
downloadApi.interceptors.request.use(
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

// 响应拦截器处理函数
const handleResponseError = (error) => {
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

// 响应拦截器 - 普通API
api.interceptors.response.use(
  (response) => {
    return response
  },
  handleResponseError
)

// 响应拦截器 - 文件上传API
uploadApi.interceptors.response.use(
  (response) => {
    return response
  },
  handleResponseError
)

// 响应拦截器 - 文件下载API
downloadApi.interceptors.response.use(
  (response) => {
    return response
  },
  handleResponseError
)

export default api
export { uploadApi, downloadApi }
