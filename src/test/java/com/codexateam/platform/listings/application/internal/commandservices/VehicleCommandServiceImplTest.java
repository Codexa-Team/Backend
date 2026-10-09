package com.codexateam.platform.listings.application.internal.commandservices;

import com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.codexateam.platform.iot.infrastructure.persistence.jpa.repositories.TelemetryRepository;
import com.codexateam.platform.listings.application.internal.outboundservices.acl.ExternalIamService;
import com.codexateam.platform.listings.domain.exceptions.OwnerNotFoundException;
import com.codexateam.platform.listings.domain.exceptions.VehicleNotFoundException;
import com.codexateam.platform.listings.domain.model.aggregates.Vehicle;
import com.codexateam.platform.listings.domain.model.commands.CreateVehicleCommand;
import com.codexateam.platform.listings.domain.model.commands.DeleteVehicleCommand;
import com.codexateam.platform.listings.domain.model.commands.UpdateVehicleCommand;
import com.codexateam.platform.listings.domain.model.commands.UpdateVehicleStatusCommand;
import com.codexateam.platform.listings.infrastructure.persistence.jpa.repositories.VehicleRepository;
import com.codexateam.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleCommandServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ExternalIamService externalIamService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private TelemetryRepository telemetryRepository;

    @InjectMocks
    private VehicleCommandServiceImpl vehicleCommandService;

    // -------------------------------------------------------------------------
    // handle(CreateVehicleCommand command) - CREATE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(CreateVehicleCommand) should save vehicle when owner is verified via ACL (AAA)")
    void handle_CreateVehicleCommand_ShouldSaveVehicle_WhenOwnerIsValid() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var command = new CreateVehicleCommand("Toyota", "Corolla", 2022, 45.0, imageBytes, 1L);
        when(externalIamService.isOwner(1L)).thenReturn(true);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent(), "Expected vehicle to be created successfully");
        assertEquals("Toyota", result.get().getBrand());
        assertEquals("Corolla", result.get().getModel());
        assertEquals(2022, result.get().getYear());
        assertEquals(45.0, result.get().getPricePerDay());

        verify(externalIamService, times(1)).isOwner(1L);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("handle(CreateVehicleCommand) should throw OwnerNotFoundException when owner is not valid (AAA)")
    void handle_CreateVehicleCommand_ShouldThrowException_WhenOwnerInvalid() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var command = new CreateVehicleCommand("Honda", "Civic", 2021, 50.0, imageBytes, 99L);
        when(externalIamService.isOwner(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(OwnerNotFoundException.class, () -> vehicleCommandService.handle(command));

        verify(externalIamService, times(1)).isOwner(99L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    // -------------------------------------------------------------------------
    // handle(UpdateVehicleStatusCommand command) - UPDATE STATUS
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(UpdateVehicleStatusCommand) should update status when vehicle exists (AAA)")
    void handle_UpdateVehicleStatusCommand_ShouldUpdateStatus_WhenVehicleExists() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var vehicle = new Vehicle(new CreateVehicleCommand("Nissan", "Sentra", 2020, 40.0, imageBytes, 1L));
        var command = new UpdateVehicleStatusCommand(10L, "rented");

        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("rented", result.get().getStatus());
        verify(vehicleRepository, times(1)).findById(10L);
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    // -------------------------------------------------------------------------
    // handle(UpdateVehicleCommand command) - UPDATE DETAILS
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(UpdateVehicleCommand) should update details when vehicle exists (AAA)")
    void handle_UpdateVehicleCommand_ShouldUpdateDetails_WhenVehicleExists() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var vehicle = new Vehicle(new CreateVehicleCommand("Mazda", "3", 2019, 45.0, imageBytes, 1L));
        var command = new UpdateVehicleCommand(5L, "Mazda", "3 Updated", 2020, 55.0, imageBytes);

        when(vehicleRepository.findById(5L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("3 Updated", result.get().getModel());
        assertEquals(55.0, result.get().getPricePerDay());
        verify(vehicleRepository, times(1)).findById(5L);
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    @DisplayName("handle(UpdateVehicleCommand) should throw VehicleNotFoundException when not found (AAA)")
    void handle_UpdateVehicleCommand_ShouldThrowException_WhenNotFound() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var command = new UpdateVehicleCommand(404L, "Brand", "Model", 2020, 50.0, imageBytes);
        when(vehicleRepository.findById(404L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VehicleNotFoundException.class, () -> vehicleCommandService.handle(command));
        verify(vehicleRepository, times(1)).findById(404L);
    }

    // -------------------------------------------------------------------------
    // handle(DeleteVehicleCommand command) - CASCADE DELETE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(DeleteVehicleCommand) should delete related entities and vehicle (AAA)")
    void handle_DeleteVehicleCommand_ShouldDeleteCascaded() {
        // Arrange
        var command = new DeleteVehicleCommand(12L);
        when(vehicleRepository.existsById(12L)).thenReturn(true);

        // Act
        vehicleCommandService.handle(command);

        // Assert
        verify(bookingRepository, times(1)).deleteByVehicleId(12L);
        verify(reviewRepository, times(1)).deleteByVehicleId(12L);
        verify(telemetryRepository, times(1)).deleteByVehicleId(12L);
        verify(vehicleRepository, times(1)).deleteById(12L);
    }

    @Test
    @DisplayName("handle(DeleteVehicleCommand) should throw VehicleNotFoundException when vehicle does not exist (AAA)")
    void handle_DeleteVehicleCommand_ShouldThrowException_WhenVehicleDoesNotExist() {
        // Arrange
        var command = new DeleteVehicleCommand(999L);
        when(vehicleRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(VehicleNotFoundException.class, () -> vehicleCommandService.handle(command));
        verify(vehicleRepository, times(1)).existsById(999L);
        verify(vehicleRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("handle(CreateVehicleCommand) should return empty Optional when repository save throws exception (AAA)")
    void handle_CreateVehicleCommand_ShouldReturnEmpty_WhenRepositorySaveFails() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var command = new CreateVehicleCommand("Toyota", "Yaris", 2021, 35.0, imageBytes, 1L);
        when(externalIamService.isOwner(1L)).thenReturn(true);
        when(vehicleRepository.save(any(Vehicle.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("handle(UpdateVehicleStatusCommand) should return empty Optional when vehicle does not exist (AAA)")
    void handle_UpdateVehicleStatusCommand_ShouldReturnEmpty_WhenVehicleNotFound() {
        // Arrange
        var command = new UpdateVehicleStatusCommand(999L, "rented");
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).findById(999L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("handle(UpdateVehicleStatusCommand) should return empty Optional when repository save throws exception (AAA)")
    void handle_UpdateVehicleStatusCommand_ShouldReturnEmpty_WhenSaveThrowsException() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var vehicle = new Vehicle(new CreateVehicleCommand("Nissan", "Versa", 2020, 40.0, imageBytes, 1L));
        var command = new UpdateVehicleStatusCommand(1L, "rented");
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenThrow(new RuntimeException("DB error"));

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("handle(UpdateVehicleCommand) should retain original image when new image is empty (AAA)")
    void handle_UpdateVehicleCommand_ShouldRetainOriginalImage_WhenNewImageIsEmpty() {
        // Arrange
        byte[] originalImage = new byte[]{9, 8, 7};
        byte[] emptyImage = new byte[]{};
        var vehicle = new Vehicle(new CreateVehicleCommand("Audi", "A4", 2021, 80.0, originalImage, 1L));
        var command = new UpdateVehicleCommand(1L, "Audi", "A4 Updated", 2022, 85.0, emptyImage);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("A4 Updated", result.get().getModel());
        assertArrayEquals(originalImage, result.get().getImage());
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    @DisplayName("handle(CreateVehicleCommand) should set default status to available (AAA)")
    void handle_CreateVehicleCommand_ShouldSetDefaultStatusToAvailable() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        var command = new CreateVehicleCommand("Chevrolet", "Onix", 2022, 38.0, imageBytes, 2L);
        when(externalIamService.isOwner(2L)).thenReturn(true);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Vehicle> result = vehicleCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("available", result.get().getStatus());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
}
