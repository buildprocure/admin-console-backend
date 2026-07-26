package com.buildprocure.admin_console_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final String frontendUrl;
    private final boolean cookieSecure;

    public OAuthLoginSuccessHandler(JwtService jwtService, String frontendUrl, boolean cookieSecure) {
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
        this.cookieSecure = cookieSecure;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String name = oidcUser.getFullName() != null ? oidcUser.getFullName() : "Unknown user";
        String email = oidcUser.getEmail() != null ? oidcUser.getEmail() : oidcUser.getPreferredUsername();

        String token = jwtService.generateToken(name, email);

        response.addHeader("Set-Cookie", buildCookie("auth_token", token, 8 * 60 * 60));
        response.addHeader("Set-Cookie", buildCookie("ms_id_token", oidcUser.getIdToken().getTokenValue(), 8 * 60 * 60));

        response.sendRedirect(frontendUrl);
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
}
