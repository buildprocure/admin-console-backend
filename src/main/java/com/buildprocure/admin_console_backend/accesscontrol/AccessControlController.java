package com.buildprocure.admin_console_backend.accesscontrol;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// Not under /auth/**, so SecurityConfig's .anyRequest().authenticated()
// applies here - same as SupplierController/ModuleTileController.
//
// {userId} is the logged-in user's email for now (URL-encoded on the
// frontend) since /auth/me doesn't return a real user id yet - see
// InMemoryAccessControlRepository for the temporary email -> role mapping.
// Swap that lookup out once real user ids/roles exist; nothing here or on
// the frontend needs to change shape when that happens.
@RestController
public class AccessControlController {

    private final AccessControlService accessControlService;

    public AccessControlController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/api/access-control/{userId}")
    public AccessControlResponse getAccessControl(@PathVariable String userId) {
        return accessControlService.getAccessForUser(userId);
    }
}
