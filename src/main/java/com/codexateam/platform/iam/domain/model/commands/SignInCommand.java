package com.codexateam.platform.iam.domain.model.commands;

/**
 * Command to initiate a user sign-in process.
 * @param email The user's email.
 * @param password The user's raw password.
 */
public record SignInCommand(String email, String password) {
}
