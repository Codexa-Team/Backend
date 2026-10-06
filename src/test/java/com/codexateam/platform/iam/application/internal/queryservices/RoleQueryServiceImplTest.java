package com.codexateam.platform.iam.application.internal.queryservices;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.codexateam.platform.iam.domain.model.queries.GetRoleByNameQuery;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleQueryServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleQueryServiceImpl roleQueryService;

    @Test
    @DisplayName("handle(GetAllRolesQuery) should return all roles from repository (AAA)")
    void handle_GetAllRolesQuery_ShouldReturnAllRoles() {
        // Arrange
        var role1 = new Role(Roles.ROLE_ARRENDADOR);
        var role2 = new Role(Roles.ROLE_ARRENDATARIO);
        when(roleRepository.findAll()).thenReturn(List.of(role1, role2));

        // Act
        List<Role> result = roleQueryService.handle(new GetAllRolesQuery());

        // Assert
        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("handle(GetRoleByNameQuery) should return role when found (AAA)")
    void handle_GetRoleByNameQuery_ShouldReturnRole_WhenFound() {
        // Arrange
        var role = new Role(Roles.ROLE_ARRENDADOR);
        when(roleRepository.findByName(Roles.ROLE_ARRENDADOR)).thenReturn(Optional.of(role));

        // Act
        Optional<Role> result = roleQueryService.handle(new GetRoleByNameQuery(Roles.ROLE_ARRENDADOR));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Roles.ROLE_ARRENDADOR, result.get().getName());
        verify(roleRepository, times(1)).findByName(Roles.ROLE_ARRENDADOR);
    }
}
