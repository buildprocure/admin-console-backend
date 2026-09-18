package com.buildprocure.admin_console_backend.accesscontrol;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemJpaRepository extends JpaRepository<MenuItemEntity, Long> {
    List<MenuItemEntity> findByRoleOrderBySortOrderAsc(String role);
}
