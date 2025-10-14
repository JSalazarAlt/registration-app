import React, { useState } from 'react'
import { userAPI } from '../services/api'

/**
 * Profile component - User profile page with editing capabilities
 */
const Profile = ({ user, onUserUpdate }) => {
    const [isEditing, setIsEditing] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [formData, setFormData] = useState({
        username: user.username || '',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        phone: user.phone || ''
    })

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        })
    }

    const handleSave = async () => {
        setLoading(true)
        setError('')
        
        try {
            const updatedUser = await userAPI.updateProfile(user.id, formData)
            onUserUpdate(updatedUser)
            setIsEditing(false)
        } catch (error) {
            setError('Failed to update profile. Please try again.')
        } finally {
            setLoading(false)
        }
    }

    const handleCancel = () => {
        setFormData({
            username: user.username || '',
            firstName: user.firstName || '',
            lastName: user.lastName || '',
            phone: user.phone || ''
        })
        setIsEditing(false)
        setError('')
    }

    return (
        <div style={{
            minHeight: 'calc(100vh - 70px)',
            background: '#f8fafc',
            padding: '2rem'
        }}>
            <div style={{
                maxWidth: '800px',
                margin: '0 auto'
            }}>
                <div style={{
                    background: 'white',
                    padding: '2rem',
                    borderRadius: '12px',
                    boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
                }}>
                    <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        marginBottom: '2rem'
                    }}>
                        <h1 style={{
                            color: '#2d3748',
                            fontSize: '2rem',
                            fontWeight: '700',
                            margin: '0'
                        }}>
                            Profile Settings
                        </h1>
                        {!isEditing ? (
                            <button
                                onClick={() => setIsEditing(true)}
                                style={{
                                    background: '#667eea',
                                    color: 'white',
                                    border: 'none',
                                    padding: '0.75rem 1.5rem',
                                    borderRadius: '6px',
                                    cursor: 'pointer',
                                    fontSize: '0.9rem',
                                    fontWeight: '500'
                                }}
                            >
                                Edit Profile
                            </button>
                        ) : (
                            <div style={{ display: 'flex', gap: '0.5rem' }}>
                                <button
                                    onClick={handleCancel}
                                    disabled={loading}
                                    style={{
                                        background: '#e2e8f0',
                                        color: '#4a5568',
                                        border: 'none',
                                        padding: '0.75rem 1.5rem',
                                        borderRadius: '6px',
                                        cursor: 'pointer',
                                        fontSize: '0.9rem'
                                    }}
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleSave}
                                    disabled={loading}
                                    style={{
                                        background: '#48bb78',
                                        color: 'white',
                                        border: 'none',
                                        padding: '0.75rem 1.5rem',
                                        borderRadius: '6px',
                                        cursor: 'pointer',
                                        fontSize: '0.9rem'
                                    }}
                                >
                                    {loading ? 'Saving...' : 'Save Changes'}
                                </button>
                            </div>
                        )}
                    </div>

                    {error && (
                        <div style={{
                            background: '#fed7d7',
                            color: '#c53030',
                            padding: '1rem',
                            borderRadius: '6px',
                            marginBottom: '1.5rem',
                            fontSize: '0.9rem'
                        }}>
                            {error}
                        </div>
                    )}
                    
                    <div style={{
                        display: 'grid',
                        gap: '1.5rem'
                    }}>
                        <div style={{
                            display: 'grid',
                            gridTemplateColumns: '1fr 1fr',
                            gap: '1rem'
                        }}>
                            <div>
                                <label style={{
                                    display: 'block',
                                    color: '#4a5568',
                                    fontSize: '0.9rem',
                                    fontWeight: '600',
                                    marginBottom: '0.5rem'
                                }}>
                                    First Name
                                </label>
                                {isEditing ? (
                                    <input
                                        type="text"
                                        name="firstName"
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #e2e8f0',
                                            borderRadius: '6px',
                                            fontSize: '1rem'
                                        }}
                                    />
                                ) : (
                                    <p style={{
                                        color: '#2d3748',
                                        fontSize: '1rem',
                                        margin: '0',
                                        padding: '0.75rem',
                                        background: '#f7fafc',
                                        borderRadius: '6px'
                                    }}>
                                        {user.firstName}
                                    </p>
                                )}
                            </div>

                            <div>
                                <label style={{
                                    display: 'block',
                                    color: '#4a5568',
                                    fontSize: '0.9rem',
                                    fontWeight: '600',
                                    marginBottom: '0.5rem'
                                }}>
                                    Last Name
                                </label>
                                {isEditing ? (
                                    <input
                                        type="text"
                                        name="lastName"
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #e2e8f0',
                                            borderRadius: '6px',
                                            fontSize: '1rem'
                                        }}
                                    />
                                ) : (
                                    <p style={{
                                        color: '#2d3748',
                                        fontSize: '1rem',
                                        margin: '0',
                                        padding: '0.75rem',
                                        background: '#f7fafc',
                                        borderRadius: '6px'
                                    }}>
                                        {user.lastName}
                                    </p>
                                )}
                            </div>
                        </div>

                        <div>
                            <label style={{
                                display: 'block',
                                color: '#4a5568',
                                fontSize: '0.9rem',
                                fontWeight: '600',
                                marginBottom: '0.5rem'
                            }}>
                                Username
                            </label>
                            {isEditing ? (
                                <input
                                    type="text"
                                    name="username"
                                    value={formData.username}
                                    onChange={handleChange}
                                    style={{
                                        width: '100%',
                                        padding: '0.75rem',
                                        border: '1px solid #e2e8f0',
                                        borderRadius: '6px',
                                        fontSize: '1rem'
                                    }}
                                />
                            ) : (
                                <p style={{
                                    color: '#2d3748',
                                    fontSize: '1rem',
                                    margin: '0',
                                    padding: '0.75rem',
                                    background: '#f7fafc',
                                    borderRadius: '6px'
                                }}>
                                    {user.username}
                                </p>
                            )}
                        </div>

                        <div>
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
                                fontSize: '1rem',
                                margin: '0',
                                padding: '0.75rem',
                                background: '#f7fafc',
                                borderRadius: '6px',
                                opacity: 0.7
                            }}>
                                {user.email} <span style={{ fontSize: '0.8rem', color: '#718096' }}>(Cannot be changed)</span>
                            </p>
                        </div>

                        <div>
                            <label style={{
                                display: 'block',
                                color: '#4a5568',
                                fontSize: '0.9rem',
                                fontWeight: '600',
                                marginBottom: '0.5rem'
                            }}>
                                Phone Number
                            </label>
                            {isEditing ? (
                                <input
                                    type="tel"
                                    name="phone"
                                    value={formData.phone}
                                    onChange={handleChange}
                                    placeholder="Enter phone number"
                                    style={{
                                        width: '100%',
                                        padding: '0.75rem',
                                        border: '1px solid #e2e8f0',
                                        borderRadius: '6px',
                                        fontSize: '1rem'
                                    }}
                                />
                            ) : (
                                <p style={{
                                    color: '#2d3748',
                                    fontSize: '1rem',
                                    margin: '0',
                                    padding: '0.75rem',
                                    background: '#f7fafc',
                                    borderRadius: '6px'
                                }}>
                                    {user.phone || 'Not provided'}
                                </p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Profile