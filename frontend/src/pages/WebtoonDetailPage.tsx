import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { mockWebtoon } from '../mock/data'
import './WebtoonDetailPage.css'

// 비회원은 1화까지만 자유 열람, 2화부터는 로그인이 필요하다는 확정 정책(CLAUDE.md)을 목록에서도 보여준다.
export default function WebtoonDetailPage() {
  const { auth } = useAuth()
  const firstEpisode = mockWebtoon.episodes[0]

  return (
    <div className="webtoon-detail">
      <div className="webtoon-detail__backdrop" style={{ backgroundImage: mockWebtoon.coverGradient }} />
      <div className="webtoon-detail__scrim" />

      <div className="page-container webtoon-detail__content">
        <Link to="/" className="webtoon-detail__back">
          ← 목록으로
        </Link>

        <div className="webtoon-detail__header">
          <div className="webtoon-detail__poster">
            {mockWebtoon.posterImageUrl ? (
              <img className="webtoon-detail__poster-image" src={mockWebtoon.posterImageUrl} alt="" />
            ) : (
              <div className="webtoon-detail__poster-placeholder">
                <span className="webtoon-detail__poster-placeholder-icon">🎬</span>
                <span>포스터 이미지 준비 중</span>
              </div>
            )}
          </div>

          <div className="webtoon-detail__info">
            <span className="webtoon-detail__brand">MY-TOON ORIGINAL</span>
            <h1 className="webtoon-detail__title">{mockWebtoon.title}</h1>
            <p className="webtoon-detail__author">{mockWebtoon.authorName}</p>
            <p className="webtoon-detail__description">{mockWebtoon.description}</p>
            <Link
              to={`/webtoons/${mockWebtoon.id}/episodes/${firstEpisode.episodeNumber}`}
              className="webtoon-detail__start-button"
            >
              감상 시작하기
            </Link>
          </div>
        </div>

        <section className="webtoon-detail__episodes">
          <h2 className="webtoon-detail__episodes-title">전체 회차</h2>
          <ul className="episode-list">
            {mockWebtoon.episodes.map((episode) => {
              const requiresLogin = episode.episodeNumber >= 2 && !auth
              return (
                <li key={episode.episodeNumber}>
                  <Link
                    to={`/webtoons/${mockWebtoon.id}/episodes/${episode.episodeNumber}`}
                    className={`episode-list__item ${requiresLogin ? 'episode-list__item--locked' : ''}`}
                  >
                    <div
                      className="episode-list__thumb"
                      style={{ backgroundImage: mockWebtoon.coverGradient }}
                    >
                      <span className="episode-list__number">{episode.episodeNumber}</span>
                    </div>
                    <div className="episode-list__meta">
                      <span className="episode-list__title">{episode.title}</span>
                      {requiresLogin && (
                        <span className="episode-list__status">🔒 로그인 필요</span>
                      )}
                    </div>
                  </Link>
                </li>
              )
            })}
          </ul>
        </section>
      </div>
    </div>
  )
}
