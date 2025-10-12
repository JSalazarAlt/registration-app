import React from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'

/**
 * Professional navigation bar component
 */
const Navbar = ({ user, onLogout }) => {
    const location = useLocation()
    const navigate = useNavigate()

    const handleLogout = () => {
        onLogout()
        navigate('/login')
    }

    const navItems = [
        { path: '/', label: 'Home', icon: '🏠' },
        { path: '/profile', label: 'Profile', icon: '👤' }
    ]

    return (
        <nav style={{
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            padding: '0 2rem',
            boxShadow: '0 2px 10px rgba(0, 0, 0, 0.1)',
            position: 'sticky',
            top: 0,
            zIndex: 1000
        }}>
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                maxWidth: '1200px',
                margin: '0 auto',
                height: '70px'
            }}>
                {/* Logo/Brand */}
                <Link 
                    to="/"
                    style={{
                        color: 'white',
                        textDecoration: 'none',
                        fontSize: '1.5rem',
                        fontWeight: 'bold',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.5rem'
                    }}
                >
                    🔐 Auth App
                </Link>

                {/* Navigation Links */}
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '2rem'
                }}>
                    {navItems.map(item => (
                        <Link
                            key={item.path}
                            to={item.path}
                            style={{
                                color: 'white',
                                textDecoration: 'none',
                                padding: '0.5rem 1rem',
                                borderRadius: '6px',
                                background: location.pathname === item.path ? 'rgba(255, 255, 255, 0.2)' : 'transparent',
                                transition: 'all 0.3s ease',
                                display: 'flex',
                                alignItems: 'center',
                                gap: '0.5rem',
                                fontSize: '0.95rem'
                            }}
                        >
                            <span>{item.icon}</span>
                            {item.label}
                        </Link>
                    ))}
                </div>

                {/* User Menu */}
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '1rem'
                }}>
                    <span style={{
                        color: 'white',
                        fontSize: '0.9rem',
                        opacity: 0.9
                    }}>
                        Welcome, {user.firstName}!
                    </span>
                    <button
                        onClick={handleLogout}
                        style={{
                            background: 'rgba(255, 255, 255, 0.2)',
                            color: 'white',
                            border: '1px solid rgba(255, 255, 255, 0.3)',
                            padding: '0.5rem 1rem',
                            borderRadius: '6px',
                            cursor: 'pointer',
                            fontSize: '0.9rem',
                            transition: 'all 0.3s ease'
                        }}
                        onMouseOver={(e) => {
                            e.target.style.background = 'rgba(255, 255, 255, 0.3)'
                        }}
                        onMouseOut={(e) => {
                            e.target.style.background = 'rgba(255, 255, 255, 0.2)'
                        }}
                    >
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    )
}

export default Navbar