package com.codexateam.platform.iam.interfaces.rest.resources;

/**
 * DTO for receiving sign-in credentials.
 * @param email The user's email.
 * @param password The user's raw password.
 */
public record SignInResource(String email, String password) {
}
