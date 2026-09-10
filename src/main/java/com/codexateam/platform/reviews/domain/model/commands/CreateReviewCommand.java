package com.codexateam.platform.reviews.domain.model.commands;

/**
 * Command to create a new review.
 * @param vehicleId The ID of the vehicle being reviewed.
 * @param renterId The ID of the user (Arrendatario) writing the review.
 * @param rating The rating (1-5).
 * @param comment The review comment.
 */
public record CreateReviewCommand(
        Long vehicleId,
        Long renterId,
        Integer rating,
        String comment
) {
}
