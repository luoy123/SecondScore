const activityStatusMap: Record<string, string> = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  FINISHED: '已完结'
}

const reviewStatusMap: Record<string, string> = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝'
}

const signupStatusMap: Record<string, string> = {
  ACTIVE: '有效报名',
  CANCELLED: '已取消'
}

const grantStatusMap: Record<string, string> = {
  GRANTED: '已发放',
  REVOKED: '已撤销'
}

export function activityStatusLabel(status?: string) {
  if (!status) return '-'
  return activityStatusMap[status] || status
}

export function reviewStatusLabel(status?: string) {
  if (!status) return '-'
  return reviewStatusMap[status] || status
}

export function signupStatusLabel(status?: string) {
  if (!status) return '-'
  return signupStatusMap[status] || status
}

export function grantStatusLabel(status?: string) {
  if (!status) return '-'
  return grantStatusMap[status] || status
}
