package com.codexateam.platform.iam.interfaces.rest.resources;

/**
 * DTO for receiving sign-up data.
 * Based on db.json (name, email, password, role).
 * @param name The user's full name.
 * @param email The user's email.
 * @param password The user's raw password.
 * @param role The role name (e.g., "arrendador" or "arrendatario").
 */
public record SignUpResource(String name, String email, String password, String role) {
}
