import { Link } from 'react-router-dom'
import './WebtoonCard.css'

export default function WebtoonCard({
  id,
  title,
  authorName,
  coverGradient,
}: {
  id: number
  title: string
  authorName: string
  coverGradient: string
}) {
  return (
    <Link to={`/webtoons/${id}`} className="webtoon-card">
      <div className="webtoon-card__cover" style={{ backgroundImage: coverGradient }} />
      <p className="webtoon-card__title">{title}</p>
      <p className="webtoon-card__author">{authorName}</p>
    </Link>
  )
}
