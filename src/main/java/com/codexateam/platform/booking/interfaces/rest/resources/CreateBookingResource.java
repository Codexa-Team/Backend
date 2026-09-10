package com.codexateam.platform.booking.interfaces.rest.resources;

import java.util.Date;

/**
 * DTO for creating a new booking.
 * 'renterId' will be taken from the authenticated token.
 * 'ownerId' will be fetched via the vehicleId.
 */
public record CreateBookingResource(
        Long vehicleId,
        Date startDate,
        Date endDate
) {
}
