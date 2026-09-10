package com.codexateam.platform.booking.domain.model.commands;

/**
 * Command to delete a booking.
 * @param bookingId The id of the booking to be deleted.
 */
public record DeleteBookingCommand(Long bookingId) {
}

