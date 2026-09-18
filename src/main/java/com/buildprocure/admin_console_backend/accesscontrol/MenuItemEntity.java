package com.buildprocure.admin_console_backend.accesscontrol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// DB row shape for one (role, module, item) nav entry. See
// src/main/resources/db/access_control_schema.sql for the table + seed
// data, and MenuItem for the API-facing shape this gets mapped into.
@Entity
@Table(name = "menu_items")
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 50)
    private String module;

    @Column(name = "item_key", nullable = false, length = 80)
    private String itemKey;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 150)
    private String path;

    @Column(length = 20)
    private String icon;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    protected MenuItemEntity() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getModule() {
        return module;
    }

    public String getItemKey() {
        return itemKey;
    }

    public String getLabel() {
        return label;
    }

    public String getPath() {
        return path;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}
