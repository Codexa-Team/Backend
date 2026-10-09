package com.codexateam.platform.booking.domain.model.aggregates;

import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.domain.model.valueobjects.BookingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    @DisplayName("Booking constructor sets attributes and initializes status to PENDING (AAA)")
    void constructor_FromCommand_ShouldInitializeFields() {
        // Arrange
        Date start = new Date();
        Date end = new Date(start.getTime() + 86400000L);
        var command = new CreateBookingCommand(10L, 2L, 1L, start, end);

        // Act
        Booking booking = new Booking(command, 120.0);

        // Assert
        assertEquals(10L, booking.getVehicleId());
        assertEquals(2L, booking.getRenterId());
        assertEquals(1L, booking.getOwnerId());
        assertEquals(start, booking.getStartDate());
        assertEquals(end, booking.getEndDate());
        assertEquals(120.0, booking.getTotalPrice());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
    }

    @Test
    @DisplayName("Booking confirm() transitions status to CONFIRMED (AAA)")
    void confirm_ShouldTransitionToConfirmed() {
        // Arrange
        Booking booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, new Date(), new Date()), 100.0);

        // Act
        booking.confirm();

        // Assert
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    @DisplayName("Booking reject() transitions status to REJECTED (AAA)")
    void reject_ShouldTransitionToRejected() {
        // Arrange
        Booking booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, new Date(), new Date()), 100.0);

        // Act
        booking.reject();

        // Assert
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    @DisplayName("Booking cancel() transitions status to CANCELED (AAA)")
    void cancel_ShouldTransitionToCanceled() {
        // Arrange
        Booking booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, new Date(), new Date()), 100.0);

        // Act
        booking.cancel();

        // Assert
        assertEquals(BookingStatus.CANCELED, booking.getStatus());
    }

    @Test
    @DisplayName("Booking setters for endDate and totalPrice update values properly (AAA)")
    void setters_ShouldUpdateEndDateAndPrice() {
        // Arrange
        Date start = new Date();
        Booking booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, start, start), 50.0);
        Date newEnd = new Date(start.getTime() + 172800000L);

        // Act
        booking.setEndDate(newEnd);
        booking.setTotalPrice(150.0);

        // Assert
        assertEquals(newEnd, booking.getEndDate());
        assertEquals(150.0, booking.getTotalPrice());
    }

    @Test
    @DisplayName("Booking default constructor initializes empty aggregate (AAA)")
    void defaultConstructor_ShouldInstantiate() {
        // Arrange & Act
        Booking booking = new Booking();

        // Assert
        assertNotNull(booking);
        assertNull(booking.getStatus());
    }
}
