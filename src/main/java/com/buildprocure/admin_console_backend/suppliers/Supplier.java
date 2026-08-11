package com.buildprocure.admin_console_backend.suppliers;

// API response / data shape for a supplier. Once persistence is added,
// this splits into a JPA @Entity (DB row shape) and a separate response
// DTO - kept as one record for now since there's no database yet.
public record Supplier(
    String id,
    String name,
    String category,
    String status,
    String contactEmail,
    String onboardedDate
) {}
