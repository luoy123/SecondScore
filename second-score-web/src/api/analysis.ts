import request from '@/utils/request'

export interface DashboardData {
  totalActivities: number
  totalSignups: number
  totalGrantedCredit: number
  totalCreditedStudents: number
}

export interface TermTrendItem {
  termId: number
  termName: string
  totalCredit: number
}

export interface CategoryDistributionItem {
  categoryId: number
  categoryName: string
  totalCredit: number
}

export interface ActivityRankingItem {
  activityId: number
  activityTitle: string
  participantCount: number
  totalCredit: number
}

export function getDashboardApi() {
  return request.get<any, DashboardData>('/analysis/dashboard')
}

export function getTermTrendApi() {
  return request.get<any, TermTrendItem[]>('/analysis/term-trend')
}

export function getCategoryDistributionApi(termId?: number) {
  return request.get<any, CategoryDistributionItem[]>('/analysis/category-distribution', { params: { termId } })
}

export function getActivityRankingApi(params?: { termId?: number; topN?: number }) {
  return request.get<any, ActivityRankingItem[]>('/analysis/activity-ranking', { params })
}
