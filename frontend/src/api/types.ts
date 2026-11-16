// 백엔드 com.mytoon.backend.user.controller.dto.LoginResponse 와 동일한 형태.
export type Role = 'USER' | 'ADMIN'

export interface LoginResponse {
  userId: number
  nickname: string
  role: Role
  accessToken: string
  refreshToken: string
}
