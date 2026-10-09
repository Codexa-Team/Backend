package com.codexateam.platform.listings.application.internal.outboundservices.acl;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.domain.services.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalIamServiceImplTest {

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private ExternalIamServiceImpl externalIamService;

    @Test
    @DisplayName("isOwner returns true when user has ROLE_ARRENDADOR (AAA)")
    void isOwner_WhenUserHasRoleArrendador_ShouldReturnTrue() {
        // Arrange
        User ownerUser = new User("Owner User", "owner@renticar.com", "pass", Set.of(new Role(Roles.ROLE_ARRENDADOR)));
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.of(ownerUser));

        // Act
        boolean result = externalIamService.isOwner(1L);

        // Assert
        assertTrue(result);
        verify(userQueryService, times(1)).handle(any(GetUserByIdQuery.class));
    }

    @Test
    @DisplayName("isOwner returns false when user only has ROLE_ARRENDATARIO (AAA)")
    void isOwner_WhenUserOnlyHasRoleArrendatario_ShouldReturnFalse() {
        // Arrange
        User renterUser = new User("Renter User", "renter@renticar.com", "pass", Set.of(new Role(Roles.ROLE_ARRENDATARIO)));
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.of(renterUser));

        // Act
        boolean result = externalIamService.isOwner(2L);

        // Assert
        assertFalse(result);
        verify(userQueryService, times(1)).handle(any(GetUserByIdQuery.class));
    }

    @Test
    @DisplayName("isOwner returns false when user is not found in IAM (AAA)")
    void isOwner_WhenUserNotFound_ShouldReturnFalse() {
        // Arrange
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.empty());

        // Act
        boolean result = externalIamService.isOwner(999L);

        // Assert
        assertFalse(result);
        verify(userQueryService, times(1)).handle(any(GetUserByIdQuery.class));
    }

    @Test
    @DisplayName("isOwner returns false when userQueryService throws exception (AAA)")
    void isOwner_WhenExceptionThrown_ShouldReturnFalseSafely() {
        // Arrange
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenThrow(new RuntimeException("IAM service error"));

        // Act
        boolean result = externalIamService.isOwner(1L);

        // Assert
        assertFalse(result);
        verify(userQueryService, times(1)).handle(any(GetUserByIdQuery.class));
    }
}
