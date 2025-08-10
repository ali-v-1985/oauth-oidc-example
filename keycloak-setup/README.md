# Keycloak Setup Scripts

This directory contains scripts to automatically configure Keycloak for OAuth2/OpenID Connect authentication.

## Prerequisites

- Node.js installed
- Keycloak running on `http://localhost:8081`
- Keycloak admin credentials (default: `admin`/`admin`)

## Files

- `setup-keycloak.js` - Full setup script with advanced configuration
- `setup-keycloak-simple.js` - Simplified setup script (recommended)
- `package.json` - Node.js dependencies
- `node_modules/` - Installed dependencies

## Quick Setup

1. **Navigate to this directory:**
   ```bash
   cd keycloak-setup
   ```

2. **Install dependencies (if not already done):**
   ```bash
   npm install
   ```

3. **Run the setup script:**
   ```bash
   node setup-keycloak-simple.js
   ```

## What the Script Does

1. **Creates a new realm** called `oauth-oidc-realm`
2. **Creates an OAuth2 client** with ID `oauth-oidc-client`
3. **Creates a test user** with credentials `testuser`/`password123`
4. **Configures redirect URIs** for your Spring Boot application

## Configuration Details

- **Realm**: `oauth-oidc-realm`
- **Client ID**: `oauth-oidc-client`
- **Client Secret**: `your-client-secret-change-this`
- **Redirect URI**: `http://localhost:8080/login/oauth2/code/keycloak`
- **Test User**: `testuser` / `password123`

## After Setup

1. **Update your `application.yml`** with the client secret
2. **Restart your Spring Boot application**
3. **Test the OAuth2 login flow**

## Manual Setup Alternative

If you prefer to configure Keycloak manually:

1. Access Keycloak Admin Console: `http://localhost:8081/auth/admin`
2. Login with admin credentials
3. Create a new realm
4. Create a new client with the configuration above
5. Create a test user

## Troubleshooting

- Ensure Keycloak is running on port 8081
- Check that admin credentials are correct
- Verify no port conflicts
- Check Keycloak logs for detailed error messages
