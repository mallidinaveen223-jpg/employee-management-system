import { useCallback, useEffect, useState } from 'react'
import EmployeeDirectory from './EmployeeDirectory'
import './EmployeeDashboard.css'

const API_URL = 'http://localhost:8080/api/employees/me'

function EmployeeDashboard() {
  const [employee, setEmployee] = useState(null)
  const [activePage, setActivePage] = useState('profile')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadProfile = useCallback((signal) => {
    const token = sessionStorage.getItem('token')

    if (!token) {
      window.location.href = '/'
      return Promise.resolve()
    }

    return fetch(API_URL, {
      signal,
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(async (response) => {
        if (signal?.aborted) return null

        if (response.status === 401 || response.status === 403) {
          sessionStorage.clear()
          window.location.href = '/'
          return null
        }

        if (!response.ok) {
          throw new Error('Unable to load employee profile.')
        }

        return response.json()
      })
      .then((data) => {
        if (!signal?.aborted && data !== null) {
          setEmployee(data)
        }
      })
      .catch((err) => {
        if (!signal?.aborted) {
          setError(err.message || 'Unable to load employee profile.')
        }
      })
      .finally(() => {
        if (!signal?.aborted) {
          setLoading(false)
        }
      })
  }, [])

  useEffect(() => {
    const controller = new AbortController()
    void loadProfile(controller.signal)

    return () => {
      controller.abort()
    }
  }, [loadProfile])

  function fetchProfile() {
    setLoading(true)
    setError('')
    return loadProfile()
  }

  function handleLogout() {
    sessionStorage.clear()
    window.location.href = '/'
  }

  function formatDate(date) {
    if (!date) return 'Not available'
    return new Date(date).toLocaleDateString('en-IN')
  }

  if (loading) {
    return (
      <div className="employee-loading">
        Loading your profile...
      </div>
    )
  }

  return (
    <div className="employee-dashboard">
      <aside className="employee-sidebar">
        <h2>EMS</h2>

        <nav>
          <button
            className={`employee-nav-link ${
              activePage === 'profile' ? 'employee-nav-active' : ''
            }`}
            onClick={() => setActivePage('profile')}
          >
            My Profile
          </button>

          <button
            className={`employee-nav-link ${
              activePage === 'directory' ? 'employee-nav-active' : ''
            }`}
            onClick={() => setActivePage('directory')}
          >
            Employee Directory
          </button>
        </nav>
      </aside>

      <main className="employee-main">
        <header className="employee-header">
          <div>
            <h1>Employee Dashboard</h1>
            <p>Welcome to Employee Management System</p>
          </div>

          <button onClick={handleLogout}>Logout</button>
        </header>

        {error && (
          <p className="employee-dashboard-error" role="alert">
            {error}
          </p>
        )}

        {activePage === 'profile' && employee && (
          <>
            <section className="employee-welcome-card">
              <div className="employee-avatar">
                {employee.name?.charAt(0).toUpperCase()}
              </div>

              <div>
                <h2>Welcome, {employee.name}!</h2>
                <p>{employee.designation}</p>
                <span>{employee.employeeCode}</span>
              </div>
            </section>

            <section className="employee-profile-section">
              <div className="employee-section-header">
                <h2>My Profile</h2>
                <button onClick={fetchProfile}>Refresh</button>
              </div>

              <div className="employee-profile-grid">
                <div className="profile-item">
                  <label>Full Name</label>
                  <p>{employee.name}</p>
                </div>

                <div className="profile-item">
                  <label>Employee Code</label>
                  <p>{employee.employeeCode}</p>
                </div>

                <div className="profile-item">
                  <label>Email Address</label>
                  <p>{employee.email}</p>
                </div>

                <div className="profile-item">
                  <label>Mobile Number</label>
                  <p>{employee.mobile}</p>
                </div>

                <div className="profile-item">
                  <label>Department</label>
                  <p>{employee.department}</p>
                </div>

                <div className="profile-item">
                  <label>Designation</label>
                  <p>{employee.designation}</p>
                </div>

                <div className="profile-item">
                  <label>Salary</label>
                  <p>
                    {employee.salary != null
                      ? `₹${Number(employee.salary).toLocaleString('en-IN')}`
                      : 'Not available'}
                  </p>
                </div>

                <div className="profile-item">
                  <label>Joining Date</label>
                  <p>{formatDate(employee.joiningDate)}</p>
                </div>

                <div className="profile-item">
                  <label>Employment Status</label>
                  <p>
                    {employee.employmentStatus || employee.employeeStatus}
                  </p>
                </div>
              </div>
            </section>
          </>
        )}

        {activePage === 'directory' && <EmployeeDirectory />}
      </main>
    </div>
  )
}

export default EmployeeDashboard