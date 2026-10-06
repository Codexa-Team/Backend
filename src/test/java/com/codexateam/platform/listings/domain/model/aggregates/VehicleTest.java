package com.codexateam.platform.listings.domain.model.aggregates;

import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.domain.model.commands.UpdateVehicleCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {

    @Test
    @DisplayName("Vehicle constructor from CreateVehicleCommand sets fields and default status available (AAA)")
    void constructor_FromCommand_ShouldInitializeFields() {
        // Arrange
        byte[] image = new byte[]{1, 2, 3};
        var command = new CreateVehicleCommand("Toyota", "Corolla", 2023, 50.0, image, 1L);

        // Act
        Vehicle vehicle = new Vehicle(command);

        // Assert
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2023, vehicle.getYear());
        assertEquals(50.0, vehicle.getPricePerDay());
        assertEquals(1L, vehicle.getOwnerId());
        assertEquals("available", vehicle.getStatus());
        assertArrayEquals(image, vehicle.getImage());
    }

    @Test
    @DisplayName("Vehicle updateStatus modifies status correctly (AAA)")
    void updateStatus_ShouldChangeStatus() {
        // Arrange
        Vehicle vehicle = new Vehicle(new CreateVehicleCommand("Honda", "Civic", 2022, 55.0, new byte[]{1}, 1L));

        // Act
        vehicle.updateStatus("rented");

        // Assert
        assertEquals("rented", vehicle.getStatus());
    }

    @Test
    @DisplayName("Vehicle update with new image replaces existing image (AAA)")
    void update_WithNewImage_ShouldReplaceImage() {
        // Arrange
        byte[] oldImage = new byte[]{1, 1, 1};
        byte[] newImage = new byte[]{2, 2, 2};
        Vehicle vehicle = new Vehicle(new CreateVehicleCommand("Nissan", "Versa", 2021, 40.0, oldImage, 1L));
        var updateCommand = new UpdateVehicleCommand(1L, "Nissan", "Versa SV", 2022, 45.0, newImage);

        // Act
        vehicle.update(updateCommand);

        // Assert
        assertEquals("Versa SV", vehicle.getModel());
        assertEquals(2022, vehicle.getYear());
        assertEquals(45.0, vehicle.getPricePerDay());
        assertArrayEquals(newImage, vehicle.getImage());
    }

    @Test
    @DisplayName("Vehicle update with null image retains original image (AAA)")
    void update_WithNullImage_ShouldRetainOriginalImage() {
        // Arrange
        byte[] originalImage = new byte[]{5, 5, 5};
        Vehicle vehicle = new Vehicle(new CreateVehicleCommand("Mazda", "3", 2020, 48.0, originalImage, 1L));
        var updateCommand = new UpdateVehicleCommand(1L, "Mazda", "3 Touring", 2021, 52.0, null);

        // Act
        vehicle.update(updateCommand);

        // Assert
        assertEquals("3 Touring", vehicle.getModel());
        assertArrayEquals(originalImage, vehicle.getImage());
    }

    @Test
    @DisplayName("Vehicle update with empty image array retains original image (AAA)")
    void update_WithEmptyImage_ShouldRetainOriginalImage() {
        // Arrange
        byte[] originalImage = new byte[]{7, 7, 7};
        Vehicle vehicle = new Vehicle(new CreateVehicleCommand("Kia", "Cerato", 2021, 42.0, originalImage, 1L));
        var updateCommand = new UpdateVehicleCommand(1L, "Kia", "Cerato GT", 2022, 49.0, new byte[]{});

        // Act
        vehicle.update(updateCommand);

        // Assert
        assertEquals("Cerato GT", vehicle.getModel());
        assertArrayEquals(originalImage, vehicle.getImage());
    }

    @Test
    @DisplayName("Vehicle getStatus returns null when status is not initialized (AAA)")
    void getStatus_WhenStatusNull_ShouldReturnNull() {
        // Arrange & Act
        Vehicle vehicle = new Vehicle();

        // Assert
        assertNull(vehicle.getStatus());
    }
}
