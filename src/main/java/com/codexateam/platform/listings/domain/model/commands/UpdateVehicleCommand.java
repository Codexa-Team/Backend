package com.codexateam.platform.listings.domain.model.commands;

import java.util.Arrays;
import java.util.Objects;

/**
 * Command to update an existing vehicle listing.
 * Contains the vehicle ID and all fields that can be modified.
 *
 * @param vehicleId The ID of the vehicle to update.
 * @param brand The updated brand of the vehicle.
 * @param model The updated model of the vehicle.
 * @param year The updated year of the vehicle.
 * @param pricePerDay The updated price per day for renting the vehicle.
 * @param image The updated image data as byte array.
 */
public record UpdateVehicleCommand(
        Long vehicleId,
        String brand,
        String model,
        Integer year,
        Double pricePerDay,
        byte[] image
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateVehicleCommand that)) return false;
        return Objects.equals(vehicleId, that.vehicleId)
                && Objects.equals(brand, that.brand)
                && Objects.equals(model, that.model)
                && Objects.equals(year, that.year)
                && Objects.equals(pricePerDay, that.pricePerDay)
                && Arrays.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vehicleId, brand, model, year, pricePerDay);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "UpdateVehicleCommand[vehicleId=" + vehicleId + ", brand=" + brand + ", model=" + model
                + ", year=" + year + ", pricePerDay=" + pricePerDay + ", image=" + Arrays.toString(image) + "]";
    }
}
