import type { LoginResponse } from './types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

// 구글/카카오가 돌려준 code를 백엔드로 전달해 로그인/가입을 완료한다.
// 아직 Client ID가 없어 실제로 호출할 일은 없지만, 나중에 그대로 쓸 수 있도록 미리 준비해둔다.
export async function exchangeCodeForTokens(
  provider: 'google' | 'kakao',
  code: string,
): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/api/auth/${provider}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ code }),
  })

  if (!response.ok) {
    throw new Error(`로그인에 실패했습니다 (status: ${response.status})`)
  }

  return response.json()
}

export async function refreshTokens(refreshToken: string): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  })

  if (!response.ok) {
    throw new Error(`토큰 갱신에 실패했습니다 (status: ${response.status})`)
  }

  return response.json()
}

export async function logout(refreshToken: string): Promise<void> {
  await fetch(`${API_BASE_URL}/api/auth/logout`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  })
  // 백엔드 logout은 없는/이미 폐기된 토큰이어도 조용히 204를 주므로(멱등), 실패해도 프론트에서 막을 이유는 없다.
}
