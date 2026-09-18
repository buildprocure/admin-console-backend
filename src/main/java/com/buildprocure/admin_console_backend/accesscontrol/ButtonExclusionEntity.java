package com.buildprocure.admin_console_backend.accesscontrol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// An action-button id hidden within pages the given role CAN otherwise
// open - see AccessControlResponse#buttonsExcluded.
@Entity
@Table(name = "buttons_excluded")
public class ButtonExclusionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "button_id", nullable = false, length = 100)
    private String buttonId;

    protected ButtonExclusionEntity() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getButtonId() {
        return buttonId;
    }
}
