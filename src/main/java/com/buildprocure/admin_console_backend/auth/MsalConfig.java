package com.buildprocure.admin_console_backend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

// Decodes and validates the ID token MSAL hands the frontend after a
// successful Entra ID login - verifies the signature against Microsoft's
// public keys (fetched from the tenant's OIDC discovery document), the
// issuer, expiry, and that the token was actually issued for *this* app
// registration (aud claim), before AuthController trusts any of its
// claims. This is the only place Microsoft's token is validated - once
// AuthController exchanges it, the rest of the app authenticates via our
// own auth_token JWT cookie (see JwtService/JwtAuthFilter), same as before.
@Configuration
public class MsalConfig {

    @Value("${app.azure.tenant-id}")
    private String tenantId;

    @Value("${app.azure.client-id}")
    private String azureClientId;

    @Bean
    public JwtDecoder microsoftJwtDecoder() {
        String issuerUri = "https://login.microsoftonline.com/" + tenantId + "/v2.0";
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new JwtClaimValidator<List<String>>(
            "aud", aud -> aud != null && aud.contains(azureClientId));

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
        return decoder;
    }
}
