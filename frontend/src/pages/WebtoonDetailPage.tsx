import { Link } from 'react-router-dom'
import { accessReasonLabel } from '../api/episodes'
import { useEpisodes } from '../hooks/useEpisodes'
import { mockWebtoon } from '../mock/data'
import './WebtoonDetailPage.css'

// 실제 접근 제한(2화부터 로그인 필요)을 백엔드 EpisodeQueryService의 판정(accessible/reason)에 맡긴다.
// 작품 제목/작가/소개/포스터는 아직 공개 조회 API가 없어 mock을 그대로 쓴다.
export default function WebtoonDetailPage() {
  const episodesState = useEpisodes(mockWebtoon.id)
  const firstEpisodeNumber =
    episodesState.status === 'success' ? episodesState.episodes[0]?.episodeNumber : undefined

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
            {firstEpisodeNumber !== undefined && (
              <Link
                to={`/webtoons/${mockWebtoon.id}/episodes/${firstEpisodeNumber}`}
                className="webtoon-detail__start-button"
              >
                감상 시작하기
              </Link>
            )}
          </div>
        </div>

        <section className="webtoon-detail__episodes">
          <h2 className="webtoon-detail__episodes-title">전체 회차</h2>

          {episodesState.status === 'loading' && <p>회차 목록을 불러오는 중...</p>}
          {episodesState.status === 'error' && <p>{episodesState.message}</p>}

          {episodesState.status === 'success' && (
            <ul className="episode-list">
              {episodesState.episodes.map((episode) => (
                <li key={episode.episodeNumber}>
                  <Link
                    to={`/webtoons/${mockWebtoon.id}/episodes/${episode.episodeNumber}`}
                    className={`episode-list__item ${!episode.accessible ? 'episode-list__item--locked' : ''}`}
                  >
                    <div
                      className="episode-list__thumb"
                      style={{ backgroundImage: mockWebtoon.coverGradient }}
                    >
                      <span className="episode-list__number">{episode.episodeNumber}</span>
                    </div>
                    <div className="episode-list__meta">
                      <span className="episode-list__title">{episode.title}</span>
                      {!episode.accessible && (
                        <span className="episode-list__status">🔒 {accessReasonLabel(episode.reason)}</span>
                      )}
                    </div>
                  </Link>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  )
}
