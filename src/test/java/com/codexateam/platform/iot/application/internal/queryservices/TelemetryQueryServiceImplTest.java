package com.codexateam.platform.iot.application.internal.queryservices;

import com.codexateam.platform.iot.application.internal.outboundservices.acl.ExternalBookingService;
import com.codexateam.platform.iot.application.internal.outboundservices.acl.ExternalListingsService;
import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import com.codexateam.platform.iot.domain.model.queries.GetLatestTelemetryQuery;
import com.codexateam.platform.iot.domain.model.queries.GetTelemetryByVehicleIdQuery;
import com.codexateam.platform.iot.infrastructure.persistence.jpa.repositories.TelemetryRepository;
import com.codexateam.platform.shared.domain.exceptions.UnauthorizedAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelemetryQueryServiceImplTest {

    @Mock
    private TelemetryRepository telemetryRepository;

    @Mock
    private ExternalListingsService externalListingsService;

    @Mock
    private ExternalBookingService externalBookingService;

    @InjectMocks
    private TelemetryQueryServiceImpl telemetryQueryService;

    @Test
    @DisplayName("handle(GetLatestTelemetryQuery) should return latest telemetry when user is owner (AAA)")
    void handle_GetLatestTelemetryQuery_ShouldReturnTelemetry_WhenOwner() {
        // Arrange
        var telemetry = new Telemetry(new RecordTelemetryCommand(10L, -12.0, -77.0, 60.0, 75.0));
        when(externalListingsService.isVehicleOwner(10L, 1L)).thenReturn(true);
        when(telemetryRepository.findFirstByVehicleIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.of(telemetry));

        // Act
        Optional<Telemetry> result = telemetryQueryService.handle(new GetLatestTelemetryQuery(10L), 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getVehicleId());
        assertEquals(60.0, result.get().getSpeed());
        verify(telemetryRepository, times(1)).findFirstByVehicleIdOrderByCreatedAtDesc(10L);
    }

    @Test
    @DisplayName("handle(GetLatestTelemetryQuery) should return latest telemetry when user has active booking (AAA)")
    void handle_GetLatestTelemetryQuery_ShouldReturnTelemetry_WhenRenterActiveBooking() {
        // Arrange
        var telemetry = new Telemetry(new RecordTelemetryCommand(10L, -12.0, -77.0, 60.0, 75.0));
        when(externalListingsService.isVehicleOwner(10L, 2L)).thenReturn(false);
        when(externalBookingService.hasTrackingPermission(2L, 10L)).thenReturn(true);
        when(telemetryRepository.findFirstByVehicleIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.of(telemetry));

        // Act
        Optional<Telemetry> result = telemetryQueryService.handle(new GetLatestTelemetryQuery(10L), 2L);

        // Assert
        assertTrue(result.isPresent());
        verify(telemetryRepository, times(1)).findFirstByVehicleIdOrderByCreatedAtDesc(10L);
    }

    @Test
    @DisplayName("handle(GetLatestTelemetryQuery) should throw UnauthorizedAccessException when neither owner nor active booking (AAA)")
    void handle_GetLatestTelemetryQuery_ShouldThrowException_WhenUnauthorized() {
        // Arrange
        when(externalListingsService.isVehicleOwner(10L, 5L)).thenReturn(false);
        when(externalBookingService.hasTrackingPermission(5L, 10L)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> telemetryQueryService.handle(new GetLatestTelemetryQuery(10L), 5L));
        verify(telemetryRepository, never()).findFirstByVehicleIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    @DisplayName("handle(GetTelemetryByVehicleIdQuery) should return list of telemetries (AAA)")
    void handle_GetTelemetryByVehicleIdQuery_ShouldReturnList() {
        // Arrange
        var t1 = new Telemetry(new RecordTelemetryCommand(10L, -12.0, -77.0, 50.0, 80.0));
        when(externalListingsService.isVehicleOwner(10L, 1L)).thenReturn(true);
        when(telemetryRepository.findByVehicleId(eq(10L), any(Sort.class))).thenReturn(List.of(t1));

        // Act
        List<Telemetry> result = telemetryQueryService.handle(new GetTelemetryByVehicleIdQuery(10L), 1L);

        // Assert
        assertEquals(1, result.size());
        verify(telemetryRepository, times(1)).findByVehicleId(eq(10L), any(Sort.class));
    }
}
