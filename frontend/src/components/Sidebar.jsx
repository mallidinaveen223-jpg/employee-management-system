
import './Sidebar.css'

function Sidebar({ activePage, onNavigate }) {
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: '▦' },
    { id: 'employees', label: 'Employees', icon: '♙' },
    {
      id: 'pending-registrations',
      label: 'Registration Approvals',
      icon: '✓'
    },
    { id: 'departments', label: 'Departments', icon: '▤' },
    { id: 'settings', label: 'Settings', icon: '⚙' }
  ]

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-logo">T</div>

        <div className="brand-details">
          <h2>TATTVAM</h2>
          <span>GROUPS</span>
        </div>
      </div>

      <div className="sidebar-section-title">
        MAIN MENU
      </div>

      <nav className="sidebar-nav">
        {menuItems.map((item) => (
          <button
            key={item.id}
            type="button"
            className={`sidebar-link ${
              activePage === item.id ? 'active' : ''
            }`}
            onClick={() => onNavigate(item.id)}
          >
            <span className="sidebar-icon">{item.icon}</span>
            <span>{item.label}</span>
          </button>
        ))}
      </nav>

      <div className="sidebar-footer">
        <span>HR MANAGEMENT SYSTEM</span>
        <small>Version 1.0</small>
      </div>
    </aside>
  )
}

export default Sidebar
