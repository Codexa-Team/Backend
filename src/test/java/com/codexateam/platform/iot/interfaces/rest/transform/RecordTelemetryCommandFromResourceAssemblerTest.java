package com.codexateam.platform.iot.interfaces.rest.transform;

import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import com.codexateam.platform.iot.interfaces.rest.resources.RecordTelemetryResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecordTelemetryCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should transform RecordTelemetryResource into RecordTelemetryCommand (AAA)")
    void toCommandFromResource_ShouldTransformSuccessfully() {
        // Arrange
        var resource = new RecordTelemetryResource(10L, -12.0463, -77.0427, 72.0, 85.0);

        // Act
        var command = RecordTelemetryCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertNotNull(command);
        assertEquals(10L, command.vehicleId());
        assertEquals(-12.0463, command.latitude());
        assertEquals(-77.0427, command.longitude());
        assertEquals(72.0, command.speed());
        assertEquals(85.0, command.fuelLevel());
    }

    @Test
    @DisplayName("TelemetryResourceFromEntityAssembler should transform Telemetry aggregate into TelemetryResource (AAA)")
    void toResourceFromEntity_ShouldTransformSuccessfully() {
        // Arrange
        var command = new RecordTelemetryCommand(10L, -12.0463, -77.0427, 72.0, 85.0);
        var telemetry = new Telemetry(command);

        // Act
        var resource = TelemetryResourceFromEntityAssembler.toResourceFromEntity(telemetry);

        // Assert
        assertNotNull(resource);
        assertEquals(10L, resource.getVehicleId());
        assertEquals(-12.0463, resource.getLatitude());
        assertEquals(-77.0427, resource.getLongitude());
        assertEquals(72.0, resource.getSpeed());
        assertEquals(85.0, resource.getFuelLevel());
    }
}
