package com.codexateam.platform.booking.domain.services;

import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CancelBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.ConfirmBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.RejectBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.DeleteBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.UpdateBookingCommand;

import java.util.Optional;

/**
 * Service interface for handling Booking commands.
 */
public interface BookingCommandService {
    /**
     * Handles the CreateBookingCommand.
     * @param command The command to create a booking.
     * @return An Optional containing the created Booking aggregate.
     */
    Optional<Booking> handle(CreateBookingCommand command);

    /**
     * Handles the ConfirmBookingCommand.
     * @param command The command to confirm a booking.
     * @return An Optional containing the confirmed Booking aggregate.
     */
    Optional<Booking> handle(ConfirmBookingCommand command);

    /**
     * Handles the RejectBookingCommand.
     * @param command The command to reject a booking.
     * @return An Optional containing the rejected Booking aggregate.
     */
    Optional<Booking> handle(RejectBookingCommand command);

    /**
     * Handles the CancelBookingCommand.
     * @param command The command to cancel a booking.
     * @return An Optional containing the canceled Booking aggregate.
     */
    Optional<Booking> handle(CancelBookingCommand command);

    /**
     * Handles the UpdateBookingCommand.
     * @param command The command containing updated booking data (end date, total price).
     * @return An Optional containing the updated Booking aggregate.
     */
    Optional<Booking> handle(UpdateBookingCommand command);

    /**
     * Handles the DeleteBookingCommand.
     * @param command The command to delete a booking.
     */
    void handle(DeleteBookingCommand command);
}
