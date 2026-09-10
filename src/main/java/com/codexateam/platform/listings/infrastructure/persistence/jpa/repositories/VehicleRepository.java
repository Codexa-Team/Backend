package com.codexateam.platform.listings.infrastructure.persistence.jpa.repositories;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository interface for the Vehicle aggregate root.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    /**
     * Finds all vehicles listed by a specific owner.
     * @param ownerId The ID of the owner (User).
     * @return A list of vehicles.
     */
    List<Vehicle> findByOwnerId(Long ownerId);
}
