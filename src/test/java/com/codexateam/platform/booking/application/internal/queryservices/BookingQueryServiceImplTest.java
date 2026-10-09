package com.codexateam.platform.booking.application.internal.queryservices;

import com.codexateam.platform.booking.domain.exceptions.BookingNotFoundException;
import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.domain.model.queries.GetBookingByIdQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingByVehicleIdAndDateQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByOwnerIdQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByRenterIdQuery;
import com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingQueryServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingQueryServiceImpl bookingQueryService;

    @Test
    @DisplayName("handle(GetBookingByIdQuery) should return booking when exists (AAA)")
    void handle_GetBookingByIdQuery_ShouldReturnBooking_WhenExists() {
        // Arrange
        var booking = new Booking(new CreateBookingCommand(10L, 2L, 1L, new Date(), new Date()), 100.0);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act
        Optional<Booking> result = bookingQueryService.handle(new GetBookingByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getVehicleId());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("handle(GetBookingByIdQuery) should throw BookingNotFoundException when not found (AAA)")
    void handle_GetBookingByIdQuery_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookingNotFoundException.class, () -> bookingQueryService.handle(new GetBookingByIdQuery(999L)));
        verify(bookingRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("handle(GetBookingsByRenterIdQuery) should return bookings for renter (AAA)")
    void handle_GetBookingsByRenterIdQuery_ShouldReturnBookings() {
        // Arrange
        var b1 = new Booking(new CreateBookingCommand(10L, 5L, 1L, new Date(), new Date()), 100.0);
        when(bookingRepository.findByRenterId(5L)).thenReturn(List.of(b1));

        // Act
        List<Booking> result = bookingQueryService.handle(new GetBookingsByRenterIdQuery(5L));

        // Assert
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getRenterId());
        verify(bookingRepository, times(1)).findByRenterId(5L);
    }

    @Test
    @DisplayName("handle(GetBookingsByOwnerIdQuery) should return bookings for owner (AAA)")
    void handle_GetBookingsByOwnerIdQuery_ShouldReturnBookings() {
        // Arrange
        var b1 = new Booking(new CreateBookingCommand(10L, 5L, 20L, new Date(), new Date()), 100.0);
        when(bookingRepository.findByOwnerId(20L)).thenReturn(List.of(b1));

        // Act
        List<Booking> result = bookingQueryService.handle(new GetBookingsByOwnerIdQuery(20L));

        // Assert
        assertEquals(1, result.size());
        assertEquals(20L, result.get(0).getOwnerId());
        verify(bookingRepository, times(1)).findByOwnerId(20L);
    }

    @Test
    @DisplayName("handle(GetBookingByVehicleIdAndDateQuery) should find booking intersecting date (AAA)")
    void handle_GetBookingByVehicleIdAndDateQuery_ShouldReturnBooking() {
        // Arrange
        Date now = new Date();
        var booking = new Booking(new CreateBookingCommand(10L, 5L, 20L, now, now), 100.0);
        when(bookingRepository.findFirstByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(10L, now, now))
                .thenReturn(Optional.of(booking));

        // Act
        Optional<Booking> result = bookingQueryService.handle(new GetBookingByVehicleIdAndDateQuery(10L, now));

        // Assert
        assertTrue(result.isPresent());
        verify(bookingRepository, times(1)).findFirstByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(10L, now, now);
    }

    @Test
    @DisplayName("handle(GetBookingsByRenterIdQuery) should return empty list when no bookings exist (AAA)")
    void handle_GetBookingsByRenterIdQuery_ShouldReturnEmptyList_WhenNoBookings() {
        // Arrange
        when(bookingRepository.findByRenterId(999L)).thenReturn(List.of());

        // Act
        List<Booking> result = bookingQueryService.handle(new GetBookingsByRenterIdQuery(999L));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findByRenterId(999L);
    }
}
