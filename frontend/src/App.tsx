import { Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import Header from './components/Header'
import EpisodeViewerPage from './pages/EpisodeViewerPage'
import MainPage from './pages/MainPage'
import WebtoonDetailPage from './pages/WebtoonDetailPage'

function App() {
  return (
    <AuthProvider>
      <Header />
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/webtoons/:id" element={<WebtoonDetailPage />} />
        <Route path="/webtoons/:id/episodes/:episodeNumber" element={<EpisodeViewerPage />} />
      </Routes>
    </AuthProvider>
  )
}

export default App
