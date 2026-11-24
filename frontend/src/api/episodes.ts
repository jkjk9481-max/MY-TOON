const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

// 백엔드 com.mytoon.backend.webtoon.controller.dto.EpisodeAccessResponse 와 동일한 형태.
export interface EpisodeAccessResponse {
  episodeNumber: number
  title: string
  accessible: boolean
  reason: 'LOGIN_REQUIRED' | 'INK_REQUIRED' | null
}

export function accessReasonLabel(reason: EpisodeAccessResponse['reason']): string {
  return reason === 'INK_REQUIRED' ? '미리보기 (잉크 필요)' : '로그인 필요'
}

export async function fetchEpisodes(
  webtoonId: number,
  accessToken?: string,
): Promise<EpisodeAccessResponse[]> {
  const response = await fetch(`${API_BASE_URL}/api/webtoons/${webtoonId}/episodes`, {
    headers: accessToken ? { Authorization: `Bearer ${accessToken}` } : undefined,
  })

  if (!response.ok) {
    throw new Error(`회차 목록을 불러오지 못했습니다 (status: ${response.status})`)
  }

  return response.json()
}
