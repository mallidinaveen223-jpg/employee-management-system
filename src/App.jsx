
import { useState, useEffect } from 'react'

import Login from './pages/Login'
import Register from './pages/Register'
import AdminDashboard from './pages/AdminDashboard'
import EmployeeDashboard from './pages/EmployeeDashboard'

function getCurrentPage() {
  const path = window.location.pathname
  const role = sessionStorage.getItem('role')

  if (path === '/register') {
    return 'register'
  }

  if (path === '/dashboard' && role === 'ADMIN') {
    return 'dashboard'
  }

  if (path === '/employee-dashboard' && role === 'EMPLOYEE') {
    return 'employee-dashboard'
  }

  return 'login'
}

function App() {
  const [page, setPage] = useState(getCurrentPage)

  useEffect(() => {
    function handlePopState() {
      setPage(getCurrentPage())
    }

    window.addEventListener('popstate', handlePopState)

    return () => {
      window.removeEventListener('popstate', handlePopState)
    }
  }, [])

  if (page === 'register') {
    return <Register />
  }

  if (page === 'dashboard') {
    return <AdminDashboard />
  }

  if (page === 'employee-dashboard') {
    return <EmployeeDashboard />
  }

  return <Login />
}

export default App
