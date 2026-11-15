import { useCallback, useEffect, useState } from 'react'
import './EmployeeDirectory.css'

const API_URL = 'http://localhost:8080/api/employees/directory'

function EmployeeDirectory() {
  const [employees, setEmployees] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadEmployees = useCallback((signal) => {
    const token = sessionStorage.getItem('token')

    return fetch(API_URL, {
      signal,
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Unable to load employee directory.')
        }

        return response.json()
      })
      .then((data) => {
        if (!signal?.aborted) {
          setEmployees(data)
        }
      })
      .catch((err) => {
        if (!signal?.aborted) {
          setError(err.message || 'Unable to load employee directory.')
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
    void loadEmployees(controller.signal)

    return () => {
      controller.abort()
    }
  }, [loadEmployees])

  function fetchEmployees() {
    setLoading(true)
    setError('')
    return loadEmployees()
  }

  const filteredEmployees = employees.filter((employee) =>
    [
      employee.name,
      employee.email,
      employee.mobile,
      employee.department,
      employee.designation
    ]
      .filter(Boolean)
      .some((value) =>
        String(value).toLowerCase().includes(search.toLowerCase())
      )
  )

  return (
    <section className="directory-page">
      <div className="directory-header">
        <div>
          <h2>Employee Directory</h2>
          <p>View employee contact and professional details.</p>
        </div>

        <button onClick={fetchEmployees} disabled={loading}>
          Refresh
        </button>
      </div>

      <input
        className="directory-search"
        type="search"
        placeholder="Search by name, email, department or designation..."
        value={search}
        onChange={(event) => setSearch(event.target.value)}
      />

      {error && (
        <p className="directory-error" role="alert">
          {error}
        </p>
      )}

      {loading ? (
        <p>Loading employee directory...</p>
      ) : filteredEmployees.length === 0 ? (
        <p className="directory-empty">No employees found.</p>
      ) : (
        <div className="directory-table-wrapper">
          <table className="directory-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Mobile</th>
                <th>Department</th>
                <th>Designation</th>
                <th>Role</th>
              </tr>
            </thead>

            <tbody>
              {filteredEmployees.map((employee) => (
                <tr key={employee.id}>
                  <td>{employee.name}</td>
                  <td>{employee.email}</td>
                  <td>{employee.mobile || 'Not available'}</td>
                  <td>{employee.department}</td>
                  <td>{employee.designation}</td>
                  <td>
                    <span className="directory-role">
                      {employee.role}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}

export default EmployeeDirectory