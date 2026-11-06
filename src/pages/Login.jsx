
import { useState } from 'react'
import { loginUser } from '../services/authService'
import './Login.css'

function Login() {
  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  async function handleLogin(event) {
    event.preventDefault()
    setError('')

    if (!identifier.trim() || !password.trim()) {
      setError('Please enter your email address and password.')
      return
    }

    try {
      setIsLoading(true)

      const data = await loginUser(identifier.trim(), password)

      sessionStorage.setItem('token', data.token)
      sessionStorage.setItem('email', data.email)
      sessionStorage.setItem('role', data.role)

      const role = data.role.toUpperCase()

      if (role === 'ADMIN') {
        window.history.pushState({}, '', '/dashboard')
        window.dispatchEvent(new PopStateEvent('popstate'))
      } else if (role === 'EMPLOYEE') {
        window.history.pushState({}, '', '/employee-dashboard')
        window.dispatchEvent(new PopStateEvent('popstate'))
      } else {
        setError('Invalid user role.')
      }

    } catch (err) {
      setError(err.message || 'Invalid email or password.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="top-navigation">
        <span>People</span>
        <span>Growth</span>
        <span>Excellence</span>
      </div>

      <section className="branding-section">
        <div className="brand-header">
          <div className="brand-logo">EMS</div>
          <div className="brand-name">
            Employee<br />
            Management<br />
            System
          </div>
        </div>

        <div className="brand-content">
          <h1>
            Empowering<br />
            People,<br />
            Building a<br />
            <span>Better Workplace</span>
          </h1>

          <div className="brand-line"></div>

          <p>
            Manage. Collaborate. Grow.<br />
            Together.
          </p>

          <div className="brand-features">
            <div>
              <div className="feature-icon">♙</div>
              <span>Our<br />People</span>
            </div>
            <div>
              <div className="feature-icon">↗</div>
              <span>Our<br />Culture</span>
            </div>
            <div>
              <div className="feature-icon">◎</div>
              <span>Our<br />Success</span>
            </div>
          </div>
        </div>
      </section>

      <section className="login-section">
        <div className="login-card">
          <div className="card-logo">EMS</div>

          <h2>Welcome Back!</h2>
          <p className="login-subtitle">
            Sign in to access your account
          </p>

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                id="email"
                type="email"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                placeholder="Enter your email address"
                autoComplete="username"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>

              <div className="password-input-wrapper">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                />

                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
            </div>

            <div className="forgot-password">
              <a href="#forgot-password">Forgot Password?</a>
            </div>

            {error && (
              <p className="login-error" role="alert">
                {error}
              </p>
            )}

            <button
              type="submit"
              className="sign-in-button"
              disabled={isLoading}
            >
              {isLoading ? 'Signing In...' : 'Sign In'}
              {!isLoading && <span> →</span>}
            </button>
          </form>

          <div className="register-divider">
            <span></span> or <span></span>
          </div>

          <div className="register-link">
            Don't have an account?
            <a href="/register"> Create Account</a>
          </div>
        </div>

        <p className="login-footer">
          © 2026 Employee Management System. All rights reserved.
        </p>
      </section>
    </div>
  )
}

export default Login
