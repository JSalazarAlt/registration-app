import React from 'react'

/**
 * Profile component - User profile page
 */
const Profile = ({ user }) => {
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
                maxWidth: '600px',
                width: '100%'
            }}>
                <h1 style={{
                    color: '#2d3748',
                    fontSize: '2.5rem',
                    marginBottom: '2rem',
                    fontWeight: '700',
                    textAlign: 'center'
                }}>
                    User Profile
                </h1>
                
                <div style={{
                    display: 'grid',
                    gap: '1.5rem'
                }}>
                    <div style={{
                        padding: '1rem',
                        background: '#f7fafc',
                        borderRadius: '8px',
                        border: '1px solid #e2e8f0'
                    }}>
                        <label style={{
                            display: 'block',
                            color: '#4a5568',
                            fontSize: '0.9rem',
                            fontWeight: '600',
                            marginBottom: '0.5rem'
                        }}>
                            Full Name
                        </label>
                        <p style={{
                            color: '#2d3748',
                            fontSize: '1.1rem',
                            margin: '0'
                        }}>
                            {user.firstName} {user.lastName}
                        </p>
                    </div>

                    <div style={{
                        padding: '1rem',
                        background: '#f7fafc',
                        borderRadius: '8px',
                        border: '1px solid #e2e8f0'
                    }}>
                        <label style={{
                            display: 'block',
                            color: '#4a5568',
                            fontSize: '0.9rem',
                            fontWeight: '600',
                            marginBottom: '0.5rem'
                        }}>
                            Username
                        </label>
                        <p style={{
                            color: '#2d3748',
                            fontSize: '1.1rem',
                            margin: '0'
                        }}>
                            {user.username}
                        </p>
                    </div>

                    <div style={{
                        padding: '1rem',
                        background: '#f7fafc',
                        borderRadius: '8px',
                        border: '1px solid #e2e8f0'
                    }}>
                        <label style={{
                            display: 'block',
                            color: '#4a5568',
                            fontSize: '0.9rem',
                            fontWeight: '600',
                            marginBottom: '0.5rem'
                        }}>
                            Email Address
                        </label>
                        <p style={{
                            color: '#2d3748',
                            fontSize: '1.1rem',
                            margin: '0'
                        }}>
                            {user.email}
                        </p>
                    </div>

                    {user.phone && (
                        <div style={{
                            padding: '1rem',
                            background: '#f7fafc',
                            borderRadius: '8px',
                            border: '1px solid #e2e8f0'
                        }}>
                            <label style={{
                                display: 'block',
                                color: '#4a5568',
                                fontSize: '0.9rem',
                                fontWeight: '600',
                                marginBottom: '0.5rem'
                            }}>
                                Phone Number
                            </label>
                            <p style={{
                                color: '#2d3748',
                                fontSize: '1.1rem',
                                margin: '0'
                            }}>
                                {user.phone}
                            </p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}

export default Profile