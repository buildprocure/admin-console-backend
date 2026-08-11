package com.buildprocure.admin_console_backend.common.config;

import com.buildprocure.admin_console_backend.auth.JwtAuthFilter;
import com.buildprocure.admin_console_backend.auth.JwtService;
import com.buildprocure.admin_console_backend.auth.OAuthLoginSuccessHandler;
import com.buildprocure.admin_console_backend.common.util.RedirectValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.allowed-redirect-origins}")
    private String allowedRedirectOriginsRaw;

    @Value("${app.cookie-secure}")
    private boolean cookieSecure;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Set<String> allowedRedirectOrigins = RedirectValidator.parseOrigins(allowedRedirectOriginsRaw);

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource(allowedRedirectOrigins)))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // CORS preflight (OPTIONS) requests never carry cookies, so
                // they must be allowed through before the auth check - else
                // any request that triggers a preflight (e.g. a custom
                // Content-Type header) gets treated as unauthenticated and
                // oauth2Login's entry point redirects it into the OAuth2
                // login flow instead of letting the real request proceed.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(new OAuthLoginSuccessHandler(jwtService, frontendUrl, cookieSecure, allowedRedirectOrigins))
            )
            .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS allows every known-good frontend origin (all lower envs + prod),
    // not just the single post-login redirect target, so local/preview
    // builds can call the API directly regardless of which one they are.
    private CorsConfigurationSource corsConfigurationSource(Set<String> allowedRedirectOrigins) {
        List<String> origins = new ArrayList<>(allowedRedirectOrigins);
        if (!origins.contains(frontendUrl)) {
            origins.add(frontendUrl);
        }

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
