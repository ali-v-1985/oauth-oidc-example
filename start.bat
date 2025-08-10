@echo off
echo ========================================
echo OAuth2 OpenID Connect Example
echo ========================================
echo.
echo Starting the application...
echo.
echo Make sure you have:
echo 1. Java 21 installed
echo 2. Maven installed
echo 3. OAuth2 provider configured
echo.
echo The application will start at: http://localhost:8080
echo.
echo Press Ctrl+C to stop the application
echo ========================================
echo.

mvn spring-boot:run

pause
