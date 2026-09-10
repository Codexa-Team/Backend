package com.codexateam.platform.booking.domain.model.commands;

import java.util.Date;

/**
 * Command to update an existing booking's end date and total price.
 * Use this when adjusting the duration of a booking (e.g., extending rental period)
 * and recalculating the final amount to be charged.
 *
 * @param bookingId The ID of the booking to update.
 * @param endDate The new end date of the booking.
 * @param totalPrice The recalculated total price reflecting the new duration.
 */
public record UpdateBookingCommand(
        Long bookingId,
        Date endDate,
        Double totalPrice
) {
}

