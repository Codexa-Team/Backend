package com.codexateam.platform.shared.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codexateam.platform.shared.domain.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleTypeMismatchException uses type simple name when requiredType is present")
    @SuppressWarnings("unchecked")
    void handleTypeMismatchException_ShouldFormatExpectedType_WhenRequiredTypePresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/vehicles/abc");
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("vehicleId");
        when(ex.getRequiredType()).thenReturn((Class) Long.class);

        var response = handler.handleTypeMismatchException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Long"));
    }

    @Test
    @DisplayName("handleTypeMismatchException falls back to unknown when requiredType is null")
    void handleTypeMismatchException_ShouldFallbackToUnknown_WhenRequiredTypeNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/bookings/xyz");
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("bookingId");
        when(ex.getRequiredType()).thenReturn(null);

        var response = handler.handleTypeMismatchException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("unknown"));
    }

    @Test
    @DisplayName("handleDomainValidationException and IllegalArgumentException return 400 Bad Request")
    void handleValidationAndIllegalArgument_ShouldReturnBadRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/bookings");

        var validationResp = handler.handleDomainValidationException(
                new ValidationException("invalid field"), request);
        var illegalResp = handler.handleIllegalArgumentException(
                new IllegalArgumentException("bad arg"), request);

        assertEquals(HttpStatus.BAD_REQUEST, validationResp.getStatusCode());
        assertEquals("Bad Request", validationResp.getBody().getError());
        assertEquals(HttpStatus.BAD_REQUEST, illegalResp.getStatusCode());
        assertEquals("Bad Request", illegalResp.getBody().getError());
    }
}
