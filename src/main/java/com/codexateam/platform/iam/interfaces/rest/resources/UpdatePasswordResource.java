package com.codexateam.platform.iam.interfaces.rest.resources;
import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * DTO for password update with verification of current password.
 * Accepts either 'currentPassword' or 'oldPassword' from the client.
 * @param currentPassword the current password for verification
 * @param newPassword the new password to set
 */
public record UpdatePasswordResource(@JsonAlias("oldPassword") String currentPassword, String newPassword) { }
