package com.codexateam.platform.reviews.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewExceptionsTest {

    @Test
    @DisplayName("CompletedBookingRequiredException formats message with renterId and vehicleId (AAA)")
    void completedBookingRequiredException_ShouldFormatMessage() {
        // Arrange & Act
        var ex = new CompletedBookingRequiredException(2L, 10L);

        // Assert
        assertTrue(ex.getMessage().contains("2"));
        assertTrue(ex.getMessage().contains("10"));
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("ReviewAlreadyExistsException formats message with renterId and vehicleId (AAA)")
    void reviewAlreadyExistsException_ShouldFormatMessage() {
        // Arrange & Act
        var ex = new ReviewAlreadyExistsException(2L, 10L);

        // Assert
        assertTrue(ex.getMessage().contains("2"));
        assertTrue(ex.getMessage().contains("10"));
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("ReviewNotFoundException formats message with reviewId (AAA)")
    void reviewNotFoundException_ShouldFormatMessage() {
        // Arrange & Act
        var ex = new ReviewNotFoundException(100L);

        // Assert
        assertTrue(ex.getMessage().contains("100"));
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("All review exceptions are unchecked RuntimeExceptions (AAA)")
    void exceptions_ShouldExtendRuntimeException() {
        // Arrange & Act
        Exception ex1 = new CompletedBookingRequiredException(1L, 2L);
        Exception ex2 = new ReviewAlreadyExistsException(1L, 2L);
        Exception ex3 = new ReviewNotFoundException(3L);

        // Assert
        assertInstanceOf(RuntimeException.class, ex1);
        assertInstanceOf(RuntimeException.class, ex2);
        assertInstanceOf(RuntimeException.class, ex3);
    }
}
