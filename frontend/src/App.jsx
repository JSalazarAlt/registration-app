import { useCallback, useEffect, useState } from 'react'
import Login from './components/Login'
import Register from './components/Register'
import Home from './components/Home'
import Profile from './components/Profile'
import './App.css'
import { authAPI } from './services/api'

// Auth view types
const AUTH_VIEWS = {
    LOGIN: 'LOGIN',
    REGISTER: 'REGISTER'
}

/**
 * Main application component managing expense tracker state, navigation, and authentication.
 * 
 * This component orchestrates the entire expense tracking application,
 * managing authentication state, showing login/register screens for unauthenticated users,
 * and the main expense management interface for authenticated users.
 * 
 * @component
 * @author Joel Salazar
 * @since 1.0
 */
function App() {
    
    const [user, setUser] = useState(null)
    const [authView, setAuthView] = useState(AUTH_VIEWS.LOGIN)
    const [loading, setLoading] = useState(true)
    const [currentView, setCurrentView] = useState('home')
    
    // Check for existing authentication on app load
    useEffect(() => {
        localStorage.clear() // Temporary - remove this after testing
        const token = localStorage.getItem('token')
        const userData = localStorage.getItem('user')
        
        if (token && userData) {
            try {
                const parsedUser = JSON.parse(userData)
                setUser(parsedUser)
                authAPI.setAuthToken(token)
            } catch (error) {
                console.error('Error parsing stored user data:', error)
                localStorage.removeItem('token')
                localStorage.removeItem('user')
            }
        }
        
        setLoading(false)
    }, [])
    
    /**
     * Handles successful login/registration.
     */
    const handleAuthSuccess = useCallback((userData) => {
        setUser(userData)
    }, [])
    
    /**
     * Handles user logout.
     */
    const handleLogout = useCallback(() => {
        authAPI.logout()
        setUser(null)
        setCurrentView('home')
    }, [])
    
    /**
     * Switches between login and register views.
     */
    const handleSwitchAuthView = useCallback((view) => {
        setAuthView(view)
    }, [])

    // Show loading spinner while checking authentication
    if (loading) {
        return (
            <div style={{ 
                display: 'flex', 
                justifyContent: 'center', 
                alignItems: 'center', 
                height: '100vh',
                fontSize: '18px',
                color: 'var(--gray-600)'
            }}>
                Loading...
            </div>
        )
    }
    
    // Show authentication screens if user is not logged in
    if (!user) {
        return authView === AUTH_VIEWS.LOGIN ? (
            <Login 
                onLoginSuccess={handleAuthSuccess}
                onSwitchToRegister={() => handleSwitchAuthView(AUTH_VIEWS.REGISTER)}
            />
        ) : (
            <Register 
                onRegisterSuccess={handleAuthSuccess}
                onSwitchToLogin={() => handleSwitchAuthView(AUTH_VIEWS.LOGIN)}
            />
        )
    }
    
    // Show main application if user is authenticated
    return (
        <div>
            {/* Header with user info and logout */}
            <header style={{
                background: 'white',
                padding: 'var(--space-md) var(--space-lg)',
                borderBottom: '1px solid var(--gray-200)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}>
                <button 
                    onClick={() => setCurrentView('home')}
                    style={{
                        background: 'none',
                        border: 'none',
                        margin: 0,
                        color: '#2d3748',
                        fontSize: '1.5rem',
                        fontWeight: 'bold',
                        cursor: 'pointer'
                    }}
                >
                    Sign Up - Login App
                </button>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button
                        onClick={() => setCurrentView('profile')}
                        style={{
                            padding: '0.5rem 1rem',
                            background: currentView === 'profile' ? '#667eea' : 'transparent',
                            color: currentView === 'profile' ? 'white' : '#4a5568',
                            border: '1px solid #e2e8f0',
                            borderRadius: '6px',
                            cursor: 'pointer',
                            fontSize: '0.9rem'
                        }}
                    >
                        Profile
                    </button>
                    <span style={{ color: '#4a5568' }}>Welcome, {user.firstName}!</span>
                    <button 
                        onClick={handleLogout}
                        style={{
                            padding: '0.5rem 1rem',
                            background: '#ef4444',
                            color: 'white',
                            border: 'none',
                            borderRadius: '6px',
                            cursor: 'pointer',
                            fontSize: '0.9rem'
                        }}
                    >
                        Logout
                    </button>
                </div>
            </header>
            
            {/* Main content */}
            {currentView === 'home' ? <Home /> : <Profile user={user} />}
        </div>
    )
}

export default App