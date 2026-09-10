package com.codexateam.platform.listings.domain.services;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.queries.GetAllVehiclesQuery;
import com.codexateam.platform.listings.domain.model.queries.GetVehicleByIdQuery;
import com.codexateam.platform.listings.domain.model.queries.GetVehiclesByOwnerIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling Vehicle queries.
 */
public interface VehicleQueryService {
    /**
     * Handles the GetVehicleByIdQuery.
     * Retrieves a single vehicle by its unique identifier.
     *
     * @param query The query containing the vehicle ID.
     * @return An Optional containing the Vehicle if found, empty otherwise.
     */
    Optional<Vehicle> handle(GetVehicleByIdQuery query);

    /**
     * Handles the GetAllVehiclesQuery.
     * Retrieves all vehicle listings in the system.
     *
     * @param query The query (no parameters needed).
     * @return A list of all Vehicle aggregates.
     */
    List<Vehicle> handle(GetAllVehiclesQuery query);

    /**
     * Handles the GetVehiclesByOwnerIdQuery.
     * Retrieves all vehicles listed by a specific owner.
     *
     * @param query The query containing the owner's user ID.
     * @return A list of Vehicle aggregates owned by the specified user.
     */
    List<Vehicle> handle(GetVehiclesByOwnerIdQuery query);
}
