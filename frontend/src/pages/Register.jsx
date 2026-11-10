
import { useState } from 'react'
import { registerUser } from '../services/authService'
import './Register.css'

function Register() {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    mobile: '',
    password: '',
    confirmPassword: ''
  })

  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

  function handleChange(event) {
    const { name, value } = event.target

    setFormData((previousData) => ({
      ...previousData,
      [name]: value
    }))

    setMessage('')
    setMessageType('')
  }

  async function handleRegister(event) {
    event.preventDefault()

    setMessage('')
    setMessageType('')

    if (formData.password !== formData.confirmPassword) {
      setMessage('Passwords do not match.')
      setMessageType('error')
      return
    }

    if (!/^[0-9]{10}$/.test(formData.mobile)) {
      setMessage('Please enter a valid 10-digit mobile number.')
      setMessageType('error')
      return
    }

    if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(formData.password)) {
      setMessage(
        'Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character.'
      )
      setMessageType('error')
      return
    }

    try {
      setIsLoading(true)

      await registerUser(formData)

      setMessage(
        'Registration submitted successfully! Your account is pending Admin approval. You can log in after approval.'
      )
      setMessageType('success')

      setFormData({
        fullName: '',
        email: '',
        mobile: '',
        password: '',
        confirmPassword: ''
      })

      setShowPassword(false)
      setShowConfirmPassword(false)

    } catch (error) {
      setMessage(error.message || 'Registration failed. Please try again.')
      setMessageType('error')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="register-page">
      <div className="register-card">

        <div className="register-logo">EMS</div>

        <h1>Create Account</h1>

        <p className="register-subtitle">
          Register to join Employee Management System
        </p>

        <form onSubmit={handleRegister}>

          <div className="register-form-group">
            <label htmlFor="fullName">Full Name</label>
            <input
              id="fullName"
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Enter your full name"
              autoComplete="name"
              required
            />
          </div>

          <div className="register-form-group">
            <label htmlFor="email">Email Address</label>
            <input
              id="email"
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email address"
              autoComplete="email"
              required
            />
          </div>

          <div className="register-form-group">
            <label htmlFor="mobile">Mobile Number</label>
            <input
              id="mobile"
              type="tel"
              name="mobile"
              value={formData.mobile}
              onChange={handleChange}
              placeholder="Enter your mobile number"
              pattern="[0-9]{10}"
              maxLength={10}
              autoComplete="tel"
              required
            />
          </div>

          {/* Password */}
          <div className="register-form-group">
            <label htmlFor="password">Password</label>

            <div className="password-input-wrapper">
              <input
                id="password"
                type={showPassword ? 'text' : 'password'}
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Create a password"
                autoComplete="new-password"
                minLength={8}
                required
              />

              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? 'Hide' : 'Show'}
              </button>
            </div>
          </div>

          {/* Confirm Password */}
          <div className="register-form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>

            <div className="password-input-wrapper">
              <input
                id="confirmPassword"
                type={showConfirmPassword ? 'text' : 'password'}
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Confirm your password"
                autoComplete="new-password"
                minLength={8}
                required
              />

              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                aria-label={
                  showConfirmPassword ? 'Hide password' : 'Show password'
                }
              >
                {showConfirmPassword ? 'Hide' : 'Show'}
              </button>
            </div>
          </div>

          {message && (
            <p
              className="register-message"
              role="alert"
              style={{
                backgroundColor:
                  messageType === 'success' ? '#ecfdf5' : '#fef2f2',
                color:
                  messageType === 'success' ? '#047857' : '#dc2626'
              }}
            >
              {message}
            </p>
          )}

          <button
            type="submit"
            className="register-submit-button"
            disabled={isLoading}
          >
            {isLoading ? 'Submitting...' : 'Create Account'}
          </button>

        </form>

        <div className="back-to-login">
          Already have an account?
          <a href="/"> Sign In</a>
        </div>

      </div>
    </div>
  )
}

export default Register
