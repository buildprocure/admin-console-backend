package com.buildprocure.admin_console_backend.accesscontrol;

import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    private final AccessControlRepository accessControlRepository;

    public AccessControlService(AccessControlRepository accessControlRepository) {
        this.accessControlRepository = accessControlRepository;
    }

    public AccessControlResponse getAccessForUser(String userId) {
        return accessControlRepository.findByUserId(userId);
    }
}
