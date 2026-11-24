import { useEffect, useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import { fetchEpisodes, type EpisodeAccessResponse } from '../api/episodes'

export type EpisodesState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'success'; episodes: EpisodeAccessResponse[] }

// 세 화면(메인/작품 상세/뷰어)이 전부 같은 회차 목록+접근 가능 여부를 써서 훅으로 공용화했다.
export function useEpisodes(webtoonId: number): EpisodesState {
  const { auth } = useAuth()
  const [state, setState] = useState<EpisodesState>({ status: 'loading' })

  useEffect(() => {
    let cancelled = false
    setState({ status: 'loading' })

    fetchEpisodes(webtoonId, auth?.accessToken)
      .then((episodes) => {
        if (!cancelled) {
          setState({ status: 'success', episodes })
        }
      })
      .catch((error: unknown) => {
        if (!cancelled) {
          setState({
            status: 'error',
            message: error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.',
          })
        }
      })

    return () => {
      cancelled = true
    }
  }, [webtoonId, auth?.accessToken])

  return state
}
