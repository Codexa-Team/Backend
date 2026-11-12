package com.codexateam.platform.booking.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    @DisplayName("BookingStatus.createPending() creates status with PENDING value (AAA)")
    void factory_pending_ShouldCreatePendingStatus() {
        // Arrange & Act
        BookingStatus status = BookingStatus.createPending();

        // Assert
        assertEquals("PENDING", status.status());
        assertTrue(status.isPending());
        assertFalse(status.isConfirmed());
        assertFalse(status.isRejected());
        assertFalse(status.isCanceled());
        assertTrue(status.isActive());
    }

    @Test
    @DisplayName("BookingStatus.createConfirmed() creates status with CONFIRMED value (AAA)")
    void factory_confirmed_ShouldCreateConfirmedStatus() {
        // Arrange & Act
        BookingStatus status = BookingStatus.createConfirmed();

        // Assert
        assertEquals("CONFIRMED", status.status());
        assertTrue(status.isConfirmed());
        assertFalse(status.isPending());
        assertTrue(status.isActive());
    }

    @Test
    @DisplayName("BookingStatus.createRejected() creates status with REJECTED value (AAA)")
    void factory_rejected_ShouldCreateRejectedStatus() {
        // Arrange & Act
        BookingStatus status = BookingStatus.createRejected();

        // Assert
        assertEquals("REJECTED", status.status());
        assertTrue(status.isRejected());
        assertFalse(status.isActive());
    }

    @Test
    @DisplayName("BookingStatus.createCanceled() creates status with CANCELED value (AAA)")
    void factory_canceled_ShouldCreateCanceledStatus() {
        // Arrange & Act
        BookingStatus status = BookingStatus.createCanceled();

        // Assert
        assertEquals("CANCELED", status.status());
        assertTrue(status.isCanceled());
        assertFalse(status.isActive());
    }

    @Test
    @DisplayName("BookingStatus constructor throws IllegalArgumentException on null (AAA)")
    void constructor_WhenNull_ShouldThrowException() {
        // Arrange & Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new BookingStatus(null));
        assertEquals("Booking status cannot be null or empty", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    @DisplayName("BookingStatus constructor throws IllegalArgumentException on blank (AAA)")
    void constructor_WhenBlank_ShouldThrowException(String blank) {
        // Arrange & Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new BookingStatus(blank));
        assertEquals("Booking status cannot be null or empty", ex.getMessage());
    }

    @Test
    @DisplayName("BookingStatus equals and hashCode contract holds (AAA)")
    void equalsAndHashCode_WhenSameStatus_ShouldBeEqual() {
        // Arrange
        BookingStatus s1 = new BookingStatus("PENDING");
        BookingStatus s2 = BookingStatus.createPending();
        BookingStatus s3 = BookingStatus.createConfirmed();

        // Act & Assert
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3);
    }
}
