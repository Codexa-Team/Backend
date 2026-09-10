package com.codexateam.platform.iam.domain.model.commands;

/**
 * Command to update a user's password verifying the current password.
 * @param userId the target user id
 * @param currentPassword the current password provided for verification
 * @param newPassword the new raw password
 */
public record UpdatePasswordCommand(Long userId, String currentPassword, String newPassword) { }
