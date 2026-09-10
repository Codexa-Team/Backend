package com.codexateam.platform.booking.interfaces.rest.resources;

import java.util.Date;

/**
 * DTO for returning booking data.
 * Based on db.json 'bookings' table.
 */
public record BookingResource(
        Long id,
        Long vehicleId,
        Long renterId,
        Long ownerId,
        Date startDate,
        Date endDate,
        Double totalPrice,
        String status,
        Date createdAt
) {
}
