import { Link } from 'react-router-dom'
import HeroBanner from '../components/HeroBanner'
import WebtoonCard from '../components/WebtoonCard'
import { accessReasonLabel } from '../api/episodes'
import { useEpisodes } from '../hooks/useEpisodes'
import { heroSlides, mockWebtoon } from '../mock/data'
import './MainPage.css'

export default function MainPage() {
  const episodesState = useEpisodes(mockWebtoon.id)

  // 작품 제목/작가/소개/표지는 아직 공개 조회 API가 없어 mock 유지, 1화는 정책상 항상 공개라 고정 링크로 둔다.
  const slides = heroSlides.map((webtoon) => ({
    id: webtoon.id,
    title: webtoon.title,
    genres: webtoon.genres,
    tagline: webtoon.tagline,
    description: webtoon.description,
    coverGradient: webtoon.coverGradient,
    posterImageUrl: webtoon.posterImageUrl,
    viewHref: `/webtoons/${webtoon.id}/episodes/1`,
    detailHref: `/webtoons/${webtoon.id}`,
  }))

  return (
    <>
      <HeroBanner slides={slides} />

      <div className="page-container">
        <section className="main-section">
          <h2 className="main-section__title">최신 업데이트 회차</h2>

          {episodesState.status === 'loading' && <p>회차 목록을 불러오는 중...</p>}
          {episodesState.status === 'error' && <p>{episodesState.message}</p>}

          {episodesState.status === 'success' && (
            <div className="episode-card-row">
              {episodesState.episodes.map((episode) => (
                <Link
                  key={episode.episodeNumber}
                  to={`/webtoons/${mockWebtoon.id}/episodes/${episode.episodeNumber}`}
                  className="episode-card"
                >
                  <div
                    className="episode-card__thumb"
                    style={{ backgroundImage: mockWebtoon.coverGradient }}
                  >
                    <span className="episode-card__number">{episode.episodeNumber}</span>
                  </div>
                  <div className="episode-card__meta">
                    <span className="episode-card__title">{episode.title}</span>
                    {!episode.accessible && (
                      <span className="episode-card__lock">🔒 {accessReasonLabel(episode.reason)}</span>
                    )}
                  </div>
                </Link>
              ))}
            </div>
          )}
        </section>

        <section id="explore" className="main-section">
          <h2 className="main-section__title">작품 탐색</h2>
          <div className="webtoon-grid">
            <WebtoonCard
              id={mockWebtoon.id}
              title={mockWebtoon.title}
              authorName={mockWebtoon.authorName}
              coverGradient={mockWebtoon.coverGradient}
            />
          </div>
        </section>
      </div>
    </>
  )
}
