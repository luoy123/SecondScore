import request from '@/utils/request'

export interface SignupItem {
  id: number
  activityId: number
  studentId: number
  studentName?: string
  studentNo?: string
  applyTime: string
  reviewStatus: 'PENDING' | 'APPROVED' | 'REJECTED'
  reviewBy?: number
  reviewerName?: string
  reviewTime?: string
  rejectReason?: string
  signupStatus: 'ACTIVE' | 'CANCELLED'
}

export interface ParticipationItem {
  id: number
  activityId: number
  studentId: number
  studentName?: string
  studentNo?: string
  attendanceStatus: 'PRESENT' | 'ABSENT'
  markedBy?: number
  markerName?: string
  markedTime?: string
  remark?: string
}

export function signupActivityApi(activityId: number) {
  return request.post(`/activities/${activityId}/signups`)
}

export function cancelMySignupApi(activityId: number) {
  return request.delete(`/activities/${activityId}/signups/me`)
}

export function listMySignupsApi() {
  return request.get<any, SignupItem[]>('/signups/me')
}

export function listActivitySignupsApi(activityId: number) {
  return request.get<any, SignupItem[]>(`/activities/${activityId}/signups`)
}

export function approveSignupApi(signupId: number) {
  return request.post(`/signups/${signupId}/approve`)
}

export function rejectSignupApi(signupId: number, reason: string) {
  return request.post(`/signups/${signupId}/reject`, { reason })
}

export function markParticipationApi(activityId: number, items: Array<{ studentId: number; attendanceStatus: string; remark?: string }>) {
  return request.post(`/activities/${activityId}/participations/mark`, { items })
}

export function listParticipationApi(activityId: number) {
  return request.get<any, ParticipationItem[]>(`/activities/${activityId}/participations`)
}
