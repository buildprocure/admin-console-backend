package com.buildprocure.admin_console_backend.auth;

import com.buildprocure.admin_console_backend.common.util.RedirectValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class AuthController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.allowed-redirect-origins}")
    private String allowedRedirectOriginsRaw;

    @Value("${app.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.azure.tenant-id}")
    private String tenantId;

    private static final String REDIRECT_COOKIE = "post_login_redirect";

    @GetMapping("/auth/login")
    public void login(@RequestParam(required = false) String redirect, HttpServletResponse response) throws IOException {
        Set<String> allowedOrigins = RedirectValidator.parseOrigins(allowedRedirectOriginsRaw);
        if (RedirectValidator.isAllowed(redirect, allowedOrigins)) {
            // Carried across the Entra ID round-trip as a short-lived cookie;
            // OAuthLoginSuccessHandler reads and re-validates it before using it.
            response.addHeader("Set-Cookie", buildRedirectCookie(redirect));
        }
        response.sendRedirect("/oauth2/authorization/azure");
    }

    private String buildRedirectCookie(String redirect) {
        StringBuilder sb = new StringBuilder();
        sb.append(REDIRECT_COOKIE).append("=")
          .append(java.net.URLEncoder.encode(redirect, StandardCharsets.UTF_8))
          .append("; Max-Age=300")
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
