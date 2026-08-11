package com.buildprocure.admin_console_backend.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates a post-login redirect target against a configured allowlist of
 * origins (app.allowed-redirect-origins). Used at both ends of the OAuth2
 * flow: /auth/login (before redirecting to Entra ID) and
 * OAuthLoginSuccessHandler (after Entra ID redirects back), so an attacker
 * can't send someone through our real login flow only to redirect them
 * somewhere arbitrary at the end (open redirect).
 */
public final class RedirectValidator {

    private RedirectValidator() {
    }

    public static Set<String> parseOrigins(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(commaSeparated.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
    }

    public static boolean isAllowed(String redirect, Set<String> allowedOrigins) {
        if (redirect == null || redirect.isBlank() || allowedOrigins.isEmpty()) {
            return false;
        }
        try {
            URI uri = new URI(redirect);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }
            String origin = uri.getScheme() + "://" + uri.getHost()
                + (uri.getPort() != -1 ? ":" + uri.getPort() : "");
            return allowedOrigins.contains(origin);
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
