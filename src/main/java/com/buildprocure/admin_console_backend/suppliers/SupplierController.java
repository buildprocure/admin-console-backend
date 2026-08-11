package com.buildprocure.admin_console_backend.suppliers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Not under /auth/**, so SecurityConfig's .anyRequest().authenticated()
// applies here - JwtAuthFilter must find a valid auth_token cookie or this
// returns 401.
@RestController
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/api/suppliers")
    public List<Supplier> getSuppliers() {
        return supplierService.getAllSuppliers();
    }
}
