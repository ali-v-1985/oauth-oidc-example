const axios = require('axios');

// Keycloak configuration
const KEYCLOAK_URL = 'http://localhost:8081';
const ADMIN_USERNAME = 'admin';
const ADMIN_PASSWORD = 'admin';
const REALM_NAME = 'oauth-oidc-realm';
const CLIENT_ID = 'oauth-oidc-client';
const CLIENT_SECRET = 'your-client-secret-change-this';

async function setupKeycloak() {
    try {
        console.log('🚀 Setting up Keycloak...');
        
        // Step 1: Get admin token
        console.log('📝 Getting admin token...');
        const tokenResponse = await axios.post(`${KEYCLOAK_URL}/auth/realms/master/protocol/openid-connect/token`, 
            new URLSearchParams({
                grant_type: 'password',
                client_id: 'admin-cli',
                username: ADMIN_USERNAME,
                password: ADMIN_PASSWORD
            }), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }
        );
        
        const adminToken = tokenResponse.data.access_token;
        console.log('✅ Admin token obtained');
        
        // Step 2: Create new realm
        console.log('🏗️  Creating new realm...');
        try {
            await axios.post(`${KEYCLOAK_URL}/auth/admin/realms`, {
                realm: REALM_NAME,
                enabled: true,
                displayName: 'OAuth OIDC Example Realm'
            }, {
                headers: {
                    'Authorization': `Bearer ${adminToken}`,
                    'Content-Type': 'application/json'
                }
            });
            console.log('✅ Realm created');
        } catch (error) {
            if (error.response?.status === 409) {
                console.log('ℹ️  Realm already exists');
            } else {
                throw error;
            }
        }
        
        // Step 3: Create OAuth2 client (simplified)
        console.log('🔐 Creating OAuth2 client...');
        const clientConfig = {
            clientId: CLIENT_ID,
            enabled: true,
            publicClient: false,
            secret: CLIENT_SECRET,
            redirectUris: ['http://localhost:8080/login/oauth2/code/keycloak'],
            webOrigins: ['http://localhost:8080'],
            standardFlowEnabled: true,
            directAccessGrantsEnabled: true,
            serviceAccountsEnabled: false,
            authorizationServicesEnabled: false,
            implicitFlowEnabled: false
        };
        
        try {
            await axios.post(`${KEYCLOAK_URL}/auth/admin/realms/${REALM_NAME}/clients`, clientConfig, {
                headers: {
                    'Authorization': `Bearer ${adminToken}`,
                    'Content-Type': 'application/json'
                }
            });
            console.log('✅ OAuth2 client created');
        } catch (error) {
            if (error.response?.status === 409) {
                console.log('ℹ️  Client already exists');
            } else {
                throw error;
            }
        }
        
        // Step 4: Create a test user
        console.log('👤 Creating test user...');
        const userConfig = {
            username: 'testuser',
            enabled: true,
            emailVerified: true,
            firstName: 'Test',
            lastName: 'User',
            email: 'testuser@example.com',
            credentials: [{
                type: 'password',
                value: 'password123',
                temporary: false
            }]
        };
        
        try {
            await axios.post(`${KEYCLOAK_URL}/auth/admin/realms/${REALM_NAME}/users`, userConfig, {
                headers: {
                    'Authorization': `Bearer ${adminToken}`,
                    'Content-Type': 'application/json'
                }
            });
            console.log('✅ Test user created');
        } catch (error) {
            if (error.response?.status === 409) {
                console.log('ℹ️  User already exists');
            } else {
                throw error;
            }
        }
        
        console.log('\n🎉 Keycloak setup completed successfully!');
        console.log('\n📋 Configuration Summary:');
        console.log(`   Realm: ${REALM_NAME}`);
        console.log(`   Client ID: ${CLIENT_ID}`);
        console.log(`   Client Secret: ${CLIENT_SECRET}`);
        console.log(`   Redirect URI: http://localhost:8080/login/oauth2/code/keycloak`);
        console.log(`   Test User: testuser / password123`);
        console.log(`   Keycloak URL: ${KEYCLOAK_URL}/auth/realms/${REALM_NAME}`);
        
        console.log('\n🔧 Next steps:');
        console.log('   1. Update application.yml with the client secret above');
        console.log('   2. Restart your Spring Boot application');
        console.log('   3. Test login with Keycloak');
        
    } catch (error) {
        console.error('❌ Error setting up Keycloak:', error.response?.data || error.message);
        process.exit(1);
    }
}

setupKeycloak();
