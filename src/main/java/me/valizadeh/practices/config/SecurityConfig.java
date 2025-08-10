package me.valizadeh.practices.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Security configuration for OAuth2 and OpenID Connect
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {

    /**
     * Configure security filter chain for web requests (OAuth2 login)
     */
    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(request -> !request.getRequestURI().startsWith("/api/"))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/public/**", "/error", "/login").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    log.error("Authentication entry point triggered: {} - URI: {}", authException.getMessage(), request.getRequestURI());
                    
                    // Check if this is an OAuth2 callback with error parameters
                    String error = request.getParameter("error");
                    String errorDescription = request.getParameter("error_description");
                    
                    if (error != null) {
                        log.error("OAuth2 callback error: {} - {}", error, errorDescription);
                        String errorMessage = "OAuth2 Error: " + error;
                        if (errorDescription != null && !errorDescription.isEmpty()) {
                            errorMessage += " - " + errorDescription;
                        }
                        response.sendRedirect("/error?message=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
                    } else {
                        // Regular authentication failure
                        response.sendRedirect("/login");
                    }
                })
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(oidcUserService())
                )
                .failureHandler((request, response, exception) -> {
                    log.error("OAuth2 login failed: {} - URI: {} - Exception: {}", 
                        exception.getMessage(), request.getRequestURI(), exception.getClass().getSimpleName());
                    
                    // Log the full exception for debugging
                    log.error("Full OAuth2 failure exception", exception);
                    
                    String errorMessage = "Authentication failed: " + exception.getMessage();
                    if (exception.getMessage() != null && exception.getMessage().contains("access_denied")) {
                        errorMessage = "Access denied by user";
                    } else if (exception.getMessage() != null && exception.getMessage().contains("invalid_grant")) {
                        errorMessage = "Invalid authorization code";
                    } else if (exception.getMessage() != null && exception.getMessage().contains("invalid_request")) {
                        errorMessage = "Invalid OAuth2 request";
                    } else if (exception.getMessage() != null && exception.getMessage().contains("unauthorized_client")) {
                        errorMessage = "Unauthorized OAuth2 client";
                    } else if (exception.getMessage() != null && exception.getMessage().contains("unsupported_response_type")) {
                        errorMessage = "Unsupported OAuth2 response type";
                    } else if (exception.getMessage() != null && exception.getMessage().contains("invalid_scope")) {
                        errorMessage = "Invalid OAuth2 scope";
                    } else if (exception.getMessage() != null && exception.getMessage().contains("server_error")) {
                        errorMessage = "OAuth2 server error";
                    } else if (exception.getMessage() != null && exception.getMessage().contains("temporarily_unavailable")) {
                        errorMessage = "OAuth2 service temporarily unavailable";
                    }
                    
                    response.sendRedirect("/error?message=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );

        return http.build();
    }

    /**
     * Configure security filter chain for API requests (JWT validation)
     */
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwkSetUri("http://localhost:8081/auth/realms/oauth-oidc-realm/protocol/openid-connect/certs"))
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    /**
     * Custom OIDC user service to handle user information
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return new OidcUserService();
    }

    /**
     * Custom logout success handler
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            // You can add custom logic here after logout
            response.sendRedirect("/");
        };
    }
}
