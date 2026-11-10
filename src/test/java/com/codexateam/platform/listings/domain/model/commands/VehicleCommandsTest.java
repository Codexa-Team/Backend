package com.codexateam.platform.listings.domain.model.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleCommandsTest {

    @Test
    @DisplayName("CreateVehicleCommand equals, hashCode and toString contract with byte array")
    void createVehicleCommand_Contract() {
        byte[] img1 = new byte[]{1, 2, 3};
        byte[] img2 = new byte[]{1, 2, 3};
        byte[] img3 = new byte[]{4, 5, 6};

        var cmd1 = new CreateVehicleCommand("Toyota", "Corolla", 2022, 50.0, img1, 1L);
        var cmd2 = new CreateVehicleCommand("Toyota", "Corolla", 2022, 50.0, img2, 1L);
        var cmd3 = new CreateVehicleCommand("Toyota", "Corolla", 2022, 50.0, img3, 1L);

        assertEquals(cmd1, cmd2);
        assertEquals(cmd1.hashCode(), cmd2.hashCode());
        assertNotEquals(cmd1, cmd3);
        assertTrue(cmd1.toString().contains("Toyota"));
    }

    @Test
    @DisplayName("UpdateVehicleCommand equals, hashCode and toString contract with byte array")
    void updateVehicleCommand_Contract() {
        byte[] img1 = new byte[]{1, 2, 3};
        byte[] img2 = new byte[]{1, 2, 3};
        byte[] img3 = new byte[]{4, 5, 6};

        var cmd1 = new UpdateVehicleCommand(10L, "Toyota", "Corolla", 2022, 50.0, img1);
        var cmd2 = new UpdateVehicleCommand(10L, "Toyota", "Corolla", 2022, 50.0, img2);
        var cmd3 = new UpdateVehicleCommand(10L, "Toyota", "Corolla", 2022, 50.0, img3);

        assertEquals(cmd1, cmd2);
        assertEquals(cmd1.hashCode(), cmd2.hashCode());
        assertNotEquals(cmd1, cmd3);
        assertTrue(cmd1.toString().contains("Toyota"));
    }
}
