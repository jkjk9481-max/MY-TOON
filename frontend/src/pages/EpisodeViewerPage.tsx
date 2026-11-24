import { Link, useParams } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { mockWebtoon } from '../mock/data'
import './EpisodeViewerPage.css'

// 실제 접근 제한(2화부터 로그인 필요)을 강제하는 지점. 목록의 자물쇠 표시는 안내일 뿐이고,
// 여기서 막지 않으면 URL을 직접 쳐서 들어오는 비회원도 내용을 볼 수 있어버린다.
export default function EpisodeViewerPage() {
  const { episodeNumber } = useParams<{ episodeNumber: string }>()
  const { auth } = useAuth()

  const episodeIndex = mockWebtoon.episodes.findIndex(
    (e) => e.episodeNumber === Number(episodeNumber),
  )
  const episode = episodeIndex === -1 ? undefined : mockWebtoon.episodes[episodeIndex]

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

  const requiresLogin = episode.episodeNumber >= 2 && !auth
  const prevEpisode = mockWebtoon.episodes[episodeIndex - 1]
  const nextEpisode = mockWebtoon.episodes[episodeIndex + 1]

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
        {requiresLogin ? (
          <div className="episode-viewer__lock">
            <p className="episode-viewer__lock-title">로그인이 필요한 회차입니다</p>
            <p className="episode-viewer__lock-description">
              2화부터는 로그인한 회원만 볼 수 있어요. 오른쪽 위 로그인 버튼을 눌러주세요.
            </p>
          </div>
        ) : (
          // 실제 이미지 조회 API가 아직 없어서, 컷이 들어올 자리를 비율만 맞춰 보여주는 목업이다.
          <div className="episode-viewer__cut">
            <span className="episode-viewer__cut-icon">🎬</span>
            <p className="episode-viewer__cut-caption">{episode.content}</p>
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
