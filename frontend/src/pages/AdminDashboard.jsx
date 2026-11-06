
import { useState } from 'react'
import Sidebar from '../components/Sidebar'
import PendingRegistrations from './PendingRegistrations'
import Employees from './Employees'
import './AdminDashboard.css'

function AdminDashboard() {
  const [activePage, setActivePage] = useState('dashboard')

  const email = sessionStorage.getItem('email') || 'Admin'

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
          <div>
            <h1>{pageTitles[activePage]}</h1>
            <p>Employee Management System</p>
          </div>

          <div className="admin-user">
            <span>{email}</span>
            <button onClick={handleLogout}>Logout</button>
          </div>
        </header>

        {activePage === 'dashboard' && (
          <section className="dashboard-content">
            <h2>Welcome, Admin!</h2>
            <p>Manage your employees and registrations from here.</p>

            <div className="dashboard-cards">
              <div className="dashboard-card">
                <h3>Total Employees</h3>
                <p>{'--'}</p>
              </div>

              <div className="dashboard-card">
                <h3>Pending Registrations</h3>
                <p>{'--'}</p>
              </div>

              <div className="dashboard-card">
                <h3>Departments</h3>
                <p>{'--'}</p>
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
