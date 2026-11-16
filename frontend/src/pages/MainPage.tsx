import { Link } from 'react-router-dom'
import HeroBanner from '../components/HeroBanner'
import WebtoonCard from '../components/WebtoonCard'
import { useAuth } from '../auth/AuthContext'
import { heroSlides, mockWebtoon } from '../mock/data'
import './MainPage.css'

export default function MainPage() {
  const { auth } = useAuth()

  const slides = heroSlides.map((webtoon) => ({
    id: webtoon.id,
    title: webtoon.title,
    description: webtoon.description,
    coverGradient: webtoon.coverGradient,
    viewHref: `/webtoons/${webtoon.id}/episodes/${webtoon.episodes[0].episodeNumber}`,
    detailHref: `/webtoons/${webtoon.id}`,
  }))

  return (
    <>
      <HeroBanner slides={slides} />

      <div className="page-container">
        <section className="main-section">
          <h2 className="main-section__title">최신 업데이트 회차</h2>
          <div className="episode-row">
            {mockWebtoon.episodes.map((episode) => {
              const requiresLogin = episode.episodeNumber >= 2 && !auth
              return (
                <Link
                  key={episode.episodeNumber}
                  to={`/webtoons/${mockWebtoon.id}/episodes/${episode.episodeNumber}`}
                  className="episode-row__item"
                >
                  <span>{episode.title}</span>
                  {requiresLogin && <span className="episode-row__lock">🔒 로그인 필요</span>}
                </Link>
              )
            })}
          </div>
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
