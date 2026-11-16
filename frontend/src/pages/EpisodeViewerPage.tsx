import { Link, useParams } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { mockWebtoon } from '../mock/data'

// 실제 접근 제한(2화부터 로그인 필요)을 강제하는 지점. 목록의 자물쇠 표시는 안내일 뿐이고,
// 여기서 막지 않으면 URL을 직접 쳐서 들어오는 비회원도 내용을 볼 수 있어버린다.
export default function EpisodeViewerPage() {
  const { episodeNumber } = useParams<{ episodeNumber: string }>()
  const { auth } = useAuth()

  const episode = mockWebtoon.episodes.find((e) => e.episodeNumber === Number(episodeNumber))

  if (!episode) {
    return (
      <section style={{ maxWidth: 600, margin: '40px auto', padding: '0 16px' }}>
        <p>존재하지 않는 회차입니다.</p>
        <Link to={`/webtoons/${mockWebtoon.id}`}>← 작품으로</Link>
      </section>
    )
  }

  const requiresLogin = episode.episodeNumber >= 2 && !auth

  return (
    <section style={{ maxWidth: 600, margin: '40px auto', padding: '0 16px' }}>
      <Link to={`/webtoons/${mockWebtoon.id}`}>← 작품으로</Link>
      <h1 style={{ fontSize: 24 }}>{episode.title}</h1>

      {requiresLogin ? (
        <div
          style={{
            marginTop: 24,
            padding: 24,
            border: '1px solid var(--border)',
            borderRadius: 8,
            textAlign: 'center',
          }}
        >
          <p>로그인이 필요한 작품입니다.</p>
          <p style={{ color: '#666', fontSize: 14 }}>2화부터는 로그인한 회원만 볼 수 있어요. 오른쪽 위 로그인 버튼을 눌러주세요.</p>
        </div>
      ) : (
        <p style={{ marginTop: 24 }}>{episode.content}</p>
      )}
    </section>
  )
}
