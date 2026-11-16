import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function HomePage() {
  const { auth, logout } = useAuth()

  if (!auth) {
    return <Navigate to="/login" replace />
  }

  return (
    <section style={{ maxWidth: 360, margin: '80px auto', textAlign: 'center' }}>
      <h1>환영합니다, {auth.nickname}님</h1>
      <p style={{ color: '#666', fontSize: 14 }}>역할: {auth.role}</p>
      <button type="button" onClick={() => void logout()}>
        로그아웃
      </button>
    </section>
  )
}
