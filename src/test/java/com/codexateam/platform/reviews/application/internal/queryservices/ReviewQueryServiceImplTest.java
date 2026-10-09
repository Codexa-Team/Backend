package com.codexateam.platform.reviews.application.internal.queryservices;

import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewByIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByRenterIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByVehicleIdQuery;
import com.codexateam.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewQueryServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewQueryServiceImpl reviewQueryService;

    @Test
    @DisplayName("handle(GetReviewsByVehicleIdQuery) should return reviews for vehicle (AAA)")
    void handle_GetReviewsByVehicleIdQuery_ShouldReturnReviews() {
        // Arrange
        var r1 = new Review(new CreateReviewCommand(10L, 1L, 5, "Great!"));
        var r2 = new Review(new CreateReviewCommand(10L, 2L, 4, "Good!"));
        when(reviewRepository.findByVehicleId(10L)).thenReturn(List.of(r1, r2));

        // Act
        List<Review> result = reviewQueryService.handle(new GetReviewsByVehicleIdQuery(10L));

        // Assert
        assertEquals(2, result.size());
        verify(reviewRepository, times(1)).findByVehicleId(10L);
    }

    @Test
    @DisplayName("handle(GetReviewsByRenterIdQuery) should return reviews by renter (AAA)")
    void handle_GetReviewsByRenterIdQuery_ShouldReturnReviews() {
        // Arrange
        var r1 = new Review(new CreateReviewCommand(10L, 3L, 5, "Loved it"));
        when(reviewRepository.findByRenterId(3L)).thenReturn(List.of(r1));

        // Act
        List<Review> result = reviewQueryService.handle(new GetReviewsByRenterIdQuery(3L));

        // Assert
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByRenterId(3L);
    }

    @Test
    @DisplayName("handle(GetReviewByIdQuery) should return review when exists (AAA)")
    void handle_GetReviewByIdQuery_ShouldReturnReview_WhenExists() {
        // Arrange
        var r1 = new Review(new CreateReviewCommand(10L, 3L, 5, "Super"));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(r1));

        // Act
        Optional<Review> result = reviewQueryService.handle(new GetReviewByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getRating());
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("handle(GetReviewsByVehicleIdQuery) should return empty list when no reviews exist (AAA)")
    void handle_GetReviewsByVehicleIdQuery_ShouldReturnEmptyList_WhenNoReviews() {
        // Arrange
        when(reviewRepository.findByVehicleId(999L)).thenReturn(List.of());

        // Act
        List<Review> result = reviewQueryService.handle(new GetReviewsByVehicleIdQuery(999L));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findByVehicleId(999L);
    }

    @Test
    @DisplayName("handle(GetReviewsByRenterIdQuery) should return empty list when renter has no reviews (AAA)")
    void handle_GetReviewsByRenterIdQuery_ShouldReturnEmptyList_WhenNoReviews() {
        // Arrange
        when(reviewRepository.findByRenterId(999L)).thenReturn(List.of());

        // Act
        List<Review> result = reviewQueryService.handle(new GetReviewsByRenterIdQuery(999L));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findByRenterId(999L);
    }

    @Test
    @DisplayName("handle(GetReviewByIdQuery) should return empty Optional when review not found (AAA)")
    void handle_GetReviewByIdQuery_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(reviewRepository.findById(404L)).thenReturn(Optional.empty());

        // Act
        Optional<Review> result = reviewQueryService.handle(new GetReviewByIdQuery(404L));

        // Assert
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findById(404L);
    }
}
