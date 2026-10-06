package com.codexateam.platform.reviews.domain.model.aggregates;

import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {

    @Test
    @DisplayName("Review constructor from CreateReviewCommand initializes all attributes (AAA)")
    void constructor_FromCommand_ShouldInitializeFields() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 5, "Outstanding service and clean car!");

        // Act
        Review review = new Review(command);

        // Assert
        assertEquals(10L, review.getVehicleId());
        assertEquals(2L, review.getRenterId());
        assertEquals(5, review.getRating());
        assertEquals("Outstanding service and clean car!", review.getComment());
    }

    @Test
    @DisplayName("Review supports minimum rating of 1 (AAA)")
    void constructor_WithMinimumRating1_ShouldInstantiate() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 1, "Disappointing");

        // Act
        Review review = new Review(command);

        // Assert
        assertEquals(1, review.getRating());
    }

    @Test
    @DisplayName("Review supports maximum rating of 5 (AAA)")
    void constructor_WithMaximumRating5_ShouldInstantiate() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 5, "Perfect");

        // Act
        Review review = new Review(command);

        // Assert
        assertEquals(5, review.getRating());
    }

    @Test
    @DisplayName("Review supports null comment (AAA)")
    void constructor_WithNullComment_ShouldAllowNull() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 4, null);

        // Act
        Review review = new Review(command);

        // Assert
        assertNull(review.getComment());
    }

    @Test
    @DisplayName("Review supports long comment (AAA)")
    void constructor_WithLongComment_ShouldPreserveComment() {
        // Arrange
        String longComment = "A".repeat(500);
        var command = new CreateReviewCommand(10L, 2L, 4, longComment);

        // Act
        Review review = new Review(command);

        // Assert
        assertEquals(longComment, review.getComment());
    }

    @Test
    @DisplayName("Review default constructor creates non-null entity (AAA)")
    void defaultConstructor_ShouldInstantiate() {
        // Arrange & Act
        Review review = new Review();

        // Assert
        assertNotNull(review);
    }
}
