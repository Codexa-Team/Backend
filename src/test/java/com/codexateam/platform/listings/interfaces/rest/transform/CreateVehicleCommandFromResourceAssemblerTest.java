package com.codexateam.platform.listings.interfaces.rest.transform;

import com.codexateam.platform.listings.interfaces.rest.resources.CreateVehicleResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateVehicleCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should transform CreateVehicleResource into CreateVehicleCommand (AAA)")
    void toCommandFromResource_ShouldTransformSuccessfully() {
        // Arrange
        var resource = new CreateVehicleResource("Nissan", "Versa", 2021, 38.0);
        byte[] dummyImage = new byte[]{1, 2, 3};

        // Act
        var command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource, dummyImage, 7L);

        // Assert
        assertNotNull(command);
        assertEquals("Nissan", command.brand());
        assertEquals("Versa", command.model());
        assertEquals(2021, command.year());
        assertEquals(38.0, command.pricePerDay());
        assertArrayEquals(dummyImage, command.image());
        assertEquals(7L, command.ownerId());
    }
}
