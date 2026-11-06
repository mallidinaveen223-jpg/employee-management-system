
import { useEffect, useState } from 'react'
import ApprovalForm from '../components/ApprovalForm'
import './PendingRegistrations.css'

const API_BASE_URL = 'http://localhost:8080/api/admin'

function PendingRegistrations() {
  const [registrations, setRegistrations] = useState([])
  const [selectedRegistration, setSelectedRegistration] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [processingId, setProcessingId] = useState(null)

  async function fetchRegistrations() {
    try {
      setLoading(true)
      setError('')

      const token = sessionStorage.getItem('token')

      const response = await fetch(
        `${API_BASE_URL}/pending-registrations`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      if (!response.ok) {
        throw new Error('Unable to fetch pending registrations.')
      }

      const data = await response.json()
      setRegistrations(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleApprove(approvalData) {
    const id = selectedRegistration.id

    try {
      setProcessingId(id)
      setError('')
      setMessage('')

      const token = sessionStorage.getItem('token')

      const response = await fetch(
        `${API_BASE_URL}/approve/${id}`,
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

      setMessage(data.message || 'Registration approved successfully.')
      setSelectedRegistration(null)

      await fetchRegistrations()
    } catch (err) {
      setError(err.message)
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
        `${API_BASE_URL}/reject/${id}`,
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
      setError(err.message)
    } finally {
      setProcessingId(null)
    }
  }

  useEffect(() => {
    fetchRegistrations()
  }, [])

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
                        onClick={() => setSelectedRegistration(registration)}
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
