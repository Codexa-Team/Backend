package com.codexateam.platform.iam.domain.model.queries;

/**
 * Query to find a user by their email address.
 * @param email The user's email.
 */
public record GetUserByEmailQuery(String email) {
}
