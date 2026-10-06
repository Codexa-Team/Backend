package com.codexateam.platform.listings.application.internal.queryservices;

import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.domain.model.queries.GetAllVehiclesQuery;
import com.codexateam.platform.listings.domain.model.queries.GetVehicleByIdQuery;
import com.codexateam.platform.listings.domain.model.queries.GetVehiclesByOwnerIdQuery;
import com.codexateam.platform.listings.infrastructure.persistence.jpa.repositories.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleQueryServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleQueryServiceImpl vehicleQueryService;

    @Test
    @DisplayName("handle(GetVehicleByIdQuery) should return vehicle when exists (AAA)")
    void handle_GetVehicleByIdQuery_ShouldReturnVehicle_WhenExists() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var vehicle = new Vehicle(new CreateVehicleCommand("Hyundai", "Elantra", 2021, 48.0, imageBytes, 2L));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        // Act
        Optional<Vehicle> result = vehicleQueryService.handle(new GetVehicleByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Hyundai", result.get().getBrand());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("handle(GetVehicleByIdQuery) should return empty Optional when not found (AAA)")
    void handle_GetVehicleByIdQuery_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Vehicle> result = vehicleQueryService.handle(new GetVehicleByIdQuery(999L));

        // Assert
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("handle(GetAllVehiclesQuery) should return all vehicles (AAA)")
    void handle_GetAllVehiclesQuery_ShouldReturnAllVehicles() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var v1 = new Vehicle(new CreateVehicleCommand("Kia", "Rio", 2020, 35.0, imageBytes, 1L));
        var v2 = new Vehicle(new CreateVehicleCommand("Ford", "Focus", 2022, 50.0, imageBytes, 1L));
        when(vehicleRepository.findAll()).thenReturn(List.of(v1, v2));

        // Act
        List<Vehicle> result = vehicleQueryService.handle(new GetAllVehiclesQuery());

        // Assert
        assertEquals(2, result.size());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("handle(GetVehiclesByOwnerIdQuery) should return owner's vehicles (AAA)")
    void handle_GetVehiclesByOwnerIdQuery_ShouldReturnOwnersVehicles() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var v1 = new Vehicle(new CreateVehicleCommand("BMW", "Series 3", 2023, 90.0, imageBytes, 5L));
        when(vehicleRepository.findByOwnerId(5L)).thenReturn(List.of(v1));

        // Act
        List<Vehicle> result = vehicleQueryService.handle(new GetVehiclesByOwnerIdQuery(5L));

        // Assert
        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).getBrand());
        verify(vehicleRepository, times(1)).findByOwnerId(5L);
    }

    @Test
    @DisplayName("handle(GetAllVehiclesQuery) should return empty list when no vehicles exist (AAA)")
    void handle_GetAllVehiclesQuery_ShouldReturnEmptyList_WhenNoVehicles() {
        // Arrange
        when(vehicleRepository.findAll()).thenReturn(List.of());

        // Act
        List<Vehicle> result = vehicleQueryService.handle(new GetAllVehiclesQuery());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("handle(GetVehiclesByOwnerIdQuery) should return empty list when owner has no vehicles (AAA)")
    void handle_GetVehiclesByOwnerIdQuery_ShouldReturnEmptyList_WhenOwnerHasNoVehicles() {
        // Arrange
        when(vehicleRepository.findByOwnerId(999L)).thenReturn(List.of());

        // Act
        List<Vehicle> result = vehicleQueryService.handle(new GetVehiclesByOwnerIdQuery(999L));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).findByOwnerId(999L);
    }
}
