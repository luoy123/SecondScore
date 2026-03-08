import request from '@/utils/request'

export interface CreditGrantResult {
  grantedCount: number
  skippedCount: number
  messages: string[]
}

export interface CreditRecordItem {
  id: number
  studentId: number
  studentName?: string
  studentNo?: string
  activityId: number
  activityTitle?: string
  termId: number
  termName?: string
  categoryId: number
  categoryName?: string
  credit: number
  grantStatus: 'GRANTED' | 'REVOKED'
  grantBy?: number
  grantByName?: string
  grantTime?: string
  revokeBy?: number
  revokeByName?: string
  revokeTime?: string
  revokeReason?: string
}

export interface PersonalCreditSummary {
  studentId: number
  studentName?: string
  termId?: number
  termName?: string
  totalCredit: number
}

export function grantCreditByActivityApi(activityId: number) {
  return request.post<any, CreditGrantResult>(`/activities/${activityId}/credits/grant`)
}

export function revokeCreditApi(id: number, reason: string) {
  return request.post(`/credit-records/${id}/revoke`, { reason })
}

export function listCreditRecordsApi(params?: { studentId?: number; termId?: number }) {
  return request.get<any, CreditRecordItem[]>('/credit-records', { params })
}

export function getPersonalCreditSummaryApi(params?: { studentId?: number; termId?: number }) {
  return request.get<any, PersonalCreditSummary>('/credit-summary/personal', { params })
}
