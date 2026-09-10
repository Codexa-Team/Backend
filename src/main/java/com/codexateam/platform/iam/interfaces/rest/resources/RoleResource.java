package com.codexateam.platform.iam.interfaces.rest.resources;

/**
 * DTO for returning Role data.
 * @param id The role's ID.
 * @param name The role's name.
 */
public record RoleResource(Long id, String name) {
}
