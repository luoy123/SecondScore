import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
  captchaId: string
  captchaCode: string
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  realName: string
  roles: string[]
  permissions: string[]
}

export interface UserProfile {
  id: number
  username: string
  realName: string
  studentNo?: string
  avatarUrl?: string
  phone?: string
  email?: string
  collegeId?: number
  majorId?: number
  classId?: number
  status: number
  roles: string[]
  permissions: string[]
}

export interface CaptchaResult {
  captchaId: string
  imageBase64: string
  expireSeconds: number
}

export interface AvatarUploadResult {
  avatarUrl: string
}

export function loginApi(data: LoginParams) {
  return request.post<any, LoginResult>('/auth/login', data)
}

export function getCaptchaApi() {
  return request.get<any, CaptchaResult>('/auth/captcha')
}

export function meApi() {
  return request.get<any, UserProfile>('/auth/me')
}

export function changePasswordApi(data: { oldPassword: string; newPassword: string }) {
  return request.post('/auth/change-password', data)
}

export function uploadAvatarApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, AvatarUploadResult>('/profile/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
