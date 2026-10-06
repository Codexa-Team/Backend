package com.codexateam.platform.iot.application.internal.commandservices;

import com.codexateam.platform.iot.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.iot.domain.exceptions.VehicleNotFoundException;
import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import com.codexateam.platform.iot.infrastructure.persistence.jpa.repositories.TelemetryRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelemetryCommandServiceImplTest {

    @Mock
    private TelemetryRepository telemetryRepository;

    @Mock
    private ExternalListingsService externalListingsService;

    @InjectMocks
    private TelemetryCommandServiceImpl telemetryCommandService;

    @Test
    @DisplayName("handle(RecordTelemetryCommand) should save telemetry when authorized owner (AAA)")
    void handle_RecordTelemetryCommand_ShouldSaveTelemetry_WhenAuthorized() {
        // Arrange
        var command = new RecordTelemetryCommand(10L, -12.046374, -77.042793, 65.5, 80.0);
        var vehicleResource = new VehicleResource(10L, "Toyota", "Corolla", 2022, 45.0, "available", "corolla.jpg", 1L, new Date());

        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicleResource));
        when(externalListingsService.isVehicleOwner(10L, 1L)).thenReturn(true);
        when(telemetryRepository.save(any(Telemetry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Telemetry result = telemetryCommandService.handle(command, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getVehicleId());
        assertEquals(-12.046374, result.getLatitude());
        assertEquals(-77.042793, result.getLongitude());
        assertEquals(65.5, result.getSpeed());

        verify(externalListingsService, times(1)).fetchVehicleById(10L);
        verify(externalListingsService, times(1)).isVehicleOwner(10L, 1L);
        verify(telemetryRepository, times(1)).save(any(Telemetry.class));
    }

    @Test
    @DisplayName("handle(RecordTelemetryCommand) should throw VehicleNotFoundException when vehicle does not exist (AAA)")
    void handle_RecordTelemetryCommand_ShouldThrowException_WhenVehicleNotFound() {
        // Arrange
        var command = new RecordTelemetryCommand(999L, -12.0, -77.0, 50.0, 70.0);
        when(externalListingsService.fetchVehicleById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VehicleNotFoundException.class, () -> telemetryCommandService.handle(command, 1L));
        verify(externalListingsService, times(1)).fetchVehicleById(999L);
        verify(telemetryRepository, never()).save(any(Telemetry.class));
    }

    @Test
    @DisplayName("handle(RecordTelemetryCommand) should throw UnauthorizedAccessException when user is not owner (AAA)")
    void handle_RecordTelemetryCommand_ShouldThrowException_WhenNotOwner() {
        // Arrange
        var command = new RecordTelemetryCommand(10L, -12.0, -77.0, 50.0, 70.0);
        var vehicleResource = new VehicleResource(10L, "Toyota", "Corolla", 2022, 45.0, "available", "corolla.jpg", 1L, new Date());

        when(externalListingsService.fetchVehicleById(10L)).thenReturn(Optional.of(vehicleResource));
        when(externalListingsService.isVehicleOwner(10L, 2L)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> telemetryCommandService.handle(command, 2L));
        verify(externalListingsService, times(1)).fetchVehicleById(10L);
        verify(externalListingsService, times(1)).isVehicleOwner(10L, 2L);
        verify(telemetryRepository, never()).save(any(Telemetry.class));
    }
}
