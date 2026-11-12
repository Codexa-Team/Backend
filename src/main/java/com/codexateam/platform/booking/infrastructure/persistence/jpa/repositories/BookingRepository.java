package com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories;

import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository interface for the Booking aggregate root.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Finds all bookings made by a specific renter.
     *
     * @param renterId The ID of the renter (Arrendatario).
     */
    List<Booking> findByRenterId(Long renterId);

    /**
     * Finds all bookings associated with a specific owner's vehicles.
     *
     * @param ownerId The ID of the owner (Arrendador).
     */
    List<Booking> findByOwnerId(Long ownerId);

    /**
     * Finds a booking for a vehicle that includes the given timestamp.
     *
     * @param vehicleId The ID of the vehicle
     * @param start The start date boundary
     * @param end The end date boundary
     * @return An optional booking that matches the criteria
     */
    Optional<Booking> findFirstByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long vehicleId, Date start, Date end);

    /**
     * Checks if there exists an overlapping booking for a vehicle with active status.
     * A booking overlaps if it has status PENDING or CONFIRMED and the date ranges intersect.
     *
     * @param vehicleId The ID of the vehicle
     * @param bookingStatus The status value to check (should be PENDING or CONFIRMED)
     * @param endDate The end date of the new booking
     * @param startDate The start date of the new booking
     * @return true if there's an overlapping booking with the given status
     */
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.vehicleId = :vehicleId
              AND b.bookingStatus.status = :bookingStatus
              AND b.startDate < :endDate
              AND b.endDate > :startDate
            """)
    boolean existsOverlappingBookingByStatus(
            @Param("vehicleId") Long vehicleId,
            @Param("bookingStatus") String bookingStatus,
            @Param("endDate") Date endDate,
            @Param("startDate") Date startDate);

    /**
     * Finds all bookings for a vehicle with a specific status within a date range.
     * Used to check for overlapping bookings with active statuses.
     *
     * @param vehicleId The ID of the vehicle
     * @param status The booking status to filter by
     * @param endDate The end date boundary
     * @param startDate The start date boundary
     * @return List of bookings matching the criteria
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.vehicleId = :vehicleId
              AND b.bookingStatus.status = :status
              AND b.startDate < :endDate
              AND b.endDate > :startDate
            """)
    List<Booking> findOverlappingBookingsByStatus(
            @Param("vehicleId") Long vehicleId,
            @Param("status") String status,
            @Param("endDate") Date endDate,
            @Param("startDate") Date startDate);

    /**
     * Deletes all bookings by vehicleId.
     * Used for cascade cleanup when deleting a vehicle.
     *
     * @param vehicleId The ID of the vehicle
     */
    void deleteByVehicleId(Long vehicleId);

    /**
     * Finds all active bookings where status is 'CONFIRMED' and current time is between startDate and endDate.
     * This is used for automatic telemetry generation for active trips.
     *
     * @param status The status value to filter by (should be 'CONFIRMED')
     * @param currentTimestamp The current timestamp to check if booking is active
     * @param currentTimestamp2 The upper timestamp bound for the interval
     * @return List of active confirmed bookings
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.bookingStatus.status = :status
              AND b.startDate <= :currentTimestamp
              AND b.endDate >= :currentTimestamp2
            """)
    List<Booking> findActiveBookingsByStatus(
            @Param("status") String status,
            @Param("currentTimestamp") Date currentTimestamp,
            @Param("currentTimestamp2") Date currentTimestamp2);

    /**
     * Finds a confirmed booking for a vehicle that includes the given timestamp.
     *
     * @param vehicleId The ID of the vehicle
     * @param status The status value of the booking (should be 'CONFIRMED')
     * @param startBoundary The start date boundary
     * @param endBoundary The end date boundary
     * @return An optional booking that matches the criteria
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.vehicleId = :vehicleId
              AND b.bookingStatus.status = :status
              AND b.startDate <= :startBoundary
              AND b.endDate >= :endBoundary
            ORDER BY b.id ASC
            LIMIT 1
            """)
    Optional<Booking> findFirstActiveBookingByVehicleAndStatus(
            @Param("vehicleId") Long vehicleId,
            @Param("status") String status,
            @Param("startBoundary") Date startBoundary,
            @Param("endBoundary") Date endBoundary);
}
