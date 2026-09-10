package com.codexateam.platform.listings.interfaces.rest.resources;

import java.util.Date;

/**
 * DTO for returning vehicle data to API consumers.
 *
 * @param id The vehicle's unique identifier.
 * @param brand The vehicle's brand/manufacturer.
 * @param model The vehicle's model name.
 * @param year The vehicle's manufacturing year.
 * @param pricePerDay The daily rental price.
 * @param status The rental status (e.g., "available", "rented").
 * @param imageUrl URL of the vehicle's image.
 * @param ownerId The ID of the vehicle's owner.
 * @param createdAt The timestamp when the vehicle was listed.
 */
public record VehicleResource(
        Long id,
        String brand,
        String model,
        Integer year,
        Double pricePerDay,
        String status,
        String imageUrl,
        Long ownerId,
        Date createdAt
) {
}
