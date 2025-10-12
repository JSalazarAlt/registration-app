import React from 'react'

/**
 * Home component - Landing page for the application
 */
const Home = () => {
    return (
        <div style={{
            minHeight: 'calc(100vh - 80px)',
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '2rem'
        }}>
            <div style={{
                background: 'white',
                padding: '3rem',
                borderRadius: '12px',
                boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)',
                textAlign: 'center',
                maxWidth: '600px',
                width: '100%'
            }}>
                <h1 style={{
                    color: '#2d3748',
                    fontSize: '2.5rem',
                    marginBottom: '1.5rem',
                    fontWeight: '700',
                    lineHeight: '1.2'
                }}>
                    Welcome to Our Platform
                </h1>
                <p style={{
                    color: '#4a5568',
                    fontSize: '1.2rem',
                    lineHeight: '1.6',
                    margin: '0',
                    textAlign: 'justify'
                }}>
                    This is the home page of our simple sign up and login application. 
                    You have successfully authenticated and can now access all the features 
                    of our platform. We're glad to have you here and hope you enjoy your 
                    experience with our secure and user-friendly authentication system.
                </p>
                <div style={{
                    marginTop: '2rem',
                    padding: '1rem',
                    background: '#f7fafc',
                    borderRadius: '8px',
                    border: '1px solid #e2e8f0'
                }}>
                    <p style={{
                        color: '#2d3748',
                        fontSize: '1rem',
                        margin: '0',
                        fontWeight: '500'
                    }}>
                        🎉 Authentication successful! You're now logged in.
                    </p>
                </div>
            </div>
        </div>
    )
}

export default Home