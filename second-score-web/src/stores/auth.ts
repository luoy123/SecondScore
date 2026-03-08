import { defineStore } from 'pinia'

import { loginApi, meApi, type LoginParams, type LoginResult, type UserProfile } from '@/api/auth'
import { clearProfileCache, clearToken, getProfileCache, getToken, setProfileCache, setToken } from '@/utils/token'

interface AuthState {
  token: string
  profile: UserProfile | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: getToken(),
    profile: getProfileCache<UserProfile>()
  }),
  getters: {
    roles: (state) => state.profile?.roles || [],
    permissions: (state) => state.profile?.permissions || [],
    displayName: (state) => state.profile?.realName || state.profile?.username || '用户',
    avatarUrl: (state) => state.profile?.avatarUrl || ''
  },
  actions: {
    async login(payload: LoginParams) {
      const result = await loginApi(payload)
      this.setLogin(result)
      await this.fetchProfile()
    },
    setLogin(result: LoginResult) {
      this.token = result.token
      setToken(result.token)
    },
    async fetchProfile() {
      const profile = await meApi()
      this.profile = profile
      setProfileCache(profile)
      return profile
    },
    setAvatar(avatarUrl: string) {
      if (!this.profile) return
      this.profile = {
        ...this.profile,
        avatarUrl
      }
      setProfileCache(this.profile)
    },
    logout() {
      this.token = ''
      this.profile = null
      clearToken()
      clearProfileCache()
    }
  }
})
