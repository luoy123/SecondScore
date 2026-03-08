import request from '@/utils/request'

export interface ActivityItem {
  id: number
  title: string
  categoryId: number
  categoryName?: string
  termId: number
  termName?: string
  organizerId: number
  organizerName?: string
  location?: string
  startTime: string
  endTime: string
  signupStart: string
  signupEnd: string
  capacity: number
  signupCount?: number
  credit: number
  status: 'DRAFT' | 'PUBLISHED' | 'FINISHED' | 'CANCELLED'
  description?: string
}

export interface ActivitySaveParams {
  title: string
  categoryId: number
  termId: number
  organizerId?: number
  location?: string
  startTime: string
  endTime: string
  signupStart: string
  signupEnd: string
  capacity: number
  credit: number
  description?: string
}

export function listActivitiesApi(params?: { title?: string; termId?: number; status?: string }) {
  return request.get<any, ActivityItem[]>('/activities', { params })
}

export function getActivityApi(id: number) {
  return request.get<any, ActivityItem>(`/activities/${id}`)
}

export function createActivityApi(data: ActivitySaveParams) {
  return request.post<any, number>('/activities', data)
}

export function updateActivityApi(id: number, data: ActivitySaveParams) {
  return request.put(`/activities/${id}`, data)
}

export function publishActivityApi(id: number) {
  return request.post(`/activities/${id}/publish`)
}

export function finishActivityApi(id: number) {
  return request.post(`/activities/${id}/finish`)
}
