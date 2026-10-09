package com.codexateam.platform.booking.interfaces.rest.transform;

import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.interfaces.rest.resources.CreateBookingResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CreateBookingCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should transform CreateBookingResource into CreateBookingCommand (AAA)")
    void toCommandFromResource_ShouldTransformSuccessfully() {
        // Arrange
        Date start = new Date();
        Date end = new Date(start.getTime() + 86400000);
        var resource = new CreateBookingResource(10L, start, end);

        // Act
        var command = CreateBookingCommandFromResourceAssembler.toCommandFromResource(resource, 2L, 1L);

        // Assert
        assertNotNull(command);
        assertEquals(10L, command.vehicleId());
        assertEquals(2L, command.renterId());
        assertEquals(1L, command.ownerId());
        assertEquals(start, command.startDate());
        assertEquals(end, command.endDate());
    }

    @Test
    @DisplayName("BookingResourceFromEntityAssembler should transform Booking aggregate into BookingResource (AAA)")
    void toResourceFromEntity_ShouldTransformSuccessfully() {
        // Arrange
        Date start = new Date();
        Date end = new Date(start.getTime() + 86400000);
        var command = new CreateBookingCommand(10L, 2L, 1L, start, end);
        var booking = new Booking(command, 150.0);

        // Act
        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);

        // Assert
        assertNotNull(resource);
        assertEquals(10L, resource.vehicleId());
        assertEquals(2L, resource.renterId());
        assertEquals(1L, resource.ownerId());
        assertEquals(150.0, resource.totalPrice());
        assertEquals("PENDING", resource.status());
    }

    @Test
    @DisplayName("toCommandFromResource supports single day rental (start equals end) (AAA)")
    void toCommandFromResource_SingleDayRental_ShouldTransformSuccessfully() {
        // Arrange
        Date today = new Date();
        var resource = new CreateBookingResource(5L, today, today);

        // Act
        var command = CreateBookingCommandFromResourceAssembler.toCommandFromResource(resource, 8L, 3L);

        // Assert
        assertNotNull(command);
        assertEquals(5L, command.vehicleId());
        assertEquals(8L, command.renterId());
        assertEquals(3L, command.ownerId());
        assertEquals(today, command.startDate());
        assertEquals(today, command.endDate());
    }

    @Test
    @DisplayName("BookingResourceFromEntityAssembler reflects CONFIRMED status when booking is confirmed (AAA)")
    void toResourceFromEntity_ConfirmedStatus_ShouldTransformSuccessfully() {
        // Arrange
        Date start = new Date();
        var command = new CreateBookingCommand(10L, 2L, 1L, start, start);
        var booking = new Booking(command, 200.0);
        booking.confirm();

        // Act
        var resource = BookingResourceFromEntityAssembler.toResourceFromEntity(booking);

        // Assert
        assertNotNull(resource);
        assertEquals("CONFIRMED", resource.status());
        assertEquals(200.0, resource.totalPrice());
    }
}
