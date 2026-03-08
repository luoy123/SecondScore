import type { RouteRecordRaw } from 'vue-router'

export interface AppRouteMeta {
  title: string
  icon?: string
  perm?: string
  hideInMenu?: boolean
}

export type AppRouteRecord = RouteRecordRaw & { meta: AppRouteMeta }

export const appRoutes: AppRouteRecord[] = [
  {
    path: '/student/hall',
    name: 'StudentHall',
    component: () => import('@/views/student/ActivityHallView.vue'),
    meta: { title: '活动大厅', icon: 'Calendar', perm: 'menu:student:hall' }
  },
  {
    path: '/student/signups',
    name: 'MySignups',
    component: () => import('@/views/student/MySignupView.vue'),
    meta: { title: '我的报名', icon: 'Tickets', perm: 'menu:student:signups' }
  },
  {
    path: '/student/credits',
    name: 'MyCredits',
    component: () => import('@/views/student/MyCreditView.vue'),
    meta: { title: '我的学分', icon: 'Medal', perm: 'menu:student:credits' }
  },
  {
    path: '/admin/analysis',
    name: 'Analysis',
    component: () => import('@/views/admin/AnalysisView.vue'),
    meta: { title: '数据分析', icon: 'DataAnalysis', perm: 'menu:admin:analysis' }
  },
  {
    path: '/admin/activities',
    name: 'ActivityManage',
    component: () => import('@/views/admin/ActivityManageView.vue'),
    meta: { title: '活动管理', icon: 'Notebook', perm: 'menu:admin:activities' }
  },
  {
    path: '/admin/signup-review',
    name: 'SignupReview',
    component: () => import('@/views/admin/SignupReviewView.vue'),
    meta: { title: '报名审核', icon: 'DocumentChecked', perm: 'menu:admin:signup-review' }
  },
  {
    path: '/admin/participation',
    name: 'ParticipationMark',
    component: () => import('@/views/admin/ParticipationMarkView.vue'),
    meta: { title: '参与登记', icon: 'Checked', perm: 'menu:admin:participation' }
  },
  {
    path: '/admin/credit-grant',
    name: 'CreditGrant',
    component: () => import('@/views/admin/CreditGrantView.vue'),
    meta: { title: '学分发放', icon: 'Promotion', perm: 'menu:admin:credit-grant' }
  },
  {
    path: '/system/users',
    name: 'UserManage',
    component: () => import('@/views/system/UserManageView.vue'),
    meta: { title: '用户管理', icon: 'User', perm: 'menu:system:users' }
  },
  {
    path: '/system/base',
    name: 'BaseManage',
    component: () => import('@/views/system/BaseManageView.vue'),
    meta: { title: '基础信息管理', icon: 'Setting', perm: 'menu:system:base' }
  },
  {
    path: '/system/roles',
    name: 'RoleManage',
    component: () => import('@/views/system/RoleManageView.vue'),
    meta: { title: '角色权限', icon: 'Key', perm: 'menu:system:roles' }
  }
]

export function hasRoutePermission(permissions: string[], routePermission?: string) {
  if (!routePermission) return true
  return permissions.includes(routePermission)
}

export function resolveHomePath(permissions: string[]) {
  const first = appRoutes.find((route) => hasRoutePermission(permissions, route.meta.perm))
  return first?.path || '/forbidden'
}
