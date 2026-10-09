package com.codexateam.platform.booking.application.internal.outboundservices.acl;

import com.codexateam.platform.listings.interfaces.acl.ListingsContextFacade;
import com.codexateam.platform.listings.interfaces.rest.resources.VehicleResource;
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
public class ExternalListingsServiceImplTest {

    @Mock
    private ListingsContextFacade listingsContextFacade;

    @InjectMocks
    private ExternalListingsServiceImpl externalListingsService;

    @Test
    @DisplayName("fetchVehicleById returns VehicleResource when found in facade (AAA)")
    void fetchVehicleById_WhenFound_ShouldReturnResource() {
        // Arrange
        var resource = new VehicleResource(1L, "Toyota", "Corolla", 2022, 50.0, "available", "img.jpg", 2L, new Date());
        when(listingsContextFacade.getVehicleById(1L)).thenReturn(Optional.of(resource));

        // Act
        Optional<VehicleResource> result = externalListingsService.fetchVehicleById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Toyota", result.get().brand());
        verify(listingsContextFacade, times(1)).getVehicleById(1L);
    }

    @Test
    @DisplayName("fetchVehicleById returns empty Optional when facade throws exception (AAA)")
    void fetchVehicleById_WhenFacadeThrows_ShouldReturnEmpty() {
        // Arrange
        when(listingsContextFacade.getVehicleById(999L)).thenThrow(new RuntimeException("Listings error"));

        // Act
        Optional<VehicleResource> result = externalListingsService.fetchVehicleById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(listingsContextFacade, times(1)).getVehicleById(999L);
    }

    @Test
    @DisplayName("getVehiclePriceById delegates to facade and returns price (AAA)")
    void getVehiclePriceById_ShouldDelegateToFacade() {
        // Arrange
        when(listingsContextFacade.getVehiclePriceById(1L)).thenReturn(Optional.of(55.0));

        // Act
        Optional<Double> result = externalListingsService.getVehiclePriceById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(55.0, result.get());
        verify(listingsContextFacade, times(1)).getVehiclePriceById(1L);
    }

    @Test
    @DisplayName("updateVehicleStatus delegates to facade (AAA)")
    void updateVehicleStatus_ShouldDelegateToFacade() {
        // Arrange
        when(listingsContextFacade.updateVehicleStatus(1L, "rented")).thenReturn(Optional.empty());

        // Act
        externalListingsService.updateVehicleStatus(1L, "rented");

        // Assert
        verify(listingsContextFacade, times(1)).updateVehicleStatus(1L, "rented");
    }
}
