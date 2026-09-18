package com.buildprocure.admin_console_backend.accesscontrol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Maps to the app's EXISTING `user` table (id/username/email/password/
// Role/dt/status/...) - not something this feature owns or creates. Only
// the columns AccessControlService actually needs are mapped here
// (id, email, Role); password and the rest are deliberately left out so
// this entity can never load or leak them, even by accident.
//
// Role is nullable in that table (see the "Role" column's Null=YES) and
// its casing isn't guaranteed to match the lowercase keys used in
// menu_items/pages_excluded/buttons_excluded ("admin", "csr", "supplier",
// "buyer") - JpaAccessControlRepository normalizes to lowercase and falls
// back to "admin" when Role is null/blank/unrecognized.
@Entity
@Table(name = "user")
public class UserAccountEntity {

    @Id
    private Integer id;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(name = "Role", length = 20)
    private String role;

    protected UserAccountEntity() {
        // JPA
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
