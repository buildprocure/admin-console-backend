package com.buildprocure.admin_console_backend.accesscontrol;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, Integer> {
    // Returns a List rather than Optional/single result on purpose: the
    // `user` table has no unique constraint on email (only `username` is
    // UNI) - a single-result finder would throw at runtime if any two
    // rows ever share an email. JpaAccessControlRepository takes the
    // first match. Worth adding a unique constraint on email if that's
    // supposed to hold in practice.
    List<UserAccountEntity> findByEmailIgnoreCase(String email);
}
