package com.buildprocure.admin_console_backend.home;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

// Mock data, keyed by role - ported from the frontend's old
// modulesCardsConfig.js. Replace with a real query once a database is
// wired up; ModuleTileService and ModuleTileController only depend on the
// ModuleTileRepository interface, so that swap won't touch either of them.
@Repository
public class InMemoryModuleTileRepository implements ModuleTileRepository {

    private static final Map<String, List<ModuleTile>> TILES_BY_ROLE = Map.of(
        "admin", List.of(
            new ModuleTile("supplier-onboarding", "Supplier Onboarding", "Manage and onboard new suppliers", "active", "/supplier-onboarding"),
            new ModuleTile("customer-config", "Customer Config", "Configure customer accounts and settings", "active", "/customer-config"),
            new ModuleTile("purchase-orders", "Purchase Orders Management", "Create and manage purchase orders", "coming-soon", "/purchase-orders"),
            new ModuleTile("vendor-invoices", "Vendor Invoice Processing", "Process vendor invoices and payments", "coming-soon", "/vendor-invoices"),
            new ModuleTile("compliance", "Compliance & Audit", "Track compliance and audit reports", "coming-soon", "/compliance"),
            new ModuleTile("analytics", "Analytics & Reports", "View analytics and generate reports", "coming-soon", "/analytics")
        ),
        "supplier", List.of(
            new ModuleTile("supplier-onboarding", "Supplier Onboarding", "Complete your supplier profile", "active", "/supplier-onboarding"),
            new ModuleTile("purchase-orders", "Purchase Orders", "View your purchase orders", "active", "/purchase-orders"),
            new ModuleTile("vendor-invoices", "My Invoices", "Submit and track your invoices", "active", "/vendor-invoices")
        ),
        "buyer", List.of(
            new ModuleTile("supplier-onboarding", "Supplier Onboarding", "View approved suppliers", "active", "/supplier-onboarding"),
            new ModuleTile("purchase-orders", "Purchase Orders", "Create and manage purchase orders", "active", "/purchase-orders")
        ),
        "csr", List.of(
            new ModuleTile("supplier-onboarding", "Supplier Onboarding", "Manage supplier onboarding process", "active", "/supplier-onboarding"),
            new ModuleTile("vendor-invoices", "Vendor Invoices", "Process vendor invoices", "active", "/vendor-invoices")
        )
    );

    @Override
    public List<ModuleTile> findByRole(String role) {
        return TILES_BY_ROLE.getOrDefault(role, TILES_BY_ROLE.get("admin"));
    }
}
