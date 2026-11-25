// 작품(제목/작가/소개/표지)을 읽는 공개 조회 API가 아직 없어서 쓰는 가짜 데이터.
// 회차 목록/접근 가능 여부는 실제 API(GET /api/webtoons/:id/episodes, src/api/episodes.ts)로 대체됐다.
// 나중에 공개 작품 조회 API가 생기면 이 파일은 통째로 걷어낸다.
// coverGradient: 실제 업로드된 작품 이미지가 아직 없어서 쓰는 CSS 그라데이션 placeholder.

export interface MockWebtoon {
  id: number
  title: string
  authorName: string
  genres: string[]
  tagline: string
  description: string
  coverGradient: string
  // 실제 포스터 이미지가 준비되면 이 값만 채우면 된다 (없으면 placeholder가 렌더링됨).
  posterImageUrl?: string
}

export const mockWebtoon: MockWebtoon = {
  id: 1,
  title: '국가특별대응단',
  authorName: '도윤',
  genres: ['액션', '범죄', '성장', '드라마'],
  tagline: '최악의 상황, 최후의 대응.',
  description:
    '수년간의 경찰공무원 시험에서 번번이 실패한 이효섭. 꿈을 포기하려던 순간, 국가특별대응단이라는 새로운 기회가 찾아온다. 자신의 한계를 넘어서는 그의 도전이 시작된다.',
  coverGradient: 'linear-gradient(135deg, #1e3a8a 0%, #0a0e1a 60%, #3b82f6 140%)',
  posterImageUrl: '/poster-main.png',
}

// 메인 배너는 여러 작품을 받을 수 있는 구조로 만들어서, 나중에 작품이 늘어나면 그대로 슬라이드가 된다.
export const heroSlides = [mockWebtoon]
