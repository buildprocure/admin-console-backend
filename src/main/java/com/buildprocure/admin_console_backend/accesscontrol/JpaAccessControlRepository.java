package com.buildprocure.admin_console_backend.accesscontrol;

import org.springframework.stereotype.Repository;

import java.util.List;

// Reads menu/pages_excluded/buttons_excluded from MySQL, keyed by role -
// see src/main/resources/db/access_control_schema.sql for the
// menu_items/pages_excluded/buttons_excluded tables and seed data (ported
// 1:1 from the original InMemoryAccessControlRepository). Role itself
// comes from the app's existing `user` table (see UserAccountEntity) -
// there's no separate roles/user_roles table, since `user.Role` already
// holds it.
//
// AccessControlService and AccessControlController don't know or care
// that the backing store changed - they only depend on the
// AccessControlRepository interface.
@Repository
public class JpaAccessControlRepository implements AccessControlRepository {

    private static final String DEFAULT_ROLE = "admin";

    private final UserAccountJpaRepository userAccountJpaRepository;
    private final MenuItemJpaRepository menuItemJpaRepository;
    private final PageExclusionJpaRepository pageExclusionJpaRepository;
    private final ButtonExclusionJpaRepository buttonExclusionJpaRepository;

    public JpaAccessControlRepository(
        UserAccountJpaRepository userAccountJpaRepository,
        MenuItemJpaRepository menuItemJpaRepository,
        PageExclusionJpaRepository pageExclusionJpaRepository,
        ButtonExclusionJpaRepository buttonExclusionJpaRepository
    ) {
        this.userAccountJpaRepository = userAccountJpaRepository;
        this.menuItemJpaRepository = menuItemJpaRepository;
        this.pageExclusionJpaRepository = pageExclusionJpaRepository;
        this.buttonExclusionJpaRepository = buttonExclusionJpaRepository;
    }

    @Override
    public AccessControlResponse findByUserId(String userId) {
        String role = resolveRole(userId);

        List<MenuItem> menu = menuItemJpaRepository.findByRoleOrderBySortOrderAsc(role).stream()
            .map(e -> new MenuItem(e.getItemKey(), e.getModule(), e.getLabel(), e.getPath(), e.getIcon()))
            .toList();

        List<String> pagesExcluded = pageExclusionJpaRepository.findByRole(role).stream()
            .map(PageExclusionEntity::getPath)
            .toList();

        List<String> buttonsExcluded = buttonExclusionJpaRepository.findByRole(role).stream()
            .map(ButtonExclusionEntity::getButtonId)
            .toList();

        return new AccessControlResponse(userId, role, menu, pagesExcluded, buttonsExcluded);
    }

    // userId is the caller's email (see AccessControlController). Falls
    // back to "admin" if no user row matches, or that user's Role is
    // null/blank, or it doesn't match any role we have menu data for -
    // same fallback behavior InMemoryAccessControlRepository had.
    private String resolveRole(String userId) {
        return userAccountJpaRepository.findByEmailIgnoreCase(userId).stream()
            .findFirst()
            .map(UserAccountEntity::getRole)
            .filter(role -> role != null && !role.isBlank())
            .map(role -> role.trim().toLowerCase())
            .orElse(DEFAULT_ROLE);
    }
}
