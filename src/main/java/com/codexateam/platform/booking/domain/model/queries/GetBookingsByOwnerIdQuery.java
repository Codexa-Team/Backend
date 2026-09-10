package com.codexateam.platform.booking.domain.model.queries;

/**
 * Query to find all booking requests for vehicles owned by a specific owner (Arrendador).
 * Used for the "Booking Requests" page.
 *
 */
public record GetBookingsByOwnerIdQuery(Long ownerId) {
}
