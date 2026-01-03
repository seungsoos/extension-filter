import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [fixedExtensions, setFixedExtensions] = useState([])
  const [customExtensions, setCustomExtensions] = useState([])
  const [newExtension, setNewExtension] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    fetchFixedExtensions()
    fetchCustomExtensions()
  }, [])

  const fetchFixedExtensions = async () => {
    try {
      const response = await axios.get('/api/extensions/fixed')
      setFixedExtensions(response.data.data.items || [])
    } catch (err) {
      console.error('고정 확장자 조회 실패:', err)
    }
  }

  const fetchCustomExtensions = async () => {
    try {
      const response = await axios.get('/api/extensions/custom')
      setCustomExtensions(response.data.data.items || [])
    } catch (err) {
      console.error('커스텀 확장자 조회 실패:', err)
    }
  }

  const handleFixedExtensionToggle = async (extension, checked) => {
    try {
      await axios.patch(`/api/extensions/fixed/${extension}`, { checked: !checked })
      fetchFixedExtensions()
    } catch (err) {
      setError('고정 확장자 업데이트 실패')
    }
  }

  const handleAddCustomExtension = async (e) => {
    e.preventDefault()
    setError('')

    if (!newExtension.trim()) {
      setError('확장자를 입력해주세요')
      return
    }

    if (customExtensions.length >= 200) {
      setError('커스텀 확장자는 최대 200개까지 추가 가능합니다')
      return
    }

    try {
      await axios.post('/api/extensions/custom', { extension: newExtension.trim() })
      setNewExtension('')
      fetchCustomExtensions()
    } catch (err) {
      setError(err.response?.data?.meta?.message || '확장자 추가 실패')
    }
  }

  const handleDeleteCustomExtension = async (id) => {
    try {
      await axios.delete(`/api/extensions/custom/${id}`)
      fetchCustomExtensions()
    } catch (err) {
      setError('확장자 삭제 실패')
    }
  }

  return (
    <div className="container">
      <h1>파일 확장자 차단</h1>

      <div className="section">
        <h2>고정 확장자</h2>
        <div className="fixed-extensions">
          {fixedExtensions.map((ext) => (
            <label key={ext.id} className="checkbox-label">
              <input
                type="checkbox"
                checked={ext.checked}
                onChange={() => handleFixedExtensionToggle(ext.extension, ext.checked)}
              />
              <span>{ext.extension}</span>
            </label>
          ))}
        </div>
      </div>

      <div className="section">
        <h2>커스텀 확장자</h2>
        <form onSubmit={handleAddCustomExtension} className="add-form">
          <input
            type="text"
            value={newExtension}
            onChange={(e) => setNewExtension(e.target.value)}
            placeholder="확장자 입력"
            maxLength={20}
            className="input"
          />
          <button type="submit" className="btn-add">+ 추가</button>
        </form>
        {error && <div className="error">{error}</div>}
        <div className="custom-count">
          {customExtensions.length} / 200
        </div>
        <div className="custom-extensions">
          {customExtensions.map((ext) => (
            <div key={ext.id} className="custom-extension-item">
              <span>{ext.extension}</span>
              <button
                onClick={() => handleDeleteCustomExtension(ext.id)}
                className="btn-delete"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default App
