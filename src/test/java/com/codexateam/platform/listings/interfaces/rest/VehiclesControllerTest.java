package com.codexateam.platform.listings.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codexateam.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.codexateam.platform.listings.domain.model.commands.DeleteVehicleCommand;
import com.codexateam.platform.listings.domain.services.VehicleCommandService;
import com.codexateam.platform.listings.domain.services.VehicleQueryService;
import com.codexateam.platform.listings.interfaces.rest.resources.CreateVehicleResource;
import com.codexateam.platform.listings.interfaces.rest.transform.VehicleResourceFromEntityAssembler;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class VehiclesControllerTest {

    @Mock
    private VehicleCommandService vehicleCommandService;

    @Mock
    private VehicleQueryService vehicleQueryService;

    @Mock
    private VehicleResourceFromEntityAssembler vehicleResourceFromEntityAssembler;

    @InjectMocks
    private VehiclesController vehiclesController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("deleteVehicle invokes command service and returns 204 No Content")
    void deleteVehicle_ShouldReturnNoContent() {
        var response = vehiclesController.deleteVehicle(15L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleCommandService, times(1)).handle(any(DeleteVehicleCommand.class));
    }

    @Test
    @DisplayName("createVehicle throws IllegalStateException when image stream fails")
    void createVehicle_ShouldThrowIllegalStateException_WhenImageReadFails() throws IOException {
        var principal = new UserDetailsImpl(1L, "Owner", "owner@renticar.com", "pass", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        var resource = new CreateVehicleResource("Toyota", "Corolla", 2023, 55.0);
        MultipartFile image = mock(MultipartFile.class);
        when(image.getBytes()).thenThrow(new IOException("disk read error"));

        assertThrows(IllegalStateException.class, () -> vehiclesController.createVehicle(resource, image));
    }

    @Test
    @DisplayName("updateVehicle throws IllegalStateException when image stream fails")
    void updateVehicle_ShouldThrowIllegalStateException_WhenImageReadFails() throws IOException {
        var resource = new CreateVehicleResource("Honda", "Civic", 2024, 65.0);
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getBytes()).thenThrow(new IOException("corrupted image"));

        assertThrows(IllegalStateException.class, () -> vehiclesController.updateVehicle(15L, resource, image));
    }
}
