package com.codexateam.platform.iam.domain.model.queries;

/**
 * Query to find a user by their unique ID.
 * @param userId The user's ID.
 */
public record GetUserByIdQuery(Long userId) {
}
