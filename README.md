# OAuth2 OpenID Connect Example

A complete Spring Boot application demonstrating OAuth2 and OpenID Connect integration with Spring Security 6.

## 🚀 Features

- **Spring Boot 3.2.x** with Spring Security 6.x
- **OAuth2 Client** integration with multiple providers
- **OpenID Connect** support for enhanced security
- **JWT Support** for token-based authentication
- **Role-based Access Control** with proper authorization
- **REST API** endpoints with security
- **Thymeleaf Templates** for web interface
- **Multiple OAuth2 Providers** (Keycloak, Google)

## 🏗️ Architecture

The application implements a secure authentication flow:

1. **Home Page (`/`)**: Public landing page with login option
2. **Login Page (`/login`)**: OAuth2 provider selection
3. **OAuth2 Flow**: Redirects to provider for authentication
4. **Dashboard (`/dashboard`)**: Protected page showing user information
5. **Logout**: Token expiration and redirect to login

## 📋 Prerequisites

- Java 21+
- Maven 3.6+
- Keycloak server (for OAuth2 provider)
- Google Cloud Console account (optional, for Google OAuth2)

## 🛠️ Setup Instructions

### 1. Clone and Build

```bash
git clone <repository-url>
cd oauth-oidc-example
mvn clean install
```

### 2. Keycloak Configuration

#### Start Keycloak Server
```bash
# Download and start Keycloak
# Default: http://localhost:8081
```

#### Create Realm and Client
1. Access Keycloak Admin Console: `http://localhost:8081/auth/admin`
2. Create a new realm: `oauth-oidc-realm`
3. Create a new client: `oauth-oidc-client`
4. Set client type to: `confidential`
5. Add redirect URI: `http://localhost:8080/login/oauth2/code/keycloak`
6. Copy the client secret

#### Update Application Configuration
Update `src/main/resources/application.yml` with your Keycloak details:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: oauth-oidc-client
            client-secret: YOUR_KEYCLOAK_CLIENT_SECRET
            scope: openid,profile,email
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            client-name: Keycloak
        provider:
          keycloak:
            issuer-uri: http://localhost:8081/auth/realms/oauth-oidc-realm
            jwk-set-uri: http://localhost:8081/auth/realms/oauth-oidc-realm/protocol/openid-connect/certs
            user-name-attribute: preferred_username
```

### 3. Google OAuth2 Configuration (Optional)

#### Create Google OAuth2 Client
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API and Google OAuth2 API
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
5. Choose "Web application" as application type
6. Set authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`

#### Update Application Configuration
```yaml
google:
  client-id: YOUR_ACTUAL_GOOGLE_CLIENT_ID
  client-secret: YOUR_ACTUAL_GOOGLE_CLIENT_SECRET
```

**Note**: Currently using placeholder values - update with real credentials to enable Google login.

## 🚀 Running the Application

### Start the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Verify Application Status
```bash
# Check if running
netstat -ano | findstr :8080

# Test home page
curl http://localhost:8080/

# Test health endpoint
curl http://localhost:8080/api/health
```

## 🔐 Authentication Flow

### 1. Access Home Page
- Navigate to `http://localhost:8080/`
- Click "Get Started" to access login page

### 2. OAuth2 Login
- Choose OAuth2 provider (Keycloak or Google)
- Redirected to provider's login page
- Authenticate with provider credentials

### 3. Post-Authentication
- Successfully authenticated users redirected to `/dashboard`
- Dashboard displays user information from OIDC claims
- Logout button available for session termination

### 4. Protected Resources
- `/dashboard`: Requires authentication
- `/api/**`: REST API endpoints with security
- Unauthenticated access redirects to login page

## 📊 Current Status

### ✅ Working Features
- **Keycloak OAuth2**: Fully functional with OIDC
- **User Authentication**: Successful login and session management
- **Dashboard Access**: Protected page with user information display
- **Logout Functionality**: Proper token expiration and cleanup
- **Security Configuration**: Proper route protection and redirects
- **Template Rendering**: Fixed Thymeleaf template parsing issues

### ❌ Known Issues
- **Google OAuth2**: Not configured (using placeholder credentials)
- **Error**: "OAuth client was not found" when attempting Google login

### 🔧 Recent Fixes
- **Template Parsing Error**: Fixed `SpelEvaluationException` in dashboard template
- **Authentication Flow**: Resolved infinite redirect loops
- **OAuth2 Error Handling**: Enhanced error messages and logging
- **Security Configuration**: Proper authentication entry points and failure handlers

## 🧪 Testing

### Test Keycloak Integration
1. Start Keycloak server
2. Access application home page
3. Click "Login with Keycloak"
4. Authenticate with Keycloak credentials
5. Verify dashboard access and user information

### Test API Endpoints
```bash
# Health check (public)
curl http://localhost:8080/api/health

# Protected endpoints (require authentication)
curl http://localhost:8080/dashboard
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── me/valizadeh/practices/
│   │       ├── config/
│   │       │   └── SecurityConfig.java          # Spring Security configuration
│   │       ├── controller/
│   │       │   └── WebController.java           # Web endpoints and templates
│   │       └── OAuthOidcExampleApplication.java # Main application class
│   └── resources/
│       ├── templates/
│       │   ├── fragments/
│       │   │   └── nav.html                     # Navigation fragment
│       │   ├── dashboard.html                   # Protected dashboard page
│       │   ├── home.html                        # Public home page
│       │   └── login.html                       # OAuth2 login page
│       └── application.yml                      # Application configuration
```

## 🔍 Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Check what's using port 8080
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <PID> /F
```

#### 2. Keycloak Connection Issues
- Verify Keycloak server is running on port 8081
- Check realm and client configuration
- Ensure redirect URI matches exactly

#### 3. Template Parsing Errors
- Verify Thymeleaf expressions use correct syntax
- Use `user.getAttribute('sub')` instead of `user.sub` for OIDC attributes

#### 4. OAuth2 Callback Errors
- Check redirect URI configuration in OAuth2 provider
- Verify client ID and secret are correct
- Check application logs for detailed error messages

### Logs and Debugging
```bash
# View application logs
# Check for OAuth2 errors and authentication issues

# Enable debug logging
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- Spring Security team for robust security features
- Keycloak team for identity provider implementation
- OpenID Connect community for standards

## 📞 Support

For issues and questions:
1. Check the troubleshooting section
2. Review application logs
3. Verify OAuth2 provider configuration
4. Create an issue in the repository

---

**Last Updated**: August 10, 2025  
**Status**: Keycloak OAuth2 working, Google OAuth2 needs configuration  
**Version**: 1.0.0
