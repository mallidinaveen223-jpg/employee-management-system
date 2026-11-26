
import { useState } from 'react'
import './ActivateAccount.css'

function ActivateAccount() {
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const token = new URLSearchParams(window.location.search).get('token')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMessage('')

    if (!token) {
      setMessage('Invalid activation link.')
      return
    }

    if (password !== confirmPassword) {
      setMessage('Passwords do not match.')
      return
    }

    if (password.length < 8) {
      setMessage('Password must contain at least 8 characters.')
      return
    }

    try {
      setLoading(true)

      const response = await fetch('http://localhost:8080/api/auth/activate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          token,
          password,
          confirmPassword
        })
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || 'Account activation failed.')
      }

      setMessage(data.message || 'Account activated successfully.')
      setPassword('')
      setConfirmPassword('')

    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="activation-container">
      <div className="activation-card">
        <h1>Activate Your Account</h1>
        <p>Create your password to activate your account.</p>

        <form onSubmit={handleSubmit}>
          <label>New Password</label>
          <input
            type="password"
            placeholder="Enter new password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <label>Confirm Password</label>
          <input
            type="password"
            placeholder="Confirm new password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? 'Activating...' : 'Activate Account'}
          </button>
        </form>

        {message && <p>{message}</p>}
      </div>
    </div>
  )
}

export default ActivateAccount
