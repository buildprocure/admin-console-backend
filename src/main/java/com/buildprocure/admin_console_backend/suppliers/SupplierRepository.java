package com.buildprocure.admin_console_backend.suppliers;

import java.util.List;

// Contract for fetching supplier data. InMemorySupplierRepository is the
// only implementation today; a JpaSupplierRepository backed by a real
// "suppliers" table can implement this same interface later with zero
// changes needed in SupplierService or SupplierController.
public interface SupplierRepository {
    List<Supplier> findAll();
}
