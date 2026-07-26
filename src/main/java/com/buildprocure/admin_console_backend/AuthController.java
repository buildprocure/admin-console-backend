package com.buildprocure.admin_console_backend;

import com.buildprocure.admin_console_backend.config.AuthenticatedUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.azure.tenant-id}")
    private String tenantId;

    @GetMapping("/auth/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/azure");
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