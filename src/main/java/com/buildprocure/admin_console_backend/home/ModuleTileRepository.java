package com.buildprocure.admin_console_backend.home;

import java.util.List;

// Contract for fetching a role's home-page tiles. InMemoryModuleTileRepository
// is the only implementation today; a real one (e.g. a module_tiles table
// joined against role entitlements) can implement this same interface later
// with zero changes needed in ModuleTileService or ModuleTileController.
public interface ModuleTileRepository {
    List<ModuleTile> findByRole(String role);
}
