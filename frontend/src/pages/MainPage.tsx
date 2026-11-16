import { Link } from 'react-router-dom'
import { mockWebtoon } from '../mock/data'

// 로그인 여부와 상관없이 누구나 볼 수 있는 메인 화면 (작품 목록).
export default function MainPage() {
  return (
    <section style={{ maxWidth: 600, margin: '40px auto', padding: '0 16px' }}>
      <h1 style={{ fontSize: 28 }}>작품 목록</h1>

      <Link
        to={`/webtoons/${mockWebtoon.id}`}
        style={{
          display: 'block',
          border: '1px solid var(--border)',
          borderRadius: 8,
          padding: 16,
          marginTop: 16,
          textDecoration: 'none',
          color: 'inherit',
        }}
      >
        <h2 style={{ fontSize: 20, margin: 0 }}>{mockWebtoon.title}</h2>
        <p style={{ color: '#666', margin: '8px 0 0' }}>{mockWebtoon.authorName}</p>
        <p style={{ color: '#666', margin: '4px 0 0', fontSize: 14 }}>{mockWebtoon.description}</p>
      </Link>
    </section>
  )
}
