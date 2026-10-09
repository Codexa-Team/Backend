package com.codexateam.platform.iot.domain.model.aggregates;

import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TelemetryTest {

    @Test
    @DisplayName("Telemetry constructor initializes all fields from RecordTelemetryCommand (AAA)")
    void constructor_FromCommand_ShouldInitializeFields() {
        // Arrange
        var command = new RecordTelemetryCommand(10L, -12.046374, -77.042793, 65.5, 80.0);

        // Act
        Telemetry telemetry = new Telemetry(command);

        // Assert
        assertEquals(10L, telemetry.getVehicleId());
        assertEquals(-12.046374, telemetry.getLatitude());
        assertEquals(-77.042793, telemetry.getLongitude());
        assertEquals(65.5, telemetry.getSpeed());
        assertEquals(80.0, telemetry.getFuelLevel());
    }

    @Test
    @DisplayName("Telemetry updateTelemetryData updates coordinates, speed, fuel and timestamp (AAA)")
    void updateTelemetryData_ShouldUpdateAllFields() {
        // Arrange
        var telemetry = new Telemetry(new RecordTelemetryCommand(10L, -12.0, -77.0, 50.0, 90.0));
        Date newDate = new Date();

        // Act
        telemetry.updateTelemetryData(-12.1, -77.1, 70.0, 85.0, newDate);

        // Assert
        assertEquals(-12.1, telemetry.getLatitude());
        assertEquals(-77.1, telemetry.getLongitude());
        assertEquals(70.0, telemetry.getSpeed());
        assertEquals(85.0, telemetry.getFuelLevel());
        assertEquals(newDate, telemetry.getCreatedAt());
    }

    @Test
    @DisplayName("Telemetry getters and setters work properly (AAA)")
    void gettersAndSetters_ShouldWorkProperly() {
        // Arrange
        Telemetry telemetry = new Telemetry();

        // Act
        telemetry.setVehicleId(15L);
        telemetry.setLatitude(-12.05);
        telemetry.setLongitude(-77.05);
        telemetry.setSpeed(45.0);
        telemetry.setFuelLevel(60.0);

        // Assert
        assertEquals(15L, telemetry.getVehicleId());
        assertEquals(-12.05, telemetry.getLatitude());
        assertEquals(-77.05, telemetry.getLongitude());
        assertEquals(45.0, telemetry.getSpeed());
        assertEquals(60.0, telemetry.getFuelLevel());
    }

    @Test
    @DisplayName("Telemetry default constructor creates non-null instance (AAA)")
    void defaultConstructor_ShouldInstantiate() {
        // Arrange & Act
        Telemetry telemetry = new Telemetry();

        // Assert
        assertNotNull(telemetry);
    }
}
