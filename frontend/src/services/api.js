import axios from 'axios';

/**
 * Base URL for the expense tracker API endpoints.
 * Points to the Spring Boot backend server.
 * 
 * @constant {string}
 * @author Joel Salazar
 */
const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with base configuration
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add request interceptor to include JWT token
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor to handle authentication errors
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Only reload if this is an authenticated request (has token)
            // Don't reload during login/register attempts
            const token = localStorage.getItem('token');
            if (token) {
                // Clear stored auth data on unauthorized response
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                // Redirect to login or trigger auth state update
                window.location.reload();
            }
        }
        return Promise.reject(error);
    }
);

/**
 * Authentication API service for user login, registration, and JWT management.
 * 
 * @namespace authAPI
 * @author Joel Salazar
 */
export const authAPI = {
    /**
     * Authenticates user login and returns JWT token.
     * 
     * @function login
     * @param {Object} credentials - User login credentials
     * @param {string} credentials.email - User's email address
     * @param {string} credentials.password - User's password
     * @returns {Promise<Object>} Promise resolving to authentication response with token and user info
     */
    login: async (credentials) => {
        const response = await apiClient.post('/v1/auth/login', credentials);
        return response.data;
    },

    /**
     * Registers a new user account.
     * 
     * @function register
     * @param {Object} userData - New user registration data
     * @param {string} userData.email - User's email address
     * @param {string} userData.password - User's password
     * @param {string} userData.username - User's chosen username
     * @param {string} userData.firstName - User's first name
     * @param {string} userData.lastName - User's last name
     * @param {boolean} userData.termsAccepted - Terms acceptance flag
     * @param {boolean} userData.privacyPolicyAccepted - Privacy policy acceptance flag
     * @returns {Promise<Object>} Promise resolving to created user profile
     */
    register: async (userData) => {
        const response = await apiClient.post('/v1/auth/register', userData);
        return response.data;
    },

    /**
     * Sets the authorization token for API requests.
     * 
     * @function setAuthToken
     * @param {string} token - JWT token to set
     */
    setAuthToken: (token) => {
        if (token) {
            apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        } else {
            delete apiClient.defaults.headers.common['Authorization'];
        }
    },

    /**
     * Logs out the current user by invalidating token and clearing stored data.
     * 
     * @function logout
     */
    logout: async () => {
        try {
            // Call backend logout to blacklist token
            await apiClient.post('/v1/auth/logout');
        } catch (error) {
            // Continue with local logout even if backend call fails
            console.warn('Backend logout failed:', error.message);
        } finally {
            // Always clear local storage
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            delete apiClient.defaults.headers.common['Authorization'];
        }
    }
};

/**
 * User profile API service for profile management operations.
 * 
 * @namespace userAPI
 * @author Joel Salazar
 */
export const userAPI = {
    /**
     * Retrieves user profile information.
     * 
     * @function getProfile
     * @param {number} userId - User's unique identifier
     * @returns {Promise<Object>} Promise resolving to user profile data
     */
    getProfile: async (userId) => {
        const response = await apiClient.get(`/v1/users/${userId}/profile`);
        return response.data;
    },

    /**
     * Updates user profile information.
     * 
     * @function updateProfile
     * @param {number} userId - User's unique identifier
     * @param {Object} profileData - Updated profile information
     * @returns {Promise<Object>} Promise resolving to updated user profile
     */
    updateProfile: async (userId, profileData) => {
        const response = await apiClient.put(`/v1/users/${userId}/profile`, profileData);
        return response.data;
    }
};