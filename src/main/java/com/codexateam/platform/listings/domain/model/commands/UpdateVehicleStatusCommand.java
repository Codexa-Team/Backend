package com.codexateam.platform.listings.domain.model.commands;

/**
 * Command to update a vehicle's rental status.
 * Used by the Booking context to mark vehicles as "available" or "rented".
 *
 * @param vehicleId The ID of the vehicle to update.
 * @param status The new status (e.g., "available", "rented").
 */
public record UpdateVehicleStatusCommand(Long vehicleId, String status) {
}

