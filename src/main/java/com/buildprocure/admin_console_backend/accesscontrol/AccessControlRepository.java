package com.buildprocure.admin_console_backend.accesscontrol;

// Contract for looking up a single user's full access-control payload.
// InMemoryAccessControlRepository is the only implementation today; a real
// one (menu + page + button entitlements joined against a users/roles
// table) can implement this same interface later with zero changes needed
// in AccessControlService or AccessControlController.
public interface AccessControlRepository {
    AccessControlResponse findByUserId(String userId);
}
