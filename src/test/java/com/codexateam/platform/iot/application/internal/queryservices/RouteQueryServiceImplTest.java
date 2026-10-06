package com.codexateam.platform.iot.application.internal.queryservices;

import com.codexateam.platform.iot.domain.exceptions.RouteNotFoundException;
import com.codexateam.platform.iot.domain.model.queries.GetCompleteRouteQuery;
import com.codexateam.platform.iot.domain.model.queries.GetRouteQuery;
import com.codexateam.platform.iot.infrastructure.external.OpenRouteServiceApiClient;
import com.codexateam.platform.iot.infrastructure.external.dto.RouteResponse;
import com.codexateam.platform.shared.domain.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteQueryServiceImplTest {

    @Mock
    private OpenRouteServiceApiClient openRouteServiceApiClient;

    @InjectMocks
    private RouteQueryServiceImpl routeQueryService;

    @Test
    @DisplayName("handle(GetRouteQuery) should return coordinates when service is configured and route found (AAA)")
    void handle_GetRouteQuery_ShouldReturnCoordinates_WhenConfigured() {
        // Arrange
        var query = new GetRouteQuery(-12.046374, -77.042793, -12.096374, -77.032793);
        List<double[]> mockCoords = List.of(new double[]{-12.046374, -77.042793}, new double[]{-12.096374, -77.032793});

        when(openRouteServiceApiClient.isConfigured()).thenReturn(true);
        when(openRouteServiceApiClient.getRouteCoordinates(-12.046374, -77.042793, -12.096374, -77.032793))
                .thenReturn(mockCoords);

        // Act
        List<double[]> result = routeQueryService.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(openRouteServiceApiClient, times(1)).isConfigured();
        verify(openRouteServiceApiClient, times(1)).getRouteCoordinates(-12.046374, -77.042793, -12.096374, -77.032793);
    }

    @Test
    @DisplayName("handle(GetRouteQuery) should throw ValidationException when coordinates are null (AAA)")
    void handle_GetRouteQuery_ShouldThrowException_WhenCoordinatesNull() {
        // Arrange
        var query = new GetRouteQuery(null, -77.0, -12.0, -77.0);

        // Act & Assert
        assertThrows(ValidationException.class, () -> routeQueryService.handle(query));
        verifyNoInteractions(openRouteServiceApiClient);
    }

    @Test
    @DisplayName("handle(GetRouteQuery) should throw RouteNotFoundException when service not configured (AAA)")
    void handle_GetRouteQuery_ShouldThrowException_WhenNotConfigured() {
        // Arrange
        var query = new GetRouteQuery(-12.0, -77.0, -12.1, -77.1);
        when(openRouteServiceApiClient.isConfigured()).thenReturn(false);

        // Act & Assert
        assertThrows(RouteNotFoundException.class, () -> routeQueryService.handle(query));
        verify(openRouteServiceApiClient, times(1)).isConfigured();
    }
}
