import request from '@/utils/request'

export interface CollegeItem {
  id: number
  collegeCode: string
  collegeName: string
  status: number
}

export interface MajorItem {
  id: number
  collegeId: number
  majorCode: string
  majorName: string
  status: number
}

export interface ClassItem {
  id: number
  majorId: number
  className: string
  grade: number
  status: number
}

export interface TermItem {
  id: number
  termName: string
  startDate: string
  endDate: string
  isCurrent: number
  status: number
}

export interface CategoryItem {
  id: number
  categoryName: string
  defaultCredit: number
  maxCreditPerTerm: number
  status: number
}

export function listCollegesApi() {
  return request.get<any, CollegeItem[]>('/base/colleges')
}

export function createCollegeApi(data: { collegeCode: string; collegeName: string; status: number }) {
  return request.post<any, number>('/base/colleges', data)
}

export function updateCollegeApi(id: number, data: { collegeCode: string; collegeName: string; status: number }) {
  return request.put(`/base/colleges/${id}`, data)
}

export function listMajorsApi(collegeId?: number) {
  return request.get<any, MajorItem[]>('/base/majors', { params: { collegeId } })
}

export function createMajorApi(data: { collegeId: number; majorCode: string; majorName: string; status: number }) {
  return request.post<any, number>('/base/majors', data)
}

export function updateMajorApi(id: number, data: { collegeId: number; majorCode: string; majorName: string; status: number }) {
  return request.put(`/base/majors/${id}`, data)
}

export function listClassesApi(majorId?: number) {
  return request.get<any, ClassItem[]>('/base/classes', { params: { majorId } })
}

export function createClassApi(data: { majorId: number; className: string; grade: number; status: number }) {
  return request.post<any, number>('/base/classes', data)
}

export function updateClassApi(id: number, data: { majorId: number; className: string; grade: number; status: number }) {
  return request.put(`/base/classes/${id}`, data)
}

export function listTermsApi() {
  return request.get<any, TermItem[]>('/base/terms')
}

export function createTermApi(data: { termName: string; startDate: string; endDate: string; isCurrent: number; status: number }) {
  return request.post<any, number>('/base/terms', data)
}

export function updateTermApi(id: number, data: { termName: string; startDate: string; endDate: string; isCurrent: number; status: number }) {
  return request.put(`/base/terms/${id}`, data)
}

export function listCategoriesApi() {
  return request.get<any, CategoryItem[]>('/base/activity-categories')
}

export function createCategoryApi(data: { categoryName: string; defaultCredit: number; maxCreditPerTerm: number; status: number }) {
  return request.post<any, number>('/base/activity-categories', data)
}

export function updateCategoryApi(id: number, data: { categoryName: string; defaultCredit: number; maxCreditPerTerm: number; status: number }) {
  return request.put(`/base/activity-categories/${id}`, data)
}
