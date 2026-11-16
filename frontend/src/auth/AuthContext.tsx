import { createContext, useContext, useState, type ReactNode } from 'react'
import { logout as logoutApi } from '../api/auth'
import type { LoginResponse } from '../api/types'

const STORAGE_KEY = 'mytoon.auth'

interface AuthState {
  userId: number
  nickname: string
  role: 'USER' | 'ADMIN'
  accessToken: string
  refreshToken: string
}

interface AuthContextValue {
  auth: AuthState | null
  // 실제 Client ID가 없어 당장은 쓰지 않지만, 나중에 콜백 페이지에서 exchangeCodeForTokens 결과로 호출할 수 있도록
  // LoginResponse를 그대로 받는 형태로 맞춰둔다.
  login: (result: LoginResponse) => void
  // Google/Kakao Client ID가 없는 지금 로그인 버튼을 눌렀을 때 쓰는 가짜 로그인.
  loginMock: (provider: 'google' | 'kakao') => void
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

function loadAuthFromStorage(): AuthState | null {
  const raw = localStorage.getItem(STORAGE_KEY)
  return raw ? (JSON.parse(raw) as AuthState) : null
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<AuthState | null>(loadAuthFromStorage)

  function login(result: LoginResponse) {
    const next: AuthState = {
      userId: result.userId,
      nickname: result.nickname,
      role: result.role,
      accessToken: result.accessToken,
      refreshToken: result.refreshToken,
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
    setAuth(next)
  }

  function loginMock(provider: 'google' | 'kakao') {
    login({
      userId: 1,
      nickname: provider === 'google' ? '구글 목업유저' : '카카오 목업유저',
      role: 'USER',
      accessToken: 'mock-access-token',
      refreshToken: 'mock-refresh-token',
    })
  }

  async function logout() {
    if (auth) {
      await logoutApi(auth.refreshToken).catch(() => {
        // 로그아웃 API가 실패해도(네트워크 오류 등) 프론트 상태는 어차피 지워야 하므로 무시한다.
      })
    }
    localStorage.removeItem(STORAGE_KEY)
    setAuth(null)
  }

  return (
    <AuthContext.Provider value={{ auth, login, loginMock, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth는 AuthProvider 안에서만 쓸 수 있습니다.')
  }
  return context
}
