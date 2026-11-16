import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { mockWebtoon } from '../mock/data'

// 비회원은 1화까지만 자유 열람, 2화부터는 로그인이 필요하다는 확정 정책(CLAUDE.md)을 목록에서도 보여준다.
export default function WebtoonDetailPage() {
  const { auth } = useAuth()

  return (
    <section style={{ maxWidth: 600, margin: '40px auto', padding: '0 16px' }}>
      <Link to="/">← 목록으로</Link>
      <h1 style={{ fontSize: 28 }}>{mockWebtoon.title}</h1>
      <p style={{ color: '#666' }}>{mockWebtoon.authorName}</p>

      <ul style={{ listStyle: 'none', padding: 0, marginTop: 16 }}>
        {mockWebtoon.episodes.map((episode) => {
          const requiresLogin = episode.episodeNumber >= 2 && !auth
          return (
            <li key={episode.episodeNumber} style={{ borderTop: '1px solid var(--border)' }}>
              <Link
                to={`/webtoons/${mockWebtoon.id}/episodes/${episode.episodeNumber}`}
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  padding: '12px 0',
                  textDecoration: 'none',
                  color: 'inherit',
                }}
              >
                <span>{episode.title}</span>
                {requiresLogin && <span>🔒 로그인 필요</span>}
              </Link>
            </li>
          )
        })}
      </ul>
    </section>
  )
}
