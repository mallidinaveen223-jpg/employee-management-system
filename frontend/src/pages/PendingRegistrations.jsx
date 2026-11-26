
import { useCallback, useEffect, useState } from 'react'
import ApprovalForm from '../components/ApprovalForm'
import { API_BASE_URL } from '../config/api'
import './PendingRegistrations.css'

const ADMIN_API = `${API_BASE_URL}/api/admin`

function PendingRegistrations() {
  const [registrations, setRegistrations] = useState([])
  const [selectedRegistration, setSelectedRegistration] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [processingId, setProcessingId] = useState(null)

  const loadRegistrations = useCallback((signal) => {
    const token = sessionStorage.getItem('token')

    return fetch(`${ADMIN_API}/pending-registrations`, {
      signal,
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Unable to fetch pending registrations.')
        }

        return response.json()
      })
      .then((data) => {
        if (!signal?.aborted) {
          setRegistrations(data)
        }
      })
      .catch((err) => {
        if (!signal?.aborted) {
          setError(err.message || 'Unable to fetch pending registrations.')
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
    void loadRegistrations(controller.signal)

    return () => {
      controller.abort()
    }
  }, [loadRegistrations])

  function fetchRegistrations() {
    setLoading(true)
    setError('')
    return loadRegistrations()
  }

  async function handleApprove(approvalData) {
    const id = selectedRegistration.id

    try {
      setProcessingId(id)
      setError('')
      setMessage('')

      const token = sessionStorage.getItem('token')

      const response = await fetch(
        `${ADMIN_API}/approve/${id}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(approvalData)
        }
      )

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || 'Registration approval failed.')
      }

      setMessage(
        `${data.message}. Generated Employee Code: ${data.employeeCode}`
      )

      setSelectedRegistration(null)
      await fetchRegistrations()
    } catch (err) {
      setError(err.message || 'Registration approval failed.')
    } finally {
      setProcessingId(null)
    }
  }

  async function handleReject(id) {
    const confirmed = window.confirm(
      'Are you sure you want to reject this registration?'
    )

    if (!confirmed) return

    try {
      setProcessingId(id)
      setError('')
      setMessage('')

      const token = sessionStorage.getItem('token')

      const response = await fetch(
        `${ADMIN_API}/reject/${id}`,
        {
          method: 'PUT',
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || 'Registration rejection failed.')
      }

      setMessage(data.message || 'Registration rejected successfully.')

      await fetchRegistrations()
    } catch (err) {
      setError(err.message || 'Registration rejection failed.')
    } finally {
      setProcessingId(null)
    }
  }

  return (
    <div className="pending-page">
      <div className="pending-header">
        <div>
          <h2>Pending Registrations</h2>
          <p>Review and manage new employee registrations.</p>
        </div>

        <button
          type="button"
          onClick={fetchRegistrations}
          disabled={loading}
        >
          Refresh
        </button>
      </div>

      {message && (
        <p className="pending-success" role="status">
          {message}
        </p>
      )}

      {error && (
        <p className="pending-error" role="alert">
          {error}
        </p>
      )}

      {loading ? (
        <p>Loading registrations...</p>
      ) : registrations.length === 0 ? (
        <p className="empty-message">
          No pending registrations found.
        </p>
      ) : (
        <div className="pending-table-wrapper">
          <table className="pending-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Mobile</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {registrations.map((registration) => (
                <tr key={registration.id}>
                  <td>{registration.id}</td>
                  <td>{registration.fullName}</td>
                  <td>{registration.email}</td>
                  <td>{registration.mobile}</td>
                  <td>
                    <span className="pending-status">
                      {registration.status}
                    </span>
                  </td>
                  <td>
                    <div className="pending-actions">
                      <button
                        type="button"
                        className="approve-button"
                        disabled={processingId === registration.id}
                        onClick={() =>
                          setSelectedRegistration(registration)
                        }
                      >
                        Approve
                      </button>

                      <button
                        type="button"
                        className="reject-button"
                        disabled={processingId === registration.id}
                        onClick={() => handleReject(registration.id)}
                      >
                        Reject
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {selectedRegistration && (
        <ApprovalForm
          registration={selectedRegistration}
          onSubmit={handleApprove}
          onCancel={() => setSelectedRegistration(null)}
          loading={processingId === selectedRegistration.id}
        />
      )}
    </div>
  )
}

export default PendingRegistrations
