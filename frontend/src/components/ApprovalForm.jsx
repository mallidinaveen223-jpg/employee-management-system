
import { useState } from 'react'
import './ApprovalForm.css'

function ApprovalForm({ registration, onSubmit, onCancel, loading }) {
  const [formData, setFormData] = useState({
    employeeCode: '',
    department: '',
    designation: '',
    salary: '',
    joiningDate: ''
  })

  function handleChange(event) {
    const { name, value } = event.target

    setFormData((previous) => ({
      ...previous,
      [name]: value
    }))
  }

  function handleSubmit(event) {
    event.preventDefault()

    onSubmit({
      ...formData,
      salary: Number(formData.salary),
      joiningDate: `${formData.joiningDate}T09:00:00`
    })
  }

  return (
    <div className="approval-overlay">
      <div className="approval-card">
        <h2>Approve Registration</h2>

        <p>
          Employee: <strong>{registration.fullName}</strong>
        </p>
        <p>Email: {registration.email}</p>

        <form onSubmit={handleSubmit}>
          <label>Employee Code</label>
          <input
            name="employeeCode"
            value={formData.employeeCode}
            onChange={handleChange}
            placeholder="EMP1002"
            required
          />

          <label>Department</label>
          <input
            name="department"
            value={formData.department}
            onChange={handleChange}
            placeholder="Engineering"
            required
          />

          <label>Designation</label>
          <input
            name="designation"
            value={formData.designation}
            onChange={handleChange}
            placeholder="Java Developer"
            required
          />

          <label>Salary</label>
          <input
            type="number"
            name="salary"
            value={formData.salary}
            onChange={handleChange}
            min="0"
            placeholder="50000"
            required
          />

          <label>Joining Date</label>
          <input
            type="date"
            name="joiningDate"
            value={formData.joiningDate}
            onChange={handleChange}
            required
          />

          <div className="approval-actions">
            <button type="submit" disabled={loading}>
              {loading ? 'Approving...' : 'Approve'}
            </button>

            <button
              type="button"
              className="cancel-button"
              onClick={onCancel}
              disabled={loading}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default ApprovalForm
