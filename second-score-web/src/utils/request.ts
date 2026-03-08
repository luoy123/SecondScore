import axios, { type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

import { clearProfileCache, clearToken, getToken } from './token'

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 20000
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    const payload = response.data
    if (payload.code === 200) {
      return payload.data
    }
    ElMessage.error(payload.message || '请求失败')
    return Promise.reject(new Error(payload.message || '请求失败'))
  },
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      clearToken()
      clearProfileCache()
      if (!location.hash.includes('/login') && location.pathname !== '/login') {
        ElMessage.error('登录已失效，请重新登录')
        window.location.href = '/login'
      }
    } else {
      ElMessage.error(error?.response?.data?.message || error?.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
