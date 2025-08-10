package me.valizadeh.practices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.valizadeh.practices.dto.ApiResponse;
import me.valizadeh.practices.dto.UserDto;
import me.valizadeh.practices.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Main API controller with protected endpoints
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ApiController {

    private final JwtUtil jwtUtil;

    /**
     * Get current user information
     */
    @GetMapping("/user/profile")
    public ResponseEntity<ApiResponse<UserDto>> getUserProfile(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            log.warn("User not authenticated for profile request");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User not authenticated"));
        }

                     UserDto userDto = UserDto.builder()
                     .id(oidcUser.getSubject())
                     .username(oidcUser.getPreferredUsername())
                     .email(oidcUser.getEmail())
                     .firstName(oidcUser.getGivenName())
                     .lastName(oidcUser.getFamilyName())
                     .fullName(oidcUser.getFullName())
                     .picture(oidcUser.getPicture())
                     .locale(oidcUser.getLocale())
                     .emailVerified(oidcUser.getEmailVerified())
                     .lastLogin(LocalDateTime.now())
                     .build();

        log.info("User profile retrieved successfully for user: {}", oidcUser.getSubject());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userDto));
    }

    /**
     * Get user claims from ID token
     */
    @GetMapping("/user/claims")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserClaims(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            log.warn("User not authenticated for claims request");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User not authenticated"));
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("subject", oidcUser.getSubject());
        claims.put("issuer", oidcUser.getIssuer());
        claims.put("audience", oidcUser.getAudience());
        claims.put("issuedAt", oidcUser.getIssuedAt());
        claims.put("expiresAt", oidcUser.getExpiresAt());
        claims.put("allClaims", oidcUser.getClaims());

        return ResponseEntity.ok(ApiResponse.success("User claims retrieved successfully", claims));
    }

    /**
     * Generate JWT token for current user
     */
    @PostMapping("/auth/token")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("User not authenticated for token generation");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User not authenticated"));
        }

        String token = jwtUtil.generateToken(authentication);
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("token", token);
        tokenResponse.put("type", "Bearer");

        log.info("JWT token generated successfully for user: {}", authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Token generated successfully", tokenResponse));
    }

    /**
     * Protected endpoint - requires authentication
     */
    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<String>> getProtectedResource() {
        return ResponseEntity.ok(ApiResponse.success("This is a protected resource", "Access granted"));
    }

    /**
     * Protected endpoint - requires ROLE_USER role
     */
    @GetMapping("/user/data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> getUserData() {
        return ResponseEntity.ok(ApiResponse.success("User data accessed successfully", "User specific data"));
    }

    /**
     * Protected endpoint - requires ROLE_ADMIN role
     */
    @GetMapping("/admin/data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getAdminData() {
        return ResponseEntity.ok(ApiResponse.success("Admin data accessed successfully", "Admin specific data"));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", "OK"));
    }

    /**
     * Echo endpoint for testing
     */
    @PostMapping("/echo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> echo(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Echo response");
        response.put("timestamp", LocalDateTime.now());
        response.put("received", request);
        
        return ResponseEntity.ok(ApiResponse.success("Echo successful", response));
    }
}
