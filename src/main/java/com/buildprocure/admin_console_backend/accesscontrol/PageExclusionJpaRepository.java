package com.buildprocure.admin_console_backend.accesscontrol;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageExclusionJpaRepository extends JpaRepository<PageExclusionEntity, Long> {
    List<PageExclusionEntity> findByRole(String role);
}
