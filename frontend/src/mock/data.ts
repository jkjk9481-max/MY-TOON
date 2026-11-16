// 백엔드 독자용 조회 API가 아직 없어서, 화면 구조/디자인을 먼저 잡기 위한 가짜 데이터.
// 나중에 실제 API(GET /api/webtoons, GET /api/webtoons/:id/episodes 등)가 생기면 이 파일은 통째로 걷어낸다.

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
  episodes: MockEpisode[]
}

export const mockWebtoon: MockWebtoon = {
  id: 1,
  title: '달빛 아래 수상한 편의점',
  authorName: '도윤',
  description: '자정이 넘으면 손님들이 조금 이상해지는 편의점 이야기.',
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
  ],
}
