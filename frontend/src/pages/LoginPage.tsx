import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function LoginPage() {
  const { auth, loginMock } = useAuth()

  if (auth) {
    return <Navigate to="/" replace />
  }

  return (
    <section style={{ maxWidth: 360, margin: '80px auto', textAlign: 'center' }}>
      <h1>MY TOON 로그인</h1>
      <p style={{ color: '#666', fontSize: 14 }}>
        아직 Google/Kakao 앱 등록 전이라, 버튼을 누르면 목업(가짜) 로그인으로 동작합니다.
      </p>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginTop: 24 }}>
        <button type="button" onClick={() => loginMock('google')}>
          Google로 로그인 (mock)
        </button>
        <button type="button" onClick={() => loginMock('kakao')}>
          Kakao로 로그인 (mock)
        </button>
      </div>
    </section>
  )
}
