package com.buildprocure.admin_console_backend.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.azure.tenant-id}")
    private String tenantId;

    private final JwtService jwtService;
    private final JwtDecoder microsoftJwtDecoder;

    public AuthController(JwtService jwtService, JwtDecoder microsoftJwtDecoder) {
        this.jwtService = jwtService;
        this.microsoftJwtDecoder = microsoftJwtDecoder;
    }

    // Called by the frontend right after MSAL completes a loginRedirect().
    // Trades the Microsoft-issued ID token for our own auth_token cookie -
    // everything else in the app keeps authenticating via that cookie
    // exactly as before (see JwtAuthFilter), regardless of how the user
    // originally signed in.
    @PostMapping("/auth/msal-login")
    public ResponseEntity<Map<String, String>> msalLogin(@RequestBody MsalLoginRequest request, HttpServletResponse response) {
        Jwt msToken;
        try {
            msToken = microsoftJwtDecoder.decode(request.idToken());
        } catch (JwtException e) {
            log.warn("Rejected MSAL ID token: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        }

        String name = msToken.getClaimAsString("name");
        String email = firstNonBlank(
            msToken.getClaimAsString("email"),
            msToken.getClaimAsString("preferred_username")
        );

        if (email == null) {
            log.warn("MSAL ID token for subject {} had no email or preferred_username claim", msToken.getSubject());
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(name != null ? name : "Unknown user", email);

        response.addHeader("Set-Cookie", buildCookie("auth_token", token, 8 * 60 * 60));
        // Kept around so /auth/logout can pass it back to Microsoft as
        // id_token_hint, same purpose the old backend-driven flow served.
        response.addHeader("Set-Cookie", buildCookie("ms_id_token", request.idToken(), 8 * 60 * 60));

        Map<String, String> body = new HashMap<>();
        body.put("name", name != null ? name : "Unknown user");
        body.put("email", email);
        return ResponseEntity.ok(body);
    }

    private static String firstNonBlank(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }

    private String buildCookie(String name, String value, int maxAgeSeconds) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value)
          .append("; Max-Age=").append(maxAgeSeconds)
          .append("; Path=/")
          .append("; HttpOnly");
        if (cookieSecure) {
            sb.append("; Secure; SameSite=None");
        }
        return sb.toString();
    }

    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, String>> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return ResponseEntity.status(401).build();
        }

        Map<String, String> body = new HashMap<>();
        body.put("name", user.name());
        body.put("email", user.email());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idTokenHint = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ms_id_token".equals(cookie.getName())) {
                    idTokenHint = cookie.getValue();
                }
            }
        }

        response.addHeader("Set-Cookie", buildExpiredCookie("auth_token"));
        response.addHeader("Set-Cookie", buildExpiredCookie("ms_id_token"));

        StringBuilder logoutUrl = new StringBuilder("https://login.microsoftonline.com/" + tenantId
            + "/oauth2/v2.0/logout?post_logout_redirect_uri="
            + java.net.URLEncoder.encode(frontendUrl, "UTF-8"));

        if (idTokenHint != null) {
            logoutUrl.append("&id_token_hint=").append(java.net.URLEncoder.encode(idTokenHint, "UTF-8"));
        }

        response.sendRedirect(logoutUrl.toString());
    }

    private String buildExpiredCookie(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=")
          .append("; Max-Age=0")
          .append("; Path=/")
          .append("; HttpOnly")
          .append("; Secure; SameSite=None");
        return sb.toString();
    }
}
