package com.codexateam.platform.iam.domain.model.commands;

/**
 * Command to update a user's profile (name, email).
 * @param userId the target user id
 * @param name new full name
 * @param email new email
 */
public record UpdateUserCommand(Long userId, String name, String email) { }

