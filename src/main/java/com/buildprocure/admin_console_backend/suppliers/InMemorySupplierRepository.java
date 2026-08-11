package com.buildprocure.admin_console_backend.suppliers;

import org.springframework.stereotype.Repository;

import java.util.List;

// Mock data store - replace with a real query (e.g. Spring Data JPA against
// a suppliers table) once a database is wired up. SupplierService and
// SupplierController only depend on the SupplierRepository interface, so
// that swap won't touch either of them.
@Repository
public class InMemorySupplierRepository implements SupplierRepository {

    private static final List<Supplier> SUPPLIERS = List.of(
        new Supplier("SUP-1001", "Acme Steel Works", "Raw Materials", "Approved", "contact@acmesteel.com", "2025-11-03"),
        new Supplier("SUP-1002", "Northwind Logistics", "Logistics", "Approved", "ops@northwindlog.com", "2025-12-14"),
        new Supplier("SUP-1003", "Bluewave Electronics", "Electronics", "Pending", "sales@bluewave-elec.com", "2026-01-22"),
        new Supplier("SUP-1004", "Granite Point Concrete", "Construction Materials", "Approved", "info@granitepoint.com", "2025-09-18"),
        new Supplier("SUP-1005", "Skyline Packaging Co.", "Packaging", "Suspended", "hello@skylinepack.com", "2025-07-30"),
        new Supplier("SUP-1006", "Vertex Industrial Supply", "Tools & Equipment", "Pending", "support@vertexsupply.com", "2026-02-05")
    );

    @Override
    public List<Supplier> findAll() {
        return SUPPLIERS;
    }
}
