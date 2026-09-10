package com.codexateam.platform.listings.domain.model.queries;

/**
 * Query to retrieve a specific vehicle by its unique identifier.
 *
 * @param vehicleId The ID of the vehicle to retrieve.
 */
public record GetVehicleByIdQuery(Long vehicleId) {
}
