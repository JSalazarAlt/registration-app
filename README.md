# Registration App v2.0.0

A full-stack authentication application built with React and Spring Boot, featuring user registration, login, JWT-based authentication, and OAuth2 integration.

## 🚀 Features

- **User Registration** - Create new accounts with comprehensive validation
- **User Login** - Secure authentication with JWT tokens
- **OAuth2 Authentication** - Google OAuth2 integration for seamless login
- **Profile Management** - View and edit user profile information
- **JWT Authentication** - Stateless authentication using JSON Web Tokens
- **Professional Navigation** - React Router with URL-based routing
- **Responsive Design** - Modern gradient UI with professional styling
- **Security Features** - Account locking, failed login tracking, password encryption
- **Real-time Validation** - Client-side form validation with error handling

## 🛠️ Tech Stack

### Frontend
- **React** - User interface library
- **React Router** - Client-side routing and navigation
- **JavaScript (ES6+)** - Modern JavaScript features
- **CSS3** - Styling and responsive design
- **Vite** - Fast build tool and development server

### Backend
- **Spring Boot** - Java framework for REST APIs
- **Spring Security** - Authentication and authorization
- **Spring OAuth2** - Google OAuth2 integration
- **Spring Data JPA** - Database operations and ORM
- **JWT (JSON Web Tokens)** - Stateless authentication
- **MySQL** - Relational database for user data
- **MapStruct** - Object mapping between DTOs and entities
- **Lombok** - Reduces boilerplate code with annotations
- **Hibernate** - JPA implementation for database operations
- **Maven** - Dependency management and build tool
- **Swagger/OpenAPI** - API documentation and testing

## 📋 Prerequisites

- **Node.js** (v16 or higher)
- **Java** (JDK 17 or higher)
- **MySQL** (v8.0 or higher)
- **Maven** (v3.6 or higher)

## 🔧 Installation & Setup

### Environment Variables Setup
1. Copy `.env.example` to `.env` in the backend directory
2. Fill in your actual OAuth2 credentials:
   ```bash
   GOOGLE_CLIENT_ID=your-actual-client-id
   GOOGLE_CLIENT_SECRET=your-actual-client-secret
   JWT_SECRET=your-jwt-secret-key
   ```
3. Get Google OAuth2 credentials from [Google Cloud Console](https://console.cloud.google.com/)

### Database Setup
```sql
CREATE DATABASE registration_app;
USE registration_app;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password VARCHAR(255) NOT NULL,
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    password_changed_at DATETIME,
    phone VARCHAR(15),
    profile_picture_url VARCHAR(500),
    locale VARCHAR(10),
    timezone VARCHAR(50),
    terms_accepted_at DATETIME NOT NULL,
    privacy_policy_accepted_at DATETIME NOT NULL,
    account_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    locked_until DATETIME,
    last_login_at DATETIME,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    oauth2_provider VARCHAR(50),
    oauth2_provider_id VARCHAR(255)
);
```

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

## 🌐 API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/logout` - User logout
- `GET /oauth2/authorize/google` - Google OAuth2 login
- `GET /oauth2/callback/google` - Google OAuth2 callback
- `GET /api/users/{id}/profile` - Get user profile
- `PUT /api/users/{id}/profile` - Update user profile

## 🔐 Security Features

- **Password Encryption** - BCrypt hashing algorithm
- **JWT Tokens** - Secure stateless authentication
- **Account Locking** - Automatic lockout after failed attempts
- **Input Validation** - Comprehensive data validation
- **CORS Configuration** - Cross-origin resource sharing setup

## 📱 Application Flow

1. **Registration** - Users create accounts with comprehensive validation
2. **Login** - Authentication with JWT token generation or Google OAuth2
3. **Home Page** - Welcome dashboard for authenticated users
4. **Profile Management** - View and edit user information with real-time updates
5. **Navigation** - Seamless routing between pages with React Router
6. **Logout** - Secure session termination with token cleanup

## 🎨 UI/UX Features

- **Professional Design** - Modern gradient backgrounds and card layouts
- **Navigation Bar** - Sticky navigation with active state indicators
- **Responsive Layout** - Works seamlessly on desktop and mobile devices
- **Form Validation** - Real-time input validation with detailed error messages
- **Loading States** - User feedback during API operations
- **Editable Profiles** - In-place editing with save/cancel functionality
- **Route Protection** - Automatic redirection based on authentication state

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Joel Salazar**
- GitHub: [@JSalazarAlt](https://github.com/JSalazarAlt)
- LinkedIn: [Joel Salazar](https://linkedin.com/in/joelsalazar-dev)

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

## ⭐ Show your support

Give a ⭐️ if this project helped you!