package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.interfaces.rest.resources.SignUpResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should transform SignUpResource into SignUpCommand (AAA)")
    void toCommandFromResource_ShouldTransformSuccessfully() {
        // Arrange
        var resource = new SignUpResource("Bruce", "bruce@renticar.com", "Password123!", "arrendatario");
        Set<Role> roles = Set.of(new Role(Roles.ROLE_ARRENDATARIO));

        // Act
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource, roles);

        // Assert
        assertNotNull(command);
        assertEquals("Bruce", command.name());
        assertEquals("bruce@renticar.com", command.email());
        assertEquals("Password123!", command.password());
        assertEquals(roles, command.roles());
    }
}
