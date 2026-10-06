package com.codexateam.platform.listings.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleStatusTest {

    @Test
    @DisplayName("VehicleStatus constructs successfully for available (AAA)")
    void constructor_WhenAvailable_ShouldCreateInstance() {
        // Arrange & Act
        VehicleStatus status = new VehicleStatus("available");

        // Assert
        assertEquals("available", status.value());
        assertEquals("available", status.toString());
    }

    @Test
    @DisplayName("VehicleStatus constructs successfully for rented (AAA)")
    void constructor_WhenRented_ShouldCreateInstance() {
        // Arrange & Act
        VehicleStatus status = new VehicleStatus("rented");

        // Assert
        assertEquals("rented", status.value());
    }

    @Test
    @DisplayName("VehicleStatus constructs successfully for maintenance (AAA)")
    void constructor_WhenMaintenance_ShouldCreateInstance() {
        // Arrange & Act
        VehicleStatus status = new VehicleStatus("maintenance");

        // Assert
        assertEquals("maintenance", status.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"AVAILABLE", "Available", "RENTED", "Rented", "MAINTENANCE", "Maintenance"})
    @DisplayName("VehicleStatus normalizes status input to lowercase (AAA)")
    void constructor_WhenMixedCase_ShouldNormalizeToLowercase(String input) {
        // Arrange & Act
        VehicleStatus status = new VehicleStatus(input);

        // Assert
        assertEquals(input.toLowerCase(), status.value());
    }

    @Test
    @DisplayName("VehicleStatus throws IllegalArgumentException when value is null (AAA)")
    void constructor_WhenNull_ShouldThrowException() {
        // Arrange & Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new VehicleStatus(null));
        assertEquals("Vehicle status cannot be blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    @DisplayName("VehicleStatus throws IllegalArgumentException when value is blank (AAA)")
    void constructor_WhenBlank_ShouldThrowException(String blankInput) {
        // Arrange & Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new VehicleStatus(blankInput));
        assertEquals("Vehicle status cannot be blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sold", "destroyed", "reserved", "active", "pending"})
    @DisplayName("VehicleStatus throws IllegalArgumentException when value is not allowed (AAA)")
    void constructor_WhenNotAllowed_ShouldThrowException(String invalidInput) {
        // Arrange & Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new VehicleStatus(invalidInput));
        assertTrue(ex.getMessage().contains("Invalid vehicle status"));
    }

    @Test
    @DisplayName("VehicleStatus equals and hashCode contract holds for same values (AAA)")
    void equalsAndHashCode_WhenSameValue_ShouldBeEqual() {
        // Arrange
        VehicleStatus s1 = new VehicleStatus("available");
        VehicleStatus s2 = new VehicleStatus("AVAILABLE");
        VehicleStatus s3 = new VehicleStatus("rented");

        // Act & Assert
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3);
    }
}
