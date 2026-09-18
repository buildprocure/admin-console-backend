package com.buildprocure.admin_console_backend.accesscontrol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// GET /api/access-control/{userId} response body.
//
// - menu: the nav items this user IS allowed to see, across all modules.
//   The frontend filters this list by module (see useMenu.jsx) instead of
//   deciding per-role which items to render itself.
// - pagesExcluded / buttonsExcluded: routes and action-button ids this
//   user is explicitly NOT allowed to use, even if reached directly (deep
//   link, browser back/forward, etc). These are separate from `menu`
//   because they can restrict things that aren't nav items at all (a
//   button inside a page the user CAN otherwise open).
//
// Serialized as pages_excluded / buttons_excluded (snake_case) to match
// the contract agreed with the frontend; everything else stays camelCase.
public record AccessControlResponse(
    String userId,
    String role,
    List<MenuItem> menu,
    @JsonProperty("pages_excluded") List<String> pagesExcluded,
    @JsonProperty("buttons_excluded") List<String> buttonsExcluded
) {}
