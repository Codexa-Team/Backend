package com.codexateam.platform.iot.infrastructure.persistence.jpa.repositories;

import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository interface for the Telemetry aggregate root.
 */
@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    /**
     * Finds all telemetry data for a specific vehicle.
     *
     * @param vehicleId The ID of the vehicle.
     * @param sort Sorting criteria (e.g., by timestamp).
     */
    List<Telemetry> findByVehicleId(Long vehicleId, Sort sort);

    /**
     * Finds all telemetry records for a specific vehicle.
     */
    List<Telemetry> findByVehicleId(Long vehicleId);

    /**
     * Deletes all telemetry records for a specific vehicle.
     *
     * @param vehicleId The ID of the vehicle.
     */
    void deleteByVehicleId(Long vehicleId);

    /**
     * Finds the latest telemetry record for a specific vehicle ordered by createdAt descending.
     */
    Optional<Telemetry> findFirstByVehicleIdOrderByCreatedAtDesc(Long vehicleId);
}
