package com.buildprocure.admin_console_backend.home;

// Shape the frontend's ModuleCard component expects (id, title,
// description, status, path). Feeds the initial home/tile page. Per-module
// nav menus and page-level data will get their own feature packages later
// (e.g. navigation/, and one package per module) following this same
// Controller -> Service -> Repository layering.
public record ModuleTile(
    String id,
    String title,
    String description,
    String status, // "active" | "coming-soon"
    String path
) {}
