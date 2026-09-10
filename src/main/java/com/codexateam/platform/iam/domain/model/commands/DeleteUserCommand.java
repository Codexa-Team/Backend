package com.codexateam.platform.iam.domain.model.commands;

/**
 * Command to delete a user.
 * @param userId The id of the user to be deleted.
 */
public record DeleteUserCommand(Long userId) {
}

