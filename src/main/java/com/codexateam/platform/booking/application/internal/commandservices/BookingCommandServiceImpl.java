package com.codexateam.platform.booking.application.internal.commandservices;

import com.codexateam.platform.booking.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.booking.domain.exceptions.BookingNotFoundException;
import com.codexateam.platform.booking.domain.exceptions.InvalidBookingDatesException;
import com.codexateam.platform.booking.domain.exceptions.InvalidBookingStatusException;
import com.codexateam.platform.booking.domain.exceptions.OwnerMismatchException;
import com.codexateam.platform.booking.domain.exceptions.UnauthorizedBookingAccessException;
import com.codexateam.platform.booking.domain.exceptions.VehicleNotAvailableException;
import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CancelBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.ConfirmBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.DeleteBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.RejectBookingCommand;
import com.codexateam.platform.booking.domain.model.commands.UpdateBookingCommand;
import com.codexateam.platform.booking.domain.model.valueobjects.BookingStatus;
import com.codexateam.platform.booking.domain.services.BookingCommandService;
import com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link BookingCommandService} handling booking write operations.
 */
@Service
public class BookingCommandServiceImpl implements BookingCommandService {

    private static final String VEHICLE_STATUS_AVAILABLE = "available";
    private static final String VEHICLE_STATUS_RENTED = "rented";

    private final BookingRepository bookingRepository;
    private final ExternalListingsService externalListingsService;

    /**
     * Creates a new booking command service.
     *
     * @param bookingRepository repository for persisting bookings
     * @param externalListingsService ACL gateway to vehicle listings
     */
    public BookingCommandServiceImpl(
            BookingRepository bookingRepository,
            ExternalListingsService externalListingsService) {
        this.bookingRepository = bookingRepository;
        this.externalListingsService = externalListingsService;
    }

