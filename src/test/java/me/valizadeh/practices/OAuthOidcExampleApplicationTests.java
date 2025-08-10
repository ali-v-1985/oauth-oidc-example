package me.valizadeh.practices;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic test class for the OAuth2 OpenID Connect application
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.keycloak.client-id=test-client",
    "spring.security.oauth2.client.registration.keycloak.client-secret=test-secret",
    "spring.jwt.secret=test-jwt-secret-key-for-testing-purposes-only",
    "spring.jwt.expiration=3600000"
})
class OAuthOidcExampleApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the Spring application context loads successfully
    }
}
