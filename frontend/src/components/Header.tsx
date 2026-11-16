import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import './Header.css'

export default function Header() {
  const { auth, loginMock, logout } = useAuth()
  const [showLoginOptions, setShowLoginOptions] = useState(false)

  return (
    <header className="header">
      <div className="header__inner page-container">
        <Link to="/" className="header__logo">
          MY-TOON
        </Link>

        <nav className="header__nav">
          <Link to="/" className="header__nav-link">
            홈
          </Link>
          <a href="#explore" className="header__nav-link">
            작품 탐색
          </a>
        </nav>

        <div className="header__actions">
          {/* 검색 API가 아직 없어서 자리만 잡아둔 비활성 버튼 */}
          <button
            type="button"
            className="header__icon-button"
            disabled
            title="검색 기능은 준비 중입니다"
            aria-label="검색 (준비 중)"
          >
            🔍
          </button>

          {auth ? (
            <div className="header__user">
              <span>{auth.nickname}님</span>
              <button type="button" className="header__logout-button" onClick={() => void logout()}>
                로그아웃
              </button>
            </div>
          ) : (
            <div className="header__login-dropdown">
              <button
                type="button"
                className="header__login-button"
                onClick={() => setShowLoginOptions((v) => !v)}
              >
                로그인
              </button>
              {showLoginOptions && (
                <div className="header__login-options">
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
        </div>
      </div>
    </header>
  )
}
