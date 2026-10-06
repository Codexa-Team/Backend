package com.codexateam.platform.booking.application.internal.commandservices;

import com.codexateam.platform.booking.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.booking.domain.exceptions.*;
import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.*;
import com.codexateam.platform.booking.domain.model.valueobjects.BookingStatus;
import com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingCommandServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ExternalListingsService externalListingsService;

    @InjectMocks
    private BookingCommandServiceImpl bookingCommandService;

    // Helper to generate dates
    private Date getDate(int addDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, addDays);
        return cal.getTime();
    }

    // -------------------------------------------------------------------------
    // handle(CreateBookingCommand command) - CREATE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(CreateBookingCommand) should save booking when data and availability are valid (AAA)")
    void handle_CreateBookingCommand_ShouldSaveBooking_WhenValid() {
        // Arrange
        Date start = getDate(1);
        Date end = getDate(3);
        var command = new CreateBookingCommand(10L, 2L, 1L, start, end);
        var vehicleResource = new VehicleResource(10L, "Toyota", "Yaris", 2021, 40.0, "available", "yaris.jpg", 1L, new Date());

        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicleResource));
        when(bookingRepository.existsByVehicleIdAndBookingStatus_StatusAndStartDateLessThanAndEndDateGreaterThan(
                eq(10L), eq(BookingStatus.PENDING), any(Date.class), any(Date.class))).thenReturn(false);
        when(bookingRepository.existsByVehicleIdAndBookingStatus_StatusAndStartDateLessThanAndEndDateGreaterThan(
                eq(10L), eq(BookingStatus.CONFIRMED), any(Date.class), any(Date.class))).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Booking> result = bookingCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getVehicleId());
        assertEquals(2L, result.get().getRenterId());
        assertEquals(1L, result.get().getOwnerId());
        assertEquals(BookingStatus.PENDING, result.get().getStatus());
        assertTrue(result.get().getTotalPrice() > 0);

        verify(externalListingsService, times(1)).fetchVehicleById(10L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("handle(CreateBookingCommand) should throw OwnerMismatchException when ownerId mismatch (AAA)")
    void handle_CreateBookingCommand_ShouldThrowOwnerMismatchException_WhenOwnerDiffers() {
        // Arrange
        Date start = getDate(1);
        Date end = getDate(3);
        var command = new CreateBookingCommand(10L, 2L, 999L, start, end); // owner 999L instead of 1L
        var vehicleResource = new VehicleResource(10L, "Toyota", "Yaris", 2021, 40.0, "available", "yaris.jpg", 1L, new Date());

        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicleResource));

        // Act & Assert
        assertThrows(OwnerMismatchException.class, () -> bookingCommandService.handle(command));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("handle(CreateBookingCommand) should throw InvalidBookingDatesException when startDate after endDate (AAA)")
    void handle_CreateBookingCommand_ShouldThrowInvalidDatesException_WhenStartAfterEnd() {
        // Arrange
        Date start = getDate(5);
        Date end = getDate(2);
        var command = new CreateBookingCommand(10L, 2L, 1L, start, end);
        var vehicleResource = new VehicleResource(10L, "Toyota", "Yaris", 2021, 40.0, "available", "yaris.jpg", 1L, new Date());

        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicleResource));

        // Act & Assert
        assertThrows(InvalidBookingDatesException.class, () -> bookingCommandService.handle(command));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("handle(CreateBookingCommand) should throw VehicleNotAvailableException when dates overlap (AAA)")
    void handle_CreateBookingCommand_ShouldThrowException_WhenDatesOverlap() {
        // Arrange
        Date start = getDate(1);
        Date end = getDate(3);
        var command = new CreateBookingCommand(10L, 2L, 1L, start, end);
        var vehicleResource = new VehicleResource(10L, "Toyota", "Yaris", 2021, 40.0, "available", "yaris.jpg", 1L, new Date());

        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicleResource));
        when(bookingRepository.existsByVehicleIdAndBookingStatus_StatusAndStartDateLessThanAndEndDateGreaterThan(
                eq(10L), eq(BookingStatus.PENDING), any(Date.class), any(Date.class))).thenReturn(true);

        // Act & Assert
        assertThrows(VehicleNotAvailableException.class, () -> bookingCommandService.handle(command));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // -------------------------------------------------------------------------
    // handle(ConfirmBookingCommand command) - CONFIRM
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(ConfirmBookingCommand) should transition to CONFIRMED and update vehicle status (AAA)")
    void handle_ConfirmBookingCommand_ShouldConfirmBooking_WhenPending() {
        // Arrange
        var booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, getDate(1), getDate(2)), 80.0);
        var command = new ConfirmBookingCommand(100L);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Booking> result = bookingCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BookingStatus.CONFIRMED, result.get().getStatus());
        verify(externalListingsService, times(1)).updateVehicleStatus(10L, "rented");
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("handle(ConfirmBookingCommand) should throw BookingNotFoundException when booking missing (AAA)")
    void handle_ConfirmBookingCommand_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookingNotFoundException.class, () -> bookingCommandService.handle(new ConfirmBookingCommand(999L)));
    }

    // -------------------------------------------------------------------------
    // handle(RejectBookingCommand command) - REJECT
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(RejectBookingCommand) should transition to REJECTED and liberate vehicle (AAA)")
    void handle_RejectBookingCommand_ShouldRejectBooking_WhenPending() {
        // Arrange
        var booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, getDate(1), getDate(2)), 80.0);
        var command = new RejectBookingCommand(100L);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Booking> result = bookingCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BookingStatus.REJECTED, result.get().getStatus());
        verify(externalListingsService, times(1)).updateVehicleStatus(10L, "available");
        verify(bookingRepository, times(1)).save(booking);
    }

    // -------------------------------------------------------------------------
    // handle(CancelBookingCommand command) - CANCEL
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(CancelBookingCommand) should cancel booking when requested by authorized renter (AAA)")
    void handle_CancelBookingCommand_ShouldCancel_WhenAuthorizedRenter() {
        // Arrange
        var booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, getDate(1), getDate(2)), 80.0);
        var command = new CancelBookingCommand(100L, 2L); // renter 2L

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Booking> result = bookingCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BookingStatus.CANCELED, result.get().getStatus());
        verify(externalListingsService, times(1)).updateVehicleStatus(10L, "available");
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("handle(CancelBookingCommand) should throw UnauthorizedBookingAccessException if wrong renter (AAA)")
    void handle_CancelBookingCommand_ShouldThrowUnauthorized_WhenWrongRenter() {
        // Arrange
        var booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, getDate(1), getDate(2)), 80.0);
        var command = new CancelBookingCommand(100L, 888L); // unauthorized renter

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(UnauthorizedBookingAccessException.class, () -> bookingCommandService.handle(command));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // -------------------------------------------------------------------------
    // handle(DeleteBookingCommand command) - DELETE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(DeleteBookingCommand) should delete booking when exists (AAA)")
    void handle_DeleteBookingCommand_ShouldDeleteBooking() {
        // Arrange
        when(bookingRepository.existsById(50L)).thenReturn(true);

        // Act
        bookingCommandService.handle(new DeleteBookingCommand(50L));

        // Assert
        verify(bookingRepository, times(1)).deleteById(50L);
    }
}
