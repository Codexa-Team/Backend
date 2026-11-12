package com.codexateam.platform.listings.domain.model.commands;

import java.util.Arrays;
import java.util.Objects;

/**
 * Command to create a new vehicle listing.
 *
 * @param brand The vehicle's brand/manufacturer.
 * @param model The vehicle's model name.
 * @param year The vehicle's manufacturing year.
 * @param pricePerDay The daily rental price.
 * @param image The image data as byte array.
 * @param ownerId The ID of the owner (must have ROLE_ARRENDADOR).
 */
public record CreateVehicleCommand(
        String brand,
        String model,
        Integer year,
        Double pricePerDay,
        byte[] image,
        Long ownerId
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateVehicleCommand that)) {
            return false;
        }
        return Objects.equals(brand, that.brand)
                && Objects.equals(model, that.model)
                && Objects.equals(year, that.year)
                && Objects.equals(pricePerDay, that.pricePerDay)
                && Arrays.equals(image, that.image)
                && Objects.equals(ownerId, that.ownerId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(brand, model, year, pricePerDay, ownerId);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "CreateVehicleCommand[brand=" + brand + ", model=" + model + ", year=" + year
                + ", pricePerDay=" + pricePerDay + ", image=" + Arrays.toString(image) + ", ownerId=" + ownerId + "]";
    }
}
