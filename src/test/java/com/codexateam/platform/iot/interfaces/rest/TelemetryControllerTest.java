package com.codexateam.platform.iot.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.codexateam.platform.booking.domain.model.aggregates.Booking;
import com.codexateam.platform.booking.domain.model.commands.CreateBookingCommand;
import com.codexateam.platform.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.codexateam.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.codexateam.platform.iot.application.internal.outboundservices.AutomaticTelemetryGeneratorService;
import com.codexateam.platform.iot.domain.model.aggregates.Telemetry;
import com.codexateam.platform.iot.domain.model.commands.RecordTelemetryCommand;
import com.codexateam.platform.iot.domain.model.queries.GetLatestTelemetryQuery;
import com.codexateam.platform.iot.domain.services.SimulationCommandService;
import com.codexateam.platform.iot.domain.services.TelemetryCommandService;
import com.codexateam.platform.iot.domain.services.TelemetryQueryService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
class TelemetryControllerTest {

    @Mock
    private TelemetryCommandService telemetryCommandService;

    @Mock
    private TelemetryQueryService telemetryQueryService;

    @Mock
    private SimulationCommandService simulationCommandService;

    @Mock
    private AutomaticTelemetryGeneratorService automaticTelemetryGeneratorService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TelemetryController telemetryController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getLatestTelemetryByVehicleId enriches resource with active confirmed booking renter name")
    void getLatestTelemetryByVehicleId_ShouldEnrichWithActiveRenterName() {
        var principal = new UserDetailsImpl(1L, "Owner", "owner@renticar.com", "pass", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        var telemetry = new Telemetry(new RecordTelemetryCommand(10L, -12.04, -77.03, 45.0, 82.0));
        var booking = new Booking(new CreateBookingCommand(10L, 5L, 1L, new Date(), new Date()), 120.0);
        var renter = new User("Carlos Renter", "carlos@renticar.com", "pass");

        when(automaticTelemetryGeneratorService.getPlannedRoute(10L)).thenReturn(List.of());
        when(telemetryQueryService.handle(any(GetLatestTelemetryQuery.class), eq(1L)))
                .thenReturn(Optional.of(telemetry));
        when(bookingRepository.findFirstActiveBookingByVehicleAndStatus(
                eq(10L), eq("CONFIRMED"), any(Date.class), any(Date.class)))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(5L)).thenReturn(Optional.of(renter));

        var response = telemetryController.getLatestTelemetryByVehicleId(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Carlos Renter", response.getBody().getRenterName());
    }
}
