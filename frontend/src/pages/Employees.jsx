
import { useEffect, useState } from 'react'
import './Employees.css'

const API_URL = 'http://localhost:8080/api/admin/employees'

const emptyForm = {
  name: '',
  email: '',
  mobile: '',
  department: '',
  designation: '',
  salary: '',
  employeeCode: '',
  joiningDate: '',
  employmentStatus: 'ACTIVE'
}

function Employees() {
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [search, setSearch] = useState('')
  const [editingEmployee, setEditingEmployee] = useState(null)
  const [formData, setFormData] = useState(emptyForm)
  const [isSaving, setIsSaving] = useState(false)

  const token = sessionStorage.getItem('token')

  async function fetchEmployees() {
    try {
      setLoading(true)
      setError('')

      const response = await fetch(API_URL, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })

      if (!response.ok) {
        throw new Error('Unable to fetch employees.')
      }

      const data = await response.json()
      setEmployees(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function handleEdit(employee) {
    setEditingEmployee(employee)

    setFormData({
      name: employee.name || '',
      email: employee.email || '',
      mobile: employee.mobile || '',
      department: employee.department || '',
      designation: employee.designation || '',
      salary: employee.salary ?? '',
      employeeCode: employee.employeeCode || '',
      joiningDate: employee.joiningDate
        ? employee.joiningDate.substring(0, 10)
        : '',
      employmentStatus: employee.employmentStatus || employee.employeeStatus || 'ACTIVE'
    })

    setError('')
    setMessage('')
  }

  function handleChange(event) {
    const { name, value } = event.target

    setFormData((previous) => ({
      ...previous,
      [name]: value
    }))
  }

  async function handleUpdate(event) {
    event.preventDefault()

    try {
      setIsSaving(true)
      setError('')
      setMessage('')

      const requestBody = {
        ...formData,
        salary: Number(formData.salary),
        joiningDate: formData.joiningDate
          ? `${formData.joiningDate}T09:00:00`
          : null
      }

      const response = await fetch(
        `${API_URL}/${editingEmployee.id}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(requestBody)
        }
      )

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || 'Employee update failed.')
      }

      setMessage('Employee updated successfully!')
      setEditingEmployee(null)
      setFormData(emptyForm)

      await fetchEmployees()
    } catch (err) {
      setError(err.message)
    } finally {
      setIsSaving(false)
    }
  }

  async function deleteEmployee(id) {
    if (!window.confirm('Are you sure you want to delete this employee?')) {
      return
    }

    try {
      setError('')
      setMessage('')

      const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || 'Unable to delete employee.')
      }

      setMessage('Employee deleted successfully!')
      await fetchEmployees()
    } catch (err) {
      setError(err.message)
    }
  }

  useEffect(() => {
    fetchEmployees()
  }, [])

  const filteredEmployees = employees.filter((employee) =>
    [
      employee.name,
      employee.email,
      employee.employeeCode,
      employee.designation,
      employee.department
    ]
      .filter(Boolean)
      .some((value) =>
        value.toLowerCase().includes(search.toLowerCase())
      )
  )

  return (
    <div className="employees-page">
      <div className="employees-header">
        <div>
          <h2>Employee Management</h2>
          <p>View and manage employee records.</p>
        </div>

        <button onClick={fetchEmployees} disabled={loading}>
          Refresh
        </button>
      </div>

      {message && (
        <p className="employee-success" role="status">
          {message}
        </p>
      )}

      {error && (
        <p className="employee-error" role="alert">
          {error}
        </p>
      )}

      {editingEmployee && (
        <form className="employee-edit-form" onSubmit={handleUpdate}>
          <h3>Edit Employee</h3>

          <div className="employee-form-grid">
            <div>
              <label>Employee Name</label>
              <input
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Email Address</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Mobile Number</label>
              <input
                name="mobile"
                value={formData.mobile}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Employee Code</label>
              <input
                name="employeeCode"
                value={formData.employeeCode}
                readOnly
              />
            </div>

            <div>
              <label>Department</label>
              <input
                name="department"
                value={formData.department}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Designation</label>
              <input
                name="designation"
                value={formData.designation}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Salary</label>
              <input
                type="number"
                name="salary"
                min="0"
                value={formData.salary}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Joining Date</label>
              <input
                type="date"
                name="joiningDate"
                value={formData.joiningDate}
                onChange={handleChange}
              />
            </div>

            <div>
              <label>Employment Status</label>
              <select
                name="employmentStatus"
                value={formData.employmentStatus}
                onChange={handleChange}
                required
              >
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
                <option value="ON_LEAVE">ON LEAVE</option>
                <option value="TERMINATED">TERMINATED</option>
              </select>
            </div>
          </div>

          <div className="employee-form-actions">
            <button type="submit" disabled={isSaving}>
              {isSaving ? 'Updating...' : 'Update Employee'}
            </button>

            <button
              type="button"
              className="employee-cancel-button"
              onClick={() => {
                setEditingEmployee(null)
                setFormData(emptyForm)
              }}
              disabled={isSaving}
            >
              Cancel
            </button>
          </div>
        </form>
      )}

      <input
        className="employee-search"
        type="search"
        placeholder="Search by name, email, code or designation..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {loading ? (
        <p>Loading employees...</p>
      ) : filteredEmployees.length === 0 ? (
        <p className="employee-empty">No employees found.</p>
      ) : (
        <div className="employees-table-wrapper">
          <table className="employees-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee Code</th>
                <th>Name</th>
                <th>Email</th>
                <th>Department</th>
                <th>Designation</th>
                <th>Salary</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredEmployees.map((employee) => (
                <tr key={employee.id}>
                  <td>{employee.id}</td>
                  <td>{employee.employeeCode}</td>
                  <td>{employee.name}</td>
                  <td>{employee.email}</td>
                  <td>{employee.department}</td>
                  <td>{employee.designation}</td>
                  <td>
                    {employee.salary?.toLocaleString('en-IN')}
                  </td>
                  <td>
                    <div className="employee-row-actions">
                      <button
                        className="employee-edit-button"
                        onClick={() => handleEdit(employee)}
                      >
                        Edit
                      </button>

                      <button
                        className="employee-delete-button"
                        onClick={() => deleteEmployee(employee.id)}
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default Employees
