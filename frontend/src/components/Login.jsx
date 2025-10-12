import { useState } from 'react'
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

                <div className="auth-switch">
                    <p>
                        Don't have an account?{' '}
                        <button 
                            type="button" 
                            className="link-button"
                            onClick={onSwitchToRegister}
                            disabled={loading}
                        >
                            Sign up here
                        </button>
                    </p>
                </div>
            </form>
        </div>
    )
}

export default Login