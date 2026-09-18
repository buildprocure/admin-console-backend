package com.buildprocure.admin_console_backend.accesscontrol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// A route the given role can't open even via a direct link - see
// AccessControlResponse#pagesExcluded.
@Entity
@Table(name = "pages_excluded")
public class PageExclusionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 150)
    private String path;

    protected PageExclusionEntity() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getPath() {
        return path;
    }
}
