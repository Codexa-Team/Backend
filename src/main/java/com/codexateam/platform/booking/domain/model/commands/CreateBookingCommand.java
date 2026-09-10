package com.codexateam.platform.booking.domain.model.commands;

import java.util.Date;

/**
 * Command to create a new booking.
 * @param vehicleId The ID of the vehicle being booked.
 * @param renterId The ID of the user (Arrendatario) making the booking.
 * @param ownerId The ID of the user (Arrendador) who owns the vehicle.
 * @param startDate The start date of the booking.
 * @param endDate The end date of the booking.
 */
public record CreateBookingCommand(
        Long vehicleId,
        Long renterId,
        Long ownerId,
        Date startDate,
        Date endDate
) {
}
