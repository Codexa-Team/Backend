package com.codexateam.platform.reviews.domain.services;

import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import java.util.Optional;

/**
 * Service interface for handling Review commands.
 */
public interface ReviewCommandService {
    /**
     * Handles the CreateReviewCommand.
     * @param command The command to create a review.
     * @return An Optional containing the created Review aggregate.
     */
    Optional<Review> handle(CreateReviewCommand command);
}
