import request from '@/utils/request'

export interface PermissionItem {
  id: number
  permCode: string
  permName: string
  permType: 'MENU' | 'API'
  status: number
}

export interface RoleManageItem {
  id: number
  roleCode: string
  roleName: string
  status: number
}

export interface RoleSavePayload {
  roleCode: string
  roleName: string
  status: number
}

export function listRoleManageApi() {
  return request.get<any, RoleManageItem[]>('/roles')
}

export function createRoleApi(data: RoleSavePayload) {
  return request.post<any, number>('/roles', data)
}

export function updateRoleApi(id: number, data: RoleSavePayload) {
  return request.put(`/roles/${id}`, data)
}

export function listPermissionsApi() {
  return request.get<any, PermissionItem[]>('/permissions')
}

export function listRolePermissionIdsApi(roleId: number) {
  return request.get<any, number[]>(`/roles/${roleId}/permissions`)
}

export function assignRolePermissionsApi(roleId: number, permissionIds: number[]) {
  return request.put(`/roles/${roleId}/permissions`, { permissionIds })
}
