import { useState } from 'react'
import { Link } from 'react-router-dom'
import { authAPI } from '../services/api'
import './Auth.css'

/**
 * Registration component for new user signup.
 * 
 * Provides a form for users to create a new account with required information.
 * Handles user registration and automatically logs them in after successful signup.
 * 
 * @component
 * @param {Function} onRegisterSuccess - Callback function called after successful registration
 * @param {Function} onSwitchToLogin - Callback to switch to login form
 * @author Joel Salazar
 * @since 1.0
 */
function Register({ onRegisterSuccess, onSwitchToLogin }) {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        username: '',
        firstName: '',
        lastName: '',
        termsAccepted: false,
        privacyPolicyAccepted: false
    })
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    /**
     * Handles input field changes and updates form state.
     */
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
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
        if (message.includes('Email already registered') || message.includes('already exists')) {
            return 'An account with this email already exists. Please sign in instead or use a different email.'
        }
        if (message.includes('Invalid email format')) {
            return 'Please enter a valid email address (e.g., user@example.com).'
        }
        if (message.includes('Password too weak') || message.includes('password requirements')) {
            return 'Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.'
        }
        if (message.includes('Username already taken')) {
            return 'This username is already taken. Please choose a different one.'
        }
        if (message.includes('Invalid username')) {
            return 'Username must be 3-20 characters long and contain only letters, numbers, and underscores.'
        }
        if (error.code === 'NETWORK_ERROR' || message.includes('Network Error')) {
            return 'Unable to connect to our servers. Please check your internet connection and try again.'
        }
        
        // Default error message
        return 'Unable to create your account. Please try again or contact support if the problem persists.'
    }

    /**
     * Validates form data before submission.
     */
    const validateForm = () => {
        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(formData.email)) {
            setError('Please enter a valid email address (e.g., user@example.com).')
            return false
        }
        
        // Password validation
        if (formData.password.length < 8) {
            setError('Password must be at least 8 characters long.')
            return false
        }
        
        // Strong password validation
        const hasUpperCase = /[A-Z]/.test(formData.password)
        const hasLowerCase = /[a-z]/.test(formData.password)
        const hasNumbers = /\d/.test(formData.password)
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(formData.password)
        
        if (!hasUpperCase || !hasLowerCase || !hasNumbers || !hasSpecialChar) {
            setError('Password must include uppercase letter, lowercase letter, number, and special character.')
            return false
        }
        
        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match. Please make sure both passwords are identical.')
            return false
        }
        
        // Username validation
        const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
        if (!usernameRegex.test(formData.username)) {
            setError('Username must be 3-20 characters long and contain only letters, numbers, and underscores.')
            return false
        }
        
        // Name validation
        if (formData.firstName.trim().length < 2) {
            setError('First name must be at least 2 characters long.')
            return false
        }
        
        if (formData.lastName.trim().length < 2) {
            setError('Last name must be at least 2 characters long.')
            return false
        }
        
        if (!formData.termsAccepted) {
            setError('Please accept the Terms of Service to continue.')
            return false
        }
        if (!formData.privacyPolicyAccepted) {
            setError('Please accept the Privacy Policy to continue.')
            return false
        }
        return true
    }

    /**
     * Handles form submission and user registration.
     */
    const handleSubmit = async (e) => {
        e.preventDefault()
        
        if (!validateForm()) {
            return
        }

        setLoading(true)
        setError('')

        try {
            // Remove confirmPassword from submission data
            const { confirmPassword, ...registrationData } = formData
            
            const response = await authAPI.register(registrationData)
            
            // Auto-login after successful registration
            const loginResponse = await authAPI.login({
                email: formData.email,
                password: formData.password
            })
            
            // Store JWT token and user info
            localStorage.setItem('token', loginResponse.accessToken)
            localStorage.setItem('user', JSON.stringify(loginResponse.user))
            
            // Set default authorization header for future requests
            authAPI.setAuthToken(loginResponse.accessToken)
            
            onRegisterSuccess(loginResponse.user)
        } catch (error) {
            console.error('Registration error:', error)
            setError(getErrorMessage(error))
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-container">
            <form className="auth-form" onSubmit={handleSubmit}>
                <div className="auth-header">
                    <h2>Create Account</h2>
                    <p>Join us to start tracking your expenses</p>
                </div>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="firstName">First Name</label>
                        <input
                            type="text"
                            id="firstName"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                            placeholder="Enter your first name"
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="lastName">Last Name</label>
                        <input
                            type="text"
                            id="lastName"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                            placeholder="Enter your last name"
                            disabled={loading}
                        />
                    </div>
                </div>

                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                        placeholder="Choose a username"
                        disabled={loading}
                    />
                </div>

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

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            placeholder="Create a password"
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirm Password</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                            placeholder="Confirm your password"
                            disabled={loading}
                        />
                    </div>
                </div>

                <div className="checkbox-group">
                    <label className="checkbox-label">
                        <input
                            type="checkbox"
                            name="termsAccepted"
                            checked={formData.termsAccepted}
                            onChange={handleChange}
                            required
                            disabled={loading}
                        />
                        I accept the Terms of Service
                    </label>
                </div>

                <div className="checkbox-group">
                    <label className="checkbox-label">
                        <input
                            type="checkbox"
                            name="privacyPolicyAccepted"
                            checked={formData.privacyPolicyAccepted}
                            onChange={handleChange}
                            required
                            disabled={loading}
                        />
                        I accept the Privacy Policy
                    </label>
                </div>

                <div className="form-buttons">
                    <button 
                        type="submit" 
                        className="btn-primary"
                        disabled={loading}
                    >
                        {loading ? 'Creating Account...' : 'Create Account'}
                    </button>
                </div>

                <div className="auth-switch">
                    <p>
                        Already have an account?{' '}
                        <Link 
                            to="/login"
                            className="link-button"
                            style={{
                                color: '#667eea',
                                textDecoration: 'none',
                                fontWeight: '500'
                            }}
                        >
                            Sign in here
                        </Link>
                    </p>
                </div>
            </form>
        </div>
    )
}

export default Register