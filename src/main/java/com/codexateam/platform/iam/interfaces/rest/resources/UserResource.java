package com.codexateam.platform.iam.interfaces.rest.resources;

import java.util.Set;

/**
 * DTO for returning user data.
 * @param id The user's ID.
 * @param name The user's full name.
 * @param email The user's email.
 * @param roles A set of role names.
 */
public record UserResource(Long id, String name, String email, Set<String> roles) {
}
