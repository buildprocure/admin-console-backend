package com.buildprocure.admin_console_backend.accesscontrol;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ButtonExclusionJpaRepository extends JpaRepository<ButtonExclusionEntity, Long> {
    List<ButtonExclusionEntity> findByRole(String role);
}
