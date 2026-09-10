package com.codexateam.platform.booking.domain.model.queries;

/**
 * Query to find all bookings made by a specific renter (Arrendatario).
 * Used for the "My Bookings" page.
 *
 */
public record GetBookingsByRenterIdQuery(Long renterId) {
}
