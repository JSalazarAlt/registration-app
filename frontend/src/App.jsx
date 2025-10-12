import { useCallback, useEffect, useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './components/Login'
import Register from './components/Register'
import Home from './components/Home'
import Profile from './components/Profile'
import Navbar from './components/Navbar'
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
    const [loading, setLoading] = useState(true)
    
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
     * Handles user profile updates.
     */
    const handleUserUpdate = useCallback((updatedUser) => {
        setUser(updatedUser)
        localStorage.setItem('user', JSON.stringify(updatedUser))
    }, [])
    
    /**
     * Handles user logout.
     */
    const handleLogout = useCallback(() => {
        authAPI.logout()
        setUser(null)
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
    
    // Show main application
    return (
        <Router>
            {!user ? (
                <Routes>
                    <Route path="/login" element={<Login onLoginSuccess={handleAuthSuccess} />} />
                    <Route path="/register" element={<Register onRegisterSuccess={handleAuthSuccess} />} />
                    <Route path="*" element={<Navigate to="/login" replace />} />
                </Routes>
            ) : (
                <>
                    <Navbar user={user} onLogout={handleLogout} />
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/profile" element={<Profile user={user} onUserUpdate={handleUserUpdate} />} />
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </>
            )}
        </Router>
    )
}

export default App