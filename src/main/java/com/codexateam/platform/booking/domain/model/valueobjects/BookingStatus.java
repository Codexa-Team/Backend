package com.codexateam.platform.booking.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value Object representing the status of a booking.
 * Encapsulates booking status validation and business rules.
 */
@Embeddable
public record BookingStatus(String status) {

    /** Initial status of a newly created booking */
    public static final String PENDING = "PENDING";
    /** Status when the booking was confirmed by the owner */
    public static final String CONFIRMED = "CONFIRMED";
    /** Status when the booking was rejected by the owner */
    public static final String REJECTED = "REJECTED";
    /** Status when the booking was canceled by the renter */
    public static final String CANCELED = "CANCELED";

    /**
     * Default constructor that validates the status value.
     * @param status The status value to encapsulate
     * @throws IllegalArgumentException if status is null or invalid
     */
    public BookingStatus {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Booking status cannot be null or empty");
        }
    }

    /**
     * Creates a PENDING status.
     * @return A new BookingStatus with PENDING value
     */
    public static BookingStatus createPending() {
        return new BookingStatus(PENDING);
    }

    public static BookingStatus createConfirmed() {
        return new BookingStatus(CONFIRMED);
    }

    public static BookingStatus createRejected() {
        return new BookingStatus(REJECTED);
    }

    public static BookingStatus createCanceled() {
        return new BookingStatus(CANCELED);
    }

    /**
     * Checks if the status is PENDING.
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return PENDING.equals(status);
    }

    /**
     * Checks if the status is CONFIRMED.
     * @return true if status is CONFIRMED
     */
    public boolean isConfirmed() {
        return CONFIRMED.equals(status);
    }

    /**
     * Checks if the status is REJECTED.
     * @return true if status is REJECTED
     */
    public boolean isRejected() {
        return REJECTED.equals(status);
    }

    /**
     * Checks if the status is CANCELED.
     * @return true if status is CANCELED
     */
    public boolean isCanceled() {
        return CANCELED.equals(status);
    }

    /**
     * Checks if the booking is in an active state (PENDING or CONFIRMED).
     * @return true if status is PENDING or CONFIRMED
     */
    public boolean isActive() {
        return isPending() || isConfirmed();
    }

    /**
     * Compares the internal value with the provided one, ignoring case.
     * @param other The value to compare with
     * @return true if both values are equal ignoring case
     */
    public boolean equalsIgnoreCase(String other) { return status.equalsIgnoreCase(other); }

    /**
     * Checks if the provided value represents an active state (PENDING or CONFIRMED).
     * @param value The value to check
     * @return true if the value represents an active state
     */
    public static boolean isActive(String value) { return PENDING.equalsIgnoreCase(value) || CONFIRMED.equalsIgnoreCase(value); }
}
