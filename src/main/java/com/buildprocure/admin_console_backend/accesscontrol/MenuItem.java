package com.buildprocure.admin_console_backend.accesscontrol;

// One left-nav entry. Shape matches what the frontend's LeftNav component
// already expects (label, path, icon) plus id/module so a single flat list
// can serve every module and useMenu() can filter it down client-side.
public record MenuItem(
    String id,
    String module,
    String label,
    String path,
    String icon
) {}
