import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import './HeroBanner.css'

export interface HeroSlide {
  id: number | string
  title: string
  description: string
  coverGradient: string
  viewHref: string
  detailHref: string
}

const AUTOPLAY_INTERVAL_MS = 8000
const SWIPE_THRESHOLD_PX = 50

export default function HeroBanner({ slides }: { slides: HeroSlide[] }) {
  const [currentIndex, setCurrentIndex] = useState(0)
  const [isHovered, setIsHovered] = useState(false)
  const [isFocused, setIsFocused] = useState(false)
  const [isManuallyPaused, setIsManuallyPaused] = useState(false)
  const touchStartX = useRef<number | null>(null)

  const hasMultipleSlides = slides.length > 1
  const isPaused = isHovered || isFocused || isManuallyPaused

  // 슬라이드가 1개뿐이면 자동 전환 자체가 필요 없다.
  useEffect(() => {
    if (!hasMultipleSlides || isPaused) {
      return
    }
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % slides.length)
    }, AUTOPLAY_INTERVAL_MS)
    return () => clearInterval(timer)
    // currentIndex가 바뀔 때마다(자동이든 수동이든) 타이머를 다시 시작해서,
    // "사용자가 직접 넘기면 자동 전환 타이머가 초기화된다"는 요구사항을 만족시킨다.
  }, [hasMultipleSlides, isPaused, currentIndex, slides.length])

  function goTo(index: number) {
    setCurrentIndex(((index % slides.length) + slides.length) % slides.length)
  }

  function handleTouchStart(e: React.TouchEvent) {
    touchStartX.current = e.touches[0].clientX
  }

  function handleTouchEnd(e: React.TouchEvent) {
    if (touchStartX.current === null) return
    const deltaX = e.changedTouches[0].clientX - touchStartX.current
    if (Math.abs(deltaX) > SWIPE_THRESHOLD_PX) {
      goTo(currentIndex + (deltaX < 0 ? 1 : -1))
    }
    touchStartX.current = null
  }

  return (
    <div
      className="hero"
      role="region"
      aria-roledescription="carousel"
      aria-label="대표 작품 배너"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onFocus={() => setIsFocused(true)}
      onBlur={() => setIsFocused(false)}
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
    >
      {slides.map((slide, index) => (
        <div
          key={slide.id}
          className={`hero__slide ${index === currentIndex ? 'hero__slide--active' : ''}`}
          aria-hidden={index !== currentIndex}
        >
          <div className="hero__background" style={{ backgroundImage: slide.coverGradient }} />
          <div className="hero__overlay" />
          <div className="hero__content">
            <h1 className="hero__title">{slide.title}</h1>
            <p className="hero__description">{slide.description}</p>
            <div className="hero__actions">
              <Link to={slide.viewHref} className="hero__button hero__button--primary">
                감상하기
              </Link>
              <Link to={slide.detailHref} className="hero__button hero__button--secondary">
                작품 상세 보기
              </Link>
            </div>
          </div>
        </div>
      ))}

      {hasMultipleSlides && (
        <>
          <button
            type="button"
            className="hero__arrow hero__arrow--prev"
            aria-label="이전 슬라이드"
            onClick={() => goTo(currentIndex - 1)}
          >
            ‹
          </button>
          <button
            type="button"
            className="hero__arrow hero__arrow--next"
            aria-label="다음 슬라이드"
            onClick={() => goTo(currentIndex + 1)}
          >
            ›
          </button>

          <div className="hero__controls">
            <div className="hero__indicators">
              {slides.map((slide, index) => (
                <button
                  key={slide.id}
                  type="button"
                  className={`hero__indicator ${index === currentIndex ? 'hero__indicator--active' : ''}`}
                  aria-label={`${index + 1}번째 슬라이드로 이동`}
                  aria-current={index === currentIndex}
                  onClick={() => goTo(index)}
                />
              ))}
            </div>
            <button
              type="button"
              className="hero__play-pause"
              aria-label={isManuallyPaused ? '자동 전환 재생' : '자동 전환 일시정지'}
              onClick={() => setIsManuallyPaused((v) => !v)}
            >
              {isManuallyPaused ? '▶' : '❚❚'}
            </button>
          </div>
        </>
      )}
    </div>
  )
}
