package com.codexateam.platform.iam.interfaces.rest.resources;

/**
 * DTO for profile update (name, email).
 * @param name new full name
 * @param email new email
 */
public record UpdateUserResource(String name, String email) { }