    /**
     * Handles booking creation after verifying vehicle ownership, availability, and date overlap.
     *
     * @param command booking creation command
     * @return created booking, or empty if persistence fails
     */
    @Override
    public Optional<Booking> handle(CreateBookingCommand command) {
        var vehicleResource = externalListingsService.fetchVehicleById(command.vehicleId())
                .orElseThrow(() -> new VehicleNotAvailableException(command.vehicleId(), "not found"));
        validateBookingRequest(command, vehicleResource);
        Double totalPrice = calculateTotalPrice(
                vehicleResource.pricePerDay(),
                command.startDate(),
                command.endDate()
        );
        var booking = new Booking(command, totalPrice);
        try {
            bookingRepository.save(booking);
            return Optional.of(booking);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Confirms a pending booking and marks the vehicle as rented.
     *
     * @param command booking confirmation command
     * @return confirmed booking, or empty if persistence fails
     */
    @Override
    public Optional<Booking> handle(ConfirmBookingCommand command) {
        var booking = bookingRepository.findById(command.bookingId());
        if (booking.isEmpty()) {
            throw new BookingNotFoundException(command.bookingId());
        }
        var bookingToConfirm = booking.get();
        if (!BookingStatus.PENDING.equals(bookingToConfirm.getStatus())) {
            throw new InvalidBookingStatusException(
                    command.bookingId(),
                    bookingToConfirm.getStatus(),
                    BookingStatus.PENDING);
        }
        bookingToConfirm.confirm();
        try {
            bookingRepository.save(bookingToConfirm);
            externalListingsService.updateVehicleStatus(bookingToConfirm.getVehicleId(), VEHICLE_STATUS_RENTED);
            return Optional.of(bookingToConfirm);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Rejects a pending booking and leaves the vehicle available.
     *
     * @param command booking rejection command
     * @return rejected booking, or empty if persistence fails
     */
    @Override
    public Optional<Booking> handle(RejectBookingCommand command) {
        var booking = bookingRepository.findById(command.bookingId());
        if (booking.isEmpty()) {
            throw new BookingNotFoundException(command.bookingId());
        }
        var bookingToReject = booking.get();
        if (!BookingStatus.PENDING.equals(bookingToReject.getStatus())) {
            throw new InvalidBookingStatusException(
                    command.bookingId(),
                    bookingToReject.getStatus(),
                    BookingStatus.PENDING);
        }
        bookingToReject.reject();
        try {
            bookingRepository.save(bookingToReject);
            externalListingsService.updateVehicleStatus(bookingToReject.getVehicleId(), VEHICLE_STATUS_AVAILABLE);
            return Optional.of(bookingToReject);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Cancels a pending or confirmed booking on behalf of its renter.
     *
     * @param command booking cancellation command
     * @return canceled booking, or empty if persistence fails
     */
    @Override
    public Optional<Booking> handle(CancelBookingCommand command) {
        var booking = bookingRepository.findById(command.bookingId());
        if (booking.isEmpty()) {
            throw new BookingNotFoundException(command.bookingId());
        }
        var bookingToCancel = booking.get();
        if (!bookingToCancel.getRenterId().equals(command.renterId())) {
            throw new UnauthorizedBookingAccessException(command.bookingId(), command.renterId());
        }
        if (!BookingStatus.PENDING.equals(bookingToCancel.getStatus())
                && !BookingStatus.CONFIRMED.equals(bookingToCancel.getStatus())) {
            throw new InvalidBookingStatusException(
                    command.bookingId(),
                    bookingToCancel.getStatus(),
                    BookingStatus.PENDING,
                    BookingStatus.CONFIRMED);
        }
        bookingToCancel.cancel();
        try {
            bookingRepository.save(bookingToCancel);
            externalListingsService.updateVehicleStatus(bookingToCancel.getVehicleId(), VEHICLE_STATUS_AVAILABLE);
            return Optional.of(bookingToCancel);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Deletes a booking by ID.
     *
     * @param command booking deletion command
     */
    @Override
    public void handle(DeleteBookingCommand command) {
        if (!bookingRepository.existsById(command.bookingId())) {
            throw new BookingNotFoundException(command.bookingId());
        }
        bookingRepository.deleteById(command.bookingId());
    }

    /**
     * Updates end date and total price of an existing booking.
     *
     * @param command booking update command
     * @return updated booking
     */
    @Override
    public Optional<Booking> handle(UpdateBookingCommand command) {
        var bookingOpt = bookingRepository.findById(command.bookingId());
        if (bookingOpt.isEmpty()) {
            throw new BookingNotFoundException(command.bookingId());
        }
        var bookingToUpdate = bookingOpt.get();
        bookingToUpdate.setEndDate(command.endDate());
        bookingToUpdate.setTotalPrice(command.totalPrice());
        bookingRepository.save(bookingToUpdate);
        return Optional.of(bookingToUpdate);
    }

    /**
     * Validates business rules for booking creation.
     *
     * @param command creation command
     * @param vehicleResource external vehicle data
     */
    private void validateBookingRequest(
            CreateBookingCommand command,
            VehicleResource vehicleResource) {
        if (!vehicleResource.ownerId().equals(command.ownerId())) {
            throw new OwnerMismatchException(command.vehicleId(), command.ownerId(), vehicleResource.ownerId());
        }
        if (command.startDate().after(command.endDate())) {
            throw new InvalidBookingDatesException(command.startDate(), command.endDate());
        }
        if (!VEHICLE_STATUS_AVAILABLE.equalsIgnoreCase(vehicleResource.status())) {
            throw new VehicleNotAvailableException(command.vehicleId(), vehicleResource.status());
        }
        boolean hasPendingOverlap = bookingRepository
                .existsOverlappingBookingByStatus(
                        command.vehicleId(),
                        BookingStatus.PENDING,
                        command.endDate(),
                        command.startDate()
                );
        boolean hasConfirmedOverlap = bookingRepository
                .existsOverlappingBookingByStatus(
                        command.vehicleId(),
                        BookingStatus.CONFIRMED,
                        command.endDate(),
                        command.startDate()
                );
        if (hasPendingOverlap || hasConfirmedOverlap) {
            throw new VehicleNotAvailableException(command.vehicleId(), "dates overlap");
        }
    }

    /**
     * Calculates total price based on daily rate and duration (minimum 1 day).
     *
     * @param pricePerDay the daily rental rate
     * @param startDate the booking start date
     * @param endDate the booking end date
     * @return the calculated total price
     */
    private Double calculateTotalPrice(Double pricePerDay, Date startDate, Date endDate) {
        long diffInMillis = Math.abs(endDate.getTime() - startDate.getTime());
        long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (days == 0) {
            days = 1;
        }
        return pricePerDay * days;
    }
}
