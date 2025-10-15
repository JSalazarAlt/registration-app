import { useState } from 'react'
import { Link } from 'react-router-dom'
import { authAPI } from '../services/api'
import './Auth.css'

/**
 * Login component for user authentication.
 * 
 * Provides a form for users to sign in with their email and password.
 * Handles authentication and stores JWT token for subsequent API requests.
 * 
 * @component
 * @param {Function} onLoginSuccess - Callback function called after successful login
 * @param {Function} onSwitchToRegister - Callback to switch to registration form
 * @author Joel Salazar
 * @since 1.0
 */
function Login({ onLoginSuccess, onSwitchToRegister }) {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    })
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    /**
     * Handles input field changes and updates form state.
     */
    const handleChange = (e) => {
        const { name, value } = e.target
        setFormData(prev => ({
            ...prev,
            [name]: value
        }))
        // Clear error when user starts typing
        if (error) setError('')
    }

    /**
     * Maps backend error messages to user-friendly messages.
     */
    const getErrorMessage = (error) => {
        const message = error.response?.data || error.message || ''
        
        // Handle specific error cases
        if (message.includes('Invalid email or password')) {
            return 'The email or password you entered is incorrect. Please try again.'
        }
        if (message.includes('User not found')) {
            return 'No account found with this email address. Please check your email or sign up.'
        }
        if (message.includes('Account is locked')) {
            return 'Your account has been temporarily locked due to multiple failed login attempts. Please try again later.'
        }
        if (message.includes('Email not verified')) {
            return 'Please verify your email address before signing in. Check your inbox for a verification link.'
        }
        if (message.includes('Account disabled')) {
            return 'Your account has been disabled. Please contact support for assistance.'
        }
        if (error.code === 'NETWORK_ERROR' || message.includes('Network Error')) {
            return 'Unable to connect to our servers. Please check your internet connection and try again.'
        }
        
        // Default error message
        return 'Something went wrong. Please try again or contact support if the problem persists.'
    }

    /**
     * Handles form submission and user authentication.
     */
    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true)
        setError('')

        try {
            const response = await authAPI.login(formData)
            
            // Store JWT token and user info
            localStorage.setItem('token', response.accessToken)
            localStorage.setItem('user', JSON.stringify(response.user))
            
            // Set default authorization header for future requests
            authAPI.setAuthToken(response.accessToken)
            
            onLoginSuccess(response.user)
        } catch (error) {
            console.error('Login error:', error)
            setError(getErrorMessage(error))
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-container">
            <form className="auth-form" onSubmit={handleSubmit}>
                <div className="auth-header">
                    <h2>Welcome Back</h2>
                    <p>Sign in to your account to continue</p>
                </div>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                <div className="form-group">
                    <label htmlFor="email">Email Address</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        placeholder="Enter your email"
                        disabled={loading}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        placeholder="Enter your password"
                        disabled={loading}
                    />
                </div>

                <div className="form-buttons">
                    <button 
                        type="submit" 
                        className="btn-primary"
                        disabled={loading}
                    >
                        {loading ? 'Signing In...' : 'Sign In'}
                    </button>
                </div>

                <div className="divider">
                    <span>or</span>
                </div>

                <div className="oauth-buttons">
                    <button 
                        type="button"
                        className="btn-google"
                        onClick={() => window.location.href = 'http://localhost:8080/oauth2/authorize/google'}
                        disabled={loading}
                    >
                        <svg width="18" height="18" viewBox="0 0 24 24">
                            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                        </svg>
                        Continue with Google
                    </button>
                </div>

                <div className="auth-switch">
                    <p>
                        Don't have an account?{' '}
                        <Link 
                            to="/register"
                            className="link-button"
                            style={{
                                color: '#667eea',
                                textDecoration: 'none',
                                fontWeight: '500'
                            }}
                        >
                            Sign up here
                        </Link>
                    </p>
                </div>
            </form>
        </div>
    )
}

export default Login