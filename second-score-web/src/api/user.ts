import request from '@/utils/request'

export interface RoleItem {
  id: number
  roleCode: string
  roleName: string
  status?: number
}

export interface UserItem {
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
}

export function listUsersApi(params?: { keyword?: string; roleCode?: string; status?: number }) {
  return request.get<any, UserItem[]>('/users', { params })
}

export function listRolesApi() {
  return request.get<any, RoleItem[]>('/users/roles')
}

export function createUserApi(data: {
  username: string
  password: string
  realName: string
  studentNo?: string
  phone?: string
  email?: string
  collegeId?: number
  majorId?: number
  classId?: number
}) {
  return request.post<any, number>('/users', data)
}

export function updateUserApi(id: number, data: {
  realName: string
  phone?: string
  email?: string
  collegeId?: number
  majorId?: number
  classId?: number
}) {
  return request.put(`/users/${id}`, data)
}

export function updateUserStatusApi(id: number, status: number) {
  return request.patch(`/users/${id}/status`, { status })
}

export function assignUserRolesApi(id: number, roleIds: number[]) {
  return request.put(`/users/${id}/roles`, { roleIds })
}
