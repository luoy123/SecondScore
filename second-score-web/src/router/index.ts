import { createRouter, createWebHistory } from 'vue-router'

import { getToken } from '@/utils/token'
import { useAuthStore } from '@/stores/auth'
import AppLayout from '@/layout/AppLayout.vue'
import { appRoutes, hasRoutePermission, resolveHomePath } from './routes'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { title: '登录', hideInMenu: true }
    },
    {
      path: '/',
      component: AppLayout,
      children: appRoutes
    },
    {
      path: '/forbidden',
      name: 'Forbidden',
      component: () => import('@/views/system/ForbiddenView.vue'),
      meta: { title: '无权限', hideInMenu: true }
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/login'
    }
  ]
})

router.beforeEach(async (to, _from, next) => {
  const token = getToken()
  const authStore = useAuthStore()
  const needsProfileRefresh = !authStore.profile || !Array.isArray(authStore.profile.permissions)

  if (!token) {
    if (to.path === '/login') {
      next()
      return
    }
    next('/login')
    return
  }

  if (to.path === '/login') {
    if (needsProfileRefresh) {
      await authStore.fetchProfile().catch(() => authStore.logout())
    }
    next(resolveHomePath(authStore.permissions))
    return
  }

  if (needsProfileRefresh) {
    try {
      await authStore.fetchProfile()
    } catch {
      authStore.logout()
      next('/login')
      return
    }
  }

  if (to.path === '/') {
    next(resolveHomePath(authStore.permissions))
    return
  }

  const routePermission = to.meta?.perm as string | undefined
  if (routePermission && !hasRoutePermission(authStore.permissions, routePermission)) {
    next('/forbidden')
    return
  }

  next()
})

export default router
