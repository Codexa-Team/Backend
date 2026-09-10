package com.codexateam.platform.listings.domain.model.queries;

/**
 * Query to retrieve all vehicles listed by a specific owner.
 *
 * @param ownerId The ID of the owner (user with ROLE_ARRENDADOR).
 */
public record GetVehiclesByOwnerIdQuery(Long ownerId) {
}
