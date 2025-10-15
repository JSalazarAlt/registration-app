import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { authAPI } from '../services/api'

/**
 * OAuth2 redirect handler component.
 * 
 * Handles the OAuth2 callback from Google authentication,
 * extracts the JWT token from URL parameters, and redirects to home.
 * 
 * @component
 * @param {Function} onLoginSuccess - Callback function called after successful OAuth2 login
 * @author Joel Salazar
 */
function OAuth2Redirect({ onLoginSuccess }) {
    const navigate = useNavigate()
    const [searchParams] = useSearchParams()

    useEffect(() => {
        const token = searchParams.get('token')
        const error = searchParams.get('error')

        if (error) {
            console.error('OAuth2 error:', error)
            navigate('/login?error=oauth2_failed')
            return
        }

        if (token) {
            try {
                // Store JWT token and set auth header
                localStorage.setItem('token', token)
                authAPI.setAuthToken(token)
                
                // Extract user data from URL parameters
                const firstName = searchParams.get('firstName')
                const lastName = searchParams.get('lastName')
                const email = searchParams.get('email')
                const userId = searchParams.get('userId')
                
                const user = {
                    id: userId,
                    firstName: firstName,
                    lastName: lastName,
                    email: email
                }
                
                localStorage.setItem('user', JSON.stringify(user))
                
                onLoginSuccess(user)
                navigate('/')
            } catch (error) {
                console.error('Error processing OAuth2 token:', error)
                navigate('/login?error=token_processing_failed')
            }
        } else {
            navigate('/login?error=no_token_received')
        }
    }, [searchParams, navigate, onLoginSuccess])

    return (
        <div className="auth-container">
            <div className="auth-form">
                <div className="auth-header">
                    <h2>Processing Login...</h2>
                    <p>Please wait while we complete your authentication.</p>
                </div>
                <div style={{ textAlign: 'center', padding: '2rem' }}>
                    <div className="loading-spinner">🔄</div>
                </div>
            </div>
        </div>
    )
}

export default OAuth2Redirect