
import { useState, useEffect } from 'react'
import Sidebar from '../components/Sidebar'
import PendingRegistrations from './PendingRegistrations'
import Employees from './Employees'
import './AdminDashboard.css'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts'

function AdminDashboard() {
  const [activePage, setActivePage] = useState('dashboard')
  const [search, setSearch] = useState('')
  const [showProfileMenu, setShowProfileMenu] = useState(false)

  const [statistics, setStatistics] = useState({
    totalEmployees: 0,
    activeEmployees: 0,
    inactiveEmployees: 0,
    pendingRegistrations: 0,
    employeesOnLeave: null,
    newJoinersThisMonth: 0
  })

  const [isLoading, setIsLoading] = useState(true)
  const [apiError, setApiError] = useState('')

  const email = sessionStorage.getItem('email') || 'Admin'
  const name = sessionStorage.getItem('name') || 'Admin'

  const hour = new Date().getHours()

  const greeting =
    hour < 12 ? 'Good Morning' :
      hour < 17 ? 'Good Afternoon' :
        'Good Evening'

  // Fetch dashboard statistics from backend
  useEffect(() => {
    async function fetchDashboardStatistics() {
      try {
        setIsLoading(true)
        setApiError('')

        const token = sessionStorage.getItem('token')

        const response = await fetch(
          'http://localhost:8080/api/admin/dashboard/stats',
          {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`
            }
          }
        )

        if (!response.ok) {
          throw new Error(
            `Failed to fetch dashboard statistics (${response.status})`
          )
        }

        const data = await response.json()

        setStatistics(data)

      } catch (error) {
        console.error('Dashboard statistics error:', error)
        setApiError(error.message)

      } finally {
        setIsLoading(false)
      }
    }

    fetchDashboardStatistics()
  }, [])

  function handleLogout() {
    sessionStorage.clear()
    window.location.href = '/'
  }

  const pageTitles = {
    dashboard: 'Dashboard',
    employees: 'Employees',
    'pending-registrations': 'Pending Registrations',
    departments: 'Departments',
    settings: 'Settings'
  }

  return (
    <div className="admin-layout">
      <Sidebar
        activePage={activePage}
        onNavigate={setActivePage}
      />

      <main className="admin-main">
        <header className="admin-header">
          <div className="header-title">
            <h1>{pageTitles[activePage]}</h1>
            <p>TATTVAM GROUPS | HR Management System</p>
          </div>

          <div className="header-actions">
            <div className="global-search">
              <span>⌕</span>
              <input
                type="search"
                placeholder="Search..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>

            <button
              type="button"
              className="notification-button"
              aria-label="Notifications"
              title="Notifications"
            >
              ♧
            </button>

            <div className="profile-container">
              <button
                type="button"
                className="profile-button"
                onClick={() => setShowProfileMenu(!showProfileMenu)}
              >
                <span className="profile-avatar">
                  {name.charAt(0).toUpperCase()}
                </span>

                <span className="profile-info">
                  <strong>{name}</strong>
                  <small>Administrator</small>
                </span>

                <span className="profile-arrow">⌄</span>
              </button>

              {showProfileMenu && (
                <div className="profile-menu">
                  <strong>{name}</strong>
                  <small>{email}</small>
                  <button type="button" onClick={handleLogout}>
                    Logout
                  </button>
                </div>
              )}
            </div>
          </div>
        </header>

        {activePage === 'dashboard' && (
          <section className="dashboard-content">
            <h2>{greeting}, {name}!</h2>
            <p>Manage your employees and registrations from here.</p>

            {apiError && (
              <p className="error-message">
                {apiError}
              </p>
            )}

            <div className="dashboard-cards">

              <div className="dashboard-card total-employees">
                <div className="card-icon">👥</div>
                <h3>Total Employees</h3>
                <p>
                  {isLoading ? '...' : statistics.totalEmployees}
                </p>
                <span>All registered employees</span>
              </div>


              <div className="dashboard-card active-employees">
                <div className="card-icon">✓</div>
                <h3>Active Employees</h3>
                <p>
                  {isLoading ? '...' : statistics.activeEmployees}
                </p>
                <span>Currently working</span>
              </div>

              <div className="dashboard-card inactive-employees">
                <div className="card-icon">⊗</div>
                <h3>Inactive Employees</h3>
                <p>
                  {isLoading ? '...' : statistics.inactiveEmployees}
                </p>
                <span>Not currently active</span>
              </div>

              <div className="dashboard-card pending-registrations">
                <div className="card-icon">✚</div>
                <h3>Pending Registrations</h3>
                <p>
                  {isLoading ? '...' : statistics.pendingRegistrations}
                </p>
                <span>Awaiting approval</span>
              </div>

              <div className="dashboard-card employees-on-leave">
                <div className="card-icon">▣</div>
                <h3>Employees on Leave</h3>
                <p>
                  {isLoading
                    ? '...'
                    : statistics.employeesOnLeave ?? '--'}
                </p>
                <span>Currently on leave</span>
              </div>

              <div className="dashboard-card new-joiners">
                <div className="card-icon">★</div>
                <h3>New Joiners This Month</h3>
                <p>
                  {isLoading ? '...' : statistics.newJoinersThisMonth}
                </p>
                <span>Joined this month</span>
              </div>

            </div>



            <div className="dashboard-chart-section">
              <div className="chart-header">
                <h3>Employees by Department</h3>
                <p>Department-wise employee distribution</p>
              </div>

              <div className="chart-container">
                <ResponsiveContainer width="100%" height={175}>
                  <BarChart
                    data={Object.entries(statistics.departmentStatistics || {}).map(
                      ([department, count]) => ({
                        department,
                        employees: Number(count)
                      })
                    )}
                    layout="vertical"
                    margin={{ top: 15, right: 55, left: 30, bottom: 10 }}
                  >
                    <CartesianGrid
                      strokeDasharray="3 3"
                      horizontal={false}
                      stroke="#E5E7EB"
                    />

                    <XAxis
                      type="number"
                      hide={true}
                      domain={[0, 'dataMax + 1']}
                      allowDecimals={false}
                    />

                    <YAxis
                      type="category"
                      dataKey="department"
                      width={280}
                      dx={-270}
                      tick={{
                        fontSize: 15,
                        fill: '#334155',
                        textAnchor: 'start'
                      }}
                      axisLine={false}
                      tickLine={false}
                    />

                    <Tooltip
                      cursor={false}
                      content={() => null}
                    />

                    <Bar
                      dataKey="employees"
                      fill="#3985D6"
                      radius={[0, 6, 6, 0]}
                      barSize={25}
                      activeBar={{
                        fill: '#1769C2',
                        stroke: '#0F4C94',
                        strokeWidth: 1
                      }}
                      isAnimationActive={true}
                      animationDuration={700}
                      label={{
                        position: 'right',
                        fill: '#1E293B',
                        fontSize: 13,
                        fontWeight: 600,
                        formatter: (value) => value
                      }}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>

          </section>
        )}

        {activePage === 'pending-registrations' && (
          <PendingRegistrations />
        )}

        {activePage === 'employees' && (
          <Employees />
        )}

        {activePage === 'departments' && (
          <section className="dashboard-content">
            <h2>Departments</h2>
            <p>Department management will appear here.</p>
          </section>
        )}

        {activePage === 'settings' && (
          <section className="dashboard-content">
            <h2>Settings</h2>
            <p>Admin settings will appear here.</p>
          </section>
        )}
      </main>
    </div>
  )
}

export default AdminDashboard
