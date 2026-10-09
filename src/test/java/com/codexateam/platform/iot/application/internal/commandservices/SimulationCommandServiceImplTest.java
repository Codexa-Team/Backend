package com.codexateam.platform.iot.application.internal.commandservices;

import com.codexateam.platform.iot.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.iot.domain.exceptions.VehicleNotFoundException;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;
import com.codexateam.platform.shared.domain.exceptions.UnauthorizedAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimulationCommandServiceImplTest {

    @Mock
    private ExternalListingsService externalListingsService;

    @InjectMocks
    private SimulationCommandServiceImpl simulationCommandService;

    @Test
    @DisplayName("startSimulation succeeds when vehicle exists and user is owner (AAA)")
    void startSimulation_WhenAuthorized_ShouldSucceed() {
        // Arrange
        var vehicle = new VehicleResource(10L, "Toyota", "Corolla", 2022, 50.0, "available", "img.jpg", 1L, new Date());
        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicle));
        when(externalListingsService.isVehicleOwner(10L, 1L)).thenReturn(true);

        // Act & Assert (no exception thrown)
        assertDoesNotThrow(() -> simulationCommandService.startSimulation(10L, 1L));
        verify(externalListingsService, times(1)).fetchVehicleById(10L);
        verify(externalListingsService, times(1)).isVehicleOwner(10L, 1L);
    }

    @Test
    @DisplayName("startSimulation throws VehicleNotFoundException when vehicle missing (AAA)")
    void startSimulation_WhenVehicleNotFound_ShouldThrowException() {
        // Arrange
        when(externalListingsService.fetchVehicleById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VehicleNotFoundException.class, () -> simulationCommandService.startSimulation(999L, 1L));
        verify(externalListingsService, times(1)).fetchVehicleById(999L);
        verify(externalListingsService, never()).isVehicleOwner(anyLong(), anyLong());
    }

    @Test
    @DisplayName("startSimulation throws UnauthorizedAccessException when user not owner (AAA)")
    void startSimulation_WhenNotOwner_ShouldThrowException() {
        // Arrange
        var vehicle = new VehicleResource(10L, "Toyota", "Corolla", 2022, 50.0, "available", "img.jpg", 1L, new Date());
        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicle));
        when(externalListingsService.isVehicleOwner(10L, 2L)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> simulationCommandService.startSimulation(10L, 2L));
        verify(externalListingsService, times(1)).isVehicleOwner(10L, 2L);
    }

    @Test
    @DisplayName("startCustomSimulation succeeds when vehicle exists and user is owner (AAA)")
    void startCustomSimulation_WhenAuthorized_ShouldSucceed() {
        // Arrange
        var vehicle = new VehicleResource(10L, "Toyota", "Corolla", 2022, 50.0, "available", "img.jpg", 1L, new Date());
        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicle));
        when(externalListingsService.isVehicleOwner(10L, 1L)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> simulationCommandService.startCustomSimulation(10L, 1L, -12.0, -77.0, -12.1, -77.1));
        verify(externalListingsService, times(1)).fetchVehicleById(10L);
        verify(externalListingsService, times(1)).isVehicleOwner(10L, 1L);
    }

    @Test
    @DisplayName("startCustomSimulation throws VehicleNotFoundException when vehicle missing (AAA)")
    void startCustomSimulation_WhenVehicleNotFound_ShouldThrowException() {
        // Arrange
        when(externalListingsService.fetchVehicleById(404L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VehicleNotFoundException.class, () -> simulationCommandService.startCustomSimulation(404L, 1L, -12.0, -77.0, -12.1, -77.1));
    }

    @Test
    @DisplayName("startCustomSimulation throws UnauthorizedAccessException when not owner (AAA)")
    void startCustomSimulation_WhenNotOwner_ShouldThrowException() {
        // Arrange
        var vehicle = new VehicleResource(10L, "Toyota", "Corolla", 2022, 50.0, "available", "img.jpg", 1L, new Date());
        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicle));
        when(externalListingsService.isVehicleOwner(10L, 5L)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> simulationCommandService.startCustomSimulation(10L, 5L, -12.0, -77.0, -12.1, -77.1));
    }
}
