package com.buildprocure.admin_console_backend.home;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleTileService {

    private final ModuleTileRepository moduleTileRepository;

    public ModuleTileService(ModuleTileRepository moduleTileRepository) {
        this.moduleTileRepository = moduleTileRepository;
    }

    public List<ModuleTile> getTilesForRole(String role) {
        return moduleTileRepository.findByRole(role);
    }
}
