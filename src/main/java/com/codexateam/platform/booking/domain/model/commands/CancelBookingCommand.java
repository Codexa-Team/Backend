package com.codexateam.platform.booking.domain.model.commands;

/**
 * Command to cancel a booking.
 * This command is used when a renter (arrendatario) cancels their own booking.
 */
public record CancelBookingCommand(Long bookingId, Long renterId) {
}

