// 백엔드 독자용 조회 API가 아직 없어서, 화면 구조/디자인을 먼저 잡기 위한 가짜 데이터.
// 나중에 실제 API(GET /api/webtoons, GET /api/webtoons/:id/episodes 등)가 생기면 이 파일은 통째로 걷어낸다.
// coverGradient: 실제 업로드된 작품 이미지가 아직 없어서 쓰는 CSS 그라데이션 placeholder.

export interface MockEpisode {
  episodeNumber: number
  title: string
  content: string
}

export interface MockWebtoon {
  id: number
  title: string
  authorName: string
  description: string
  coverGradient: string
  // 실제 포스터 이미지가 준비되면 이 값만 채우면 된다 (없으면 placeholder가 렌더링됨).
  posterImageUrl?: string
  episodes: MockEpisode[]
}

export const mockWebtoon: MockWebtoon = {
  id: 1,
  title: '달빛 아래 수상한 편의점',
  authorName: '도윤',
  description: '자정이 넘으면 손님들이 조금 이상해지는 편의점 이야기.',
  coverGradient: 'linear-gradient(135deg, #1e3a8a 0%, #0a0e1a 60%, #3b82f6 140%)',
  episodes: [
    {
      episodeNumber: 1,
      title: '1화 - 첫 출근',
      content: '(1화 본문 자리 - 실제 이미지 업로드 기능이 생기면 여기에 웹툰 컷이 들어갑니다)',
    },
    {
      episodeNumber: 2,
      title: '2화 - 수상한 손님',
      content: '(2화 본문 자리 - 로그인한 회원만 볼 수 있습니다)',
    },
    {
      episodeNumber: 3,
      title: '3화 - 자정의 거래',
      content: '(3화 본문 자리 - 로그인한 회원만 볼 수 있습니다)',
    },
  ],
}

// 메인 배너는 여러 작품을 받을 수 있는 구조로 만들어서, 나중에 작품이 늘어나면 그대로 슬라이드가 된다.
export const heroSlides = [mockWebtoon]
