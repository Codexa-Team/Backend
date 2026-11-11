package com.codexateam.platform.booking.interfaces.acl;

import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.queries.GetBookingByVehicleIdAndDateQuery;
import com.codexateam.platform.booking.domain.model.queries.GetBookingsByRenterIdQuery;
import com.codexateam.platform.booking.domain.model.valueobjects.BookingStatus;
import com.codexateam.platform.booking.domain.services.BookingQueryService;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * ACL Facade exposing Booking context operations without coupling callers to internal layers.
 */
@Service
public class BookingContextFacade {

    private final BookingQueryService bookingQueryService;

    public BookingContextFacade(BookingQueryService bookingQueryService) {
        this.bookingQueryService = bookingQueryService;
    }

    /**
     * Retrieves an active booking ID for a given vehicle at a specific timestamp.
     * Used by the IoT bounded context to track active bookings.
     *
     * @param vehicleId The vehicle ID to search for.
     * @param timestamp The timestamp to check for an active booking.
     * @return An Optional containing the booking ID if found.
     */
    public Optional<Long> getBookingIdByVehicleIdAndDate(Long vehicleId, Date timestamp) {
        var query = new GetBookingByVehicleIdAndDateQuery(vehicleId, timestamp);
        var booking = bookingQueryService.handle(query);
        return booking.map(Booking::getId);
    }

    /**
     * Checks if a renter has completed at least one booking for the specified vehicle.
     * A booking counts as completed if status is CANCELED, REJECTED, or CONFIRMED with a past end date.
     * Used by Reviews to confirm prior rental eligibility before publishing a review.
     *
     * @param renterId The ID of the renter.
     * @param vehicleId The ID of the vehicle.
     * @return true if the renter has a completed booking for the vehicle, false otherwise.
     */
    public boolean hasCompletedBooking(Long renterId, Long vehicleId) {
        var bookings = bookingQueryService.handle(new GetBookingsByRenterIdQuery(renterId));
        Date now = new Date();
        return bookings.stream().anyMatch(b ->
                b.getVehicleId().equals(vehicleId) && (
                        BookingStatus.CANCELED.equalsIgnoreCase(b.getStatus())
                        || BookingStatus.REJECTED.equalsIgnoreCase(b.getStatus())
                        || (BookingStatus.CONFIRMED.equalsIgnoreCase(b.getStatus())
                                && b.getEndDate() != null
                                && b.getEndDate().before(now))
                )
        );
    }
}
