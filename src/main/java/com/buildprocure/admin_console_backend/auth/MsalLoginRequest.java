package com.buildprocure.admin_console_backend.auth;

// Body of POST /auth/msal-login - the ID token MSAL obtained from Entra ID
// on the frontend.
public record MsalLoginRequest(String idToken) {}
