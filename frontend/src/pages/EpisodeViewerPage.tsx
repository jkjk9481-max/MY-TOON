import { Link, useParams } from 'react-router-dom'
import { useEpisodes } from '../hooks/useEpisodes'
import { mockWebtoon } from '../mock/data'
import './EpisodeViewerPage.css'

// 실제 접근 제한(2화부터 로그인 필요 등)을 백엔드 EpisodeQueryService의 판정(accessible/reason)에 맡긴다.
// 여기서 막지 않으면 URL을 직접 쳐서 들어오는 비회원도 내용을 볼 수 있어버린다.
export default function EpisodeViewerPage() {
  const { episodeNumber } = useParams<{ episodeNumber: string }>()
  const episodesState = useEpisodes(mockWebtoon.id)

  if (episodesState.status === 'loading') {
    return (
      <div className="episode-viewer episode-viewer--empty">
        <p>불러오는 중...</p>
      </div>
    )
  }

  if (episodesState.status === 'error') {
    return (
      <div className="episode-viewer episode-viewer--empty">
        <p>{episodesState.message}</p>
        <Link to={`/webtoons/${mockWebtoon.id}`} className="episode-viewer__back">
          ← 작품으로
        </Link>
      </div>
    )
  }

  const episodes = episodesState.episodes
  const episodeIndex = episodes.findIndex((e) => e.episodeNumber === Number(episodeNumber))
  const episode = episodeIndex === -1 ? undefined : episodes[episodeIndex]

  // 여기 도달하는 경우: 번호가 아예 존재하지 않거나 / DRAFT라 항상 숨겨지거나 /
  // PREVIEW인데 비회원이라 목록 자체에서 안 보인 경우. 셋 다 "존재하지 않는 회차"로 안내한다
  // (비회원에게 PREVIEW 회차의 존재 자체를 알려주지 않는 게 의도된 동작이다).
  if (!episode) {
    return (
      <div className="episode-viewer episode-viewer--empty">
        <p>존재하지 않는 회차입니다.</p>
        <Link to={`/webtoons/${mockWebtoon.id}`} className="episode-viewer__back">
          ← 작품으로
        </Link>
      </div>
    )
  }

  const prevEpisode = episodes[episodeIndex - 1]
  const nextEpisode = episodes[episodeIndex + 1]

  return (
    <div className="episode-viewer">
      <div className="episode-viewer__topbar">
        <Link to={`/webtoons/${mockWebtoon.id}`} className="episode-viewer__back">
          ← 작품으로
        </Link>
        <span className="episode-viewer__heading">{episode.title}</span>
        <span className="episode-viewer__spacer" aria-hidden="true" />
      </div>

      <div className="episode-viewer__stage">
        {!episode.accessible ? (
          <div className="episode-viewer__lock">
            <p className="episode-viewer__lock-title">
              {episode.reason === 'INK_REQUIRED' ? '미리보기 구매가 필요한 회차입니다' : '로그인이 필요한 회차입니다'}
            </p>
            <p className="episode-viewer__lock-description">
              {episode.reason === 'INK_REQUIRED'
                ? '미리보기 회차는 잉크로 구매해야 볼 수 있어요. (구매 기능은 아직 준비 중입니다)'
                : '2화부터는 로그인한 회원만 볼 수 있어요. 오른쪽 위 로그인 버튼을 눌러주세요.'}
            </p>
          </div>
        ) : (
          // 실제 이미지 조회 API가 아직 없어서, 컷이 들어올 자리를 비율만 맞춰 보여주는 목업이다.
          <div className="episode-viewer__cut">
            <span className="episode-viewer__cut-icon">🎬</span>
            <p className="episode-viewer__cut-caption">
              (실제 이미지 조회 API가 아직 없어 컷이 들어갈 자리만 표시합니다)
            </p>
          </div>
        )}
      </div>

      <div className="episode-viewer__nav">
        {prevEpisode ? (
          <Link
            to={`/webtoons/${mockWebtoon.id}/episodes/${prevEpisode.episodeNumber}`}
            className="episode-viewer__nav-button"
          >
            ← 이전화
          </Link>
        ) : (
          <span
            className="episode-viewer__nav-button episode-viewer__nav-button--disabled"
            aria-disabled="true"
          >
            ← 이전화
          </span>
        )}

        {nextEpisode ? (
          <Link
            to={`/webtoons/${mockWebtoon.id}/episodes/${nextEpisode.episodeNumber}`}
            className="episode-viewer__nav-button"
          >
            다음화 →
          </Link>
        ) : (
          <span
            className="episode-viewer__nav-button episode-viewer__nav-button--disabled"
            aria-disabled="true"
          >
            다음화 →
          </span>
        )}
      </div>
    </div>
  )
}
