package com.buildprocure.admin_console_backend.auth;

import com.buildprocure.admin_console_backend.common.util.RedirectValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String REDIRECT_COOKIE = "post_login_redirect";

    private final JwtService jwtService;
    private final String frontendUrl;
    private final boolean cookieSecure;
    private final Set<String> allowedRedirectOrigins;

    public OAuthLoginSuccessHandler(JwtService jwtService, String frontendUrl, boolean cookieSecure,
                                     Set<String> allowedRedirectOrigins) {
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
        this.cookieSecure = cookieSecure;
        this.allowedRedirectOrigins = allowedRedirectOrigins;
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
        response.addHeader("Set-Cookie", expireCookie(REDIRECT_COOKIE));

        response.sendRedirect(resolveRedirectTarget(request));
    }

    // Reads the redirect origin the /auth/login step stashed in a cookie
    // (see AuthController) and re-validates it here, so the value can't be
    // tampered with between the two requests. Falls back to the single
    // configured frontend URL if there's no cookie, or it's no longer valid.
    private String resolveRedirectTarget(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (REDIRECT_COOKIE.equals(cookie.getName())) {
                    String value = java.net.URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    if (RedirectValidator.isAllowed(value, allowedRedirectOrigins)) {
                        return value;
                    }
                }
            }
        }
        return frontendUrl;
    }

    private String expireCookie(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=; Max-Age=0; Path=/; HttpOnly");
        if (cookieSecure) {
            sb.append("; Secure; SameSite=None");
        }
        return sb.toString();
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
