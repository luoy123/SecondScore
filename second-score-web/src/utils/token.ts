const TOKEN_KEY = 'second_score_token'
const PROFILE_KEY = 'second_score_profile'

export function getToken(): string {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function getProfileCache<T>(): T | null {
  const text = localStorage.getItem(PROFILE_KEY)
  if (!text) return null
  try {
    return JSON.parse(text) as T
  } catch {
    return null
  }
}

export function setProfileCache(data: unknown): void {
  localStorage.setItem(PROFILE_KEY, JSON.stringify(data))
}

export function clearProfileCache(): void {
  localStorage.removeItem(PROFILE_KEY)
}
