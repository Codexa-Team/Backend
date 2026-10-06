package com.codexateam.platform.iam.application.internal.commandservices;

import com.codexateam.platform.iam.domain.model.commands.SeedRolesCommand;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleCommandServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleCommandServiceImpl roleCommandService;

    @Test
    @DisplayName("handle(SeedRolesCommand) should save roles that do not exist yet (AAA)")
    void handle_SeedRolesCommand_ShouldSaveNonExistingRoles() {
        // Arrange
        when(roleRepository.existsByName(any(Roles.class))).thenReturn(false);

        // Act
        roleCommandService.handle(new SeedRolesCommand());

        // Assert
        verify(roleRepository, times(Roles.values().length)).existsByName(any(Roles.class));
        verify(roleRepository, times(Roles.values().length)).save(any(Role.class));
    }

    @Test
    @DisplayName("handle(SeedRolesCommand) should skip roles that already exist (AAA)")
    void handle_SeedRolesCommand_ShouldSkipExistingRoles() {
        // Arrange
        when(roleRepository.existsByName(any(Roles.class))).thenReturn(true);

        // Act
        roleCommandService.handle(new SeedRolesCommand());

        // Assert
        verify(roleRepository, times(Roles.values().length)).existsByName(any(Roles.class));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("handle(SeedRolesCommand) should only save roles that are missing when some exist (AAA)")
    void handle_SeedRolesCommand_ShouldOnlySaveMissingRoles() {
        // Arrange
        when(roleRepository.existsByName(Roles.ROLE_ARRENDADOR)).thenReturn(true);
        when(roleRepository.existsByName(Roles.ROLE_ARRENDATARIO)).thenReturn(false);

        // Act
        roleCommandService.handle(new SeedRolesCommand());

        // Assert
        verify(roleRepository, times(1)).save(argThat(role -> role.getName() == Roles.ROLE_ARRENDATARIO));
        verify(roleRepository, never()).save(argThat(role -> role.getName() == Roles.ROLE_ARRENDADOR));
    }
}
