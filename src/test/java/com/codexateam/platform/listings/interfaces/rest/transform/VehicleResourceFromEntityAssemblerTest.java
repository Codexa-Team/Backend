package com.codexateam.platform.listings.interfaces.rest.transform;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleResourceFromEntityAssemblerTest {

    private VehicleResourceFromEntityAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new VehicleResourceFromEntityAssembler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("toResourceFromEntity transforms Vehicle to VehicleResource with dynamic image URL (AAA)")
    void toResourceFromEntity_ShouldTransformCorrectly() {
        // Arrange
        byte[] image = new byte[]{1, 2, 3};
        var command = new CreateVehicleCommand("Toyota", "Corolla", 2022, 45.0, image, 1L);
        Vehicle vehicle = new Vehicle(command);

        // Act
        VehicleResource resource = assembler.toResourceFromEntity(vehicle);

        // Assert
        assertNotNull(resource);
        assertEquals("Toyota", resource.brand());
        assertEquals("Corolla", resource.model());
        assertEquals(2022, resource.year());
        assertEquals(45.0, resource.pricePerDay());
        assertEquals("available", resource.status());
        assertEquals(1L, resource.ownerId());
        assertNotNull(resource.imageUrl());
        assertTrue(resource.imageUrl().contains("/api/v1/vehicles/"));
    }

    @Test
    @DisplayName("toResourceFromEntity handles rented status properly (AAA)")
    void toResourceFromEntity_WhenRentedStatus_ShouldTransformCorrectly() {
        // Arrange
        var command = new CreateVehicleCommand("Mazda", "3", 2021, 50.0, new byte[]{1}, 2L);
        Vehicle vehicle = new Vehicle(command);
        vehicle.updateStatus("rented");

        // Act
        VehicleResource resource = assembler.toResourceFromEntity(vehicle);

        // Assert
        assertNotNull(resource);
        assertEquals("rented", resource.status());
        assertEquals("Mazda", resource.brand());
    }
}
