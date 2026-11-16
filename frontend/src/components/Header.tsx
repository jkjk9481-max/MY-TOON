import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function Header() {
  const { auth, loginMock, logout } = useAuth()
  const [showLoginOptions, setShowLoginOptions] = useState(false)

  return (
    <header
      style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '16px 24px',
        borderBottom: '1px solid var(--border)',
      }}
    >
      <Link to="/" style={{ fontWeight: 700, fontSize: 20, textDecoration: 'none', color: 'inherit' }}>
        MY TOON
      </Link>

      {auth ? (
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <span>{auth.nickname}님</span>
          <button type="button" onClick={() => void logout()}>
            로그아웃
          </button>
        </div>
      ) : (
        <div style={{ position: 'relative' }}>
          <button type="button" onClick={() => setShowLoginOptions((v) => !v)}>
            로그인
          </button>
          {showLoginOptions && (
            <div
              style={{
                position: 'absolute',
                right: 0,
                top: '100%',
                marginTop: 8,
                display: 'flex',
                flexDirection: 'column',
                gap: 8,
                padding: 12,
                border: '1px solid var(--border)',
                background: 'var(--bg)',
                borderRadius: 8,
                zIndex: 1,
              }}
            >
              <button
                type="button"
                onClick={() => {
                  loginMock('google')
                  setShowLoginOptions(false)
                }}
              >
                Google로 로그인 (mock)
              </button>
              <button
                type="button"
                onClick={() => {
                  loginMock('kakao')
                  setShowLoginOptions(false)
                }}
              >
                Kakao로 로그인 (mock)
              </button>
            </div>
          )}
        </div>
      )}
    </header>
  )
}
