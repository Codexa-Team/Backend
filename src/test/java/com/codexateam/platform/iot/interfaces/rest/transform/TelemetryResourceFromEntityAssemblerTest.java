package com.codexateam.platform.iot.interfaces.rest.transform;

import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import com.codexateam.platform.iot.interfaces.rest.resources.TelemetryResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TelemetryResourceFromEntityAssemblerTest {

    @Test
    @DisplayName("toResourceFromEntity maps Telemetry aggregate to TelemetryResource (AAA)")
    void toResourceFromEntity_ShouldMapCorrectly() {
        // Arrange
        var command = new RecordTelemetryCommand(10L, -12.046374, -77.042793, 65.5, 80.0);
        var telemetry = new Telemetry(command);

        // Act
        TelemetryResource resource = TelemetryResourceFromEntityAssembler.toResourceFromEntity(telemetry);

        // Assert
        assertNotNull(resource);
        assertEquals(10L, resource.getVehicleId());
        assertEquals(-12.046374, resource.getLatitude());
        assertEquals(-77.042793, resource.getLongitude());
        assertEquals(65.5, resource.getSpeed());
        assertEquals(80.0, resource.getFuelLevel());
    }

    @Test
    @DisplayName("toResourceFromEntity with plannedRoute maps coordinates list (AAA)")
    void toResourceFromEntity_WithPlannedRoute_ShouldMapCorrectly() {
        // Arrange
        var command = new RecordTelemetryCommand(10L, -12.0, -77.0, 50.0, 75.0);
        var telemetry = new Telemetry(command);
        List<List<Double>> plannedRoute = List.of(List.of(-12.0, -77.0), List.of(-12.1, -77.1));

        // Act
        TelemetryResource resource = TelemetryResourceFromEntityAssembler.toResourceFromEntity(telemetry, plannedRoute);

        // Assert
        assertNotNull(resource);
        assertEquals(10L, resource.getVehicleId());
        assertEquals(plannedRoute, resource.getPlannedRoute());
    }
}
