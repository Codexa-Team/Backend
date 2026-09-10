package com.codexateam.platform.iam.interfaces.rest.resources;

import java.util.Set;

/**
 * DTO for returning an authenticated user's data.
 * @param id The user's ID.
 * @param email The user's email.
 * @param name The user's full name.
 * @param roles The user's roles (e.g., "ROLE_ARRENDADOR").
 * @param token The JWT token.
 */
public record AuthenticatedUserResource(Long id, String email, String name, Set<String> roles, String token) {
}