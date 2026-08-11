package com.buildprocure.admin_console_backend.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Not under /auth/**, so this requires a valid auth_token cookie same as
// SupplierController. role comes from the frontend as a query param
// (defaulting to "admin") because AuthController#me doesn't return a role
// yet - once it does, this should read the role from the authenticated
// principal instead of trusting a client-supplied param.
@RestController
public class ModuleTileController {

    private final ModuleTileService moduleTileService;

    public ModuleTileController(ModuleTileService moduleTileService) {
        this.moduleTileService = moduleTileService;
    }

    @GetMapping("/api/modules")
    public List<ModuleTile> getModules(@RequestParam(defaultValue = "admin") String role) {
        return moduleTileService.getTilesForRole(role);
    }
}
