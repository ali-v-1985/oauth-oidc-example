#!/bin/bash

echo "Starting Keycloak with OAuth OIDC configuration..."
echo
echo "This will:"
echo "- Start PostgreSQL database"
echo "- Start Keycloak with automatic realm import"
echo "- Create the oauth-oidc-realm automatically"
echo "- Create the oauth-oidc-client automatically"
echo "- Create test user (testuser/password123)"
echo
read -p "Press Enter to continue..."

docker-compose up -d

echo
echo "Waiting for Keycloak to start..."
sleep 30

echo
echo "Keycloak is now running at: http://localhost:8081"
echo "Admin console: http://localhost:8081/auth"
echo "Admin credentials: admin/admin"
echo
echo "Test user: testuser / password123"
echo
echo "Your Spring Boot app should now be able to connect to Keycloak!"
echo
read -p "Press Enter to exit..."
