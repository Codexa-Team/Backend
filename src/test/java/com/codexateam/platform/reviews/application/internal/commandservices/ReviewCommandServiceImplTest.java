package com.codexateam.platform.reviews.application.internal.commandservices;

import com.codexateam.platform.booking.interfaces.acl.BookingContextFacade;
import com.codexateam.platform.reviews.domain.exceptions.CompletedBookingRequiredException;
import com.codexateam.platform.reviews.domain.exceptions.ReviewAlreadyExistsException;
import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewCommandServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingContextFacade bookingContextFacade;

    @InjectMocks
    private ReviewCommandServiceImpl reviewCommandService;

    // -------------------------------------------------------------------------
    // handle(CreateReviewCommand command) - CREATE REVIEW
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(CreateReviewCommand) should save review when booking is completed and no prior review (AAA)")
    void handle_CreateReviewCommand_ShouldSaveReview_WhenValid() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 5, "Excellent vehicle, spotless and smooth ride!");
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(10L, 2L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getVehicleId());
        assertEquals(2L, result.get().getRenterId());
        assertEquals(5, result.get().getRating());
        assertEquals("Excellent vehicle, spotless and smooth ride!", result.get().getComment());

        verify(bookingContextFacade, times(1)).hasCompletedBooking(2L, 10L);
        verify(reviewRepository, times(1)).existsByVehicleIdAndRenterId(10L, 2L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("handle(CreateReviewCommand) should throw CompletedBookingRequiredException when no completed booking (AAA)")
    void handle_CreateReviewCommand_ShouldThrowException_WhenBookingNotCompleted() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 4, "Good");
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(false);

        // Act & Assert
        assertThrows(CompletedBookingRequiredException.class, () -> reviewCommandService.handle(command));
        verify(bookingContextFacade, times(1)).hasCompletedBooking(2L, 10L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("handle(CreateReviewCommand) should throw ReviewAlreadyExistsException when user already reviewed (AAA)")
    void handle_CreateReviewCommand_ShouldThrowException_WhenAlreadyReviewed() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 5, "Repeat review");
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(10L, 2L)).thenReturn(true);

        // Act & Assert
        assertThrows(ReviewAlreadyExistsException.class, () -> reviewCommandService.handle(command));
        verify(bookingContextFacade, times(1)).hasCompletedBooking(2L, 10L);
        verify(reviewRepository, times(1)).existsByVehicleIdAndRenterId(10L, 2L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("handle(CreateReviewCommand) should return empty Optional when repository save throws exception (AAA)")
    void handle_CreateReviewCommand_ShouldReturnEmpty_WhenRepositorySaveFails() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 4, "Good car");
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(10L, 2L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenThrow(new RuntimeException("DB error"));

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("handle(CreateReviewCommand) should allow minimum rating 1 star (AAA)")
    void handle_CreateReviewCommand_ShouldAllowMinimumRating1() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 1, "Terrible condition");
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(10L, 2L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("handle(CreateReviewCommand) should allow maximum rating 5 stars (AAA)")
    void handle_CreateReviewCommand_ShouldAllowMaximumRating5() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 5, "Best car ever");
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(10L, 2L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("handle(CreateReviewCommand) should allow null comment (AAA)")
    void handle_CreateReviewCommand_ShouldAllowNullComment() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 4, null);
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(10L, 2L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("handle(CreateReviewCommand) should allow empty comment (AAA)")
    void handle_CreateReviewCommand_ShouldAllowEmptyComment() {
        // Arrange
        var command = new CreateReviewCommand(10L, 2L, 3, "");
        when(bookingContextFacade.hasCompletedBooking(2L, 10L)).thenReturn(true);
        when(reviewRepository.existsByVehicleIdAndRenterId(10L, 2L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}
