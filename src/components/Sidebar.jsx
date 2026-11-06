
import './Sidebar.css'

function Sidebar({ activePage, onNavigate }) {
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'employees', label: 'Employees' },
    { id: 'pending-registrations', label: 'Pending Registrations' },
    { id: 'departments', label: 'Departments' },
    { id: 'settings', label: 'Settings' }
  ]

  return (
    <aside className="sidebar">
      <h2>EMS</h2>

      <nav>
        {menuItems.map((item) => (
          <button
            key={item.id}
            type="button"
            className={`sidebar-link ${
              activePage === item.id ? 'active' : ''
            }`}
            onClick={() => onNavigate(item.id)}
          >
            {item.label}
          </button>
        ))}
      </nav>
    </aside>
  )
}

export default Sidebar
