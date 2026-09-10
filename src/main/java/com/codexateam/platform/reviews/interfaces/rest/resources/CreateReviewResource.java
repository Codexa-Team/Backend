package com.codexateam.platform.reviews.interfaces.rest.resources;

/**
 * DTO for creating a new review.
 * 'renterId' will be taken from the authenticated token.
 *
 */
public record CreateReviewResource(
        Long vehicleId,
        Integer rating,
        String comment
) {
}
