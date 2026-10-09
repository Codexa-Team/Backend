package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.interfaces.rest.resources.UserResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserResourceFromEntityAssemblerTest {

    @Test
    @DisplayName("toResourceFromEntity transforms User with roles to UserResource (AAA)")
    void toResourceFromEntity_WithRoles_ShouldTransformCorrectly() {
        // Arrange
        User user = new User("Bruce Via", "bruce@renticar.com", "pass123", Set.of(new Role(Roles.ROLE_ARRENDADOR)));

        // Act
        UserResource resource = UserResourceFromEntityAssembler.toResourceFromEntity(user);

        // Assert
        assertNotNull(resource);
        assertEquals("Bruce Via", resource.name());
        assertEquals("bruce@renticar.com", resource.email());
        assertEquals(1, resource.roles().size());
        assertTrue(resource.roles().contains("ROLE_ARRENDADOR"));
    }

    @Test
    @DisplayName("toResourceFromEntity transforms User with empty roles to UserResource (AAA)")
    void toResourceFromEntity_WithEmptyRoles_ShouldTransformCorrectly() {
        // Arrange
        User user = new User("Estefano Solis", "estefano@renticar.com", "pass123");

        // Act
        UserResource resource = UserResourceFromEntityAssembler.toResourceFromEntity(user);

        // Assert
        assertNotNull(resource);
        assertEquals("Estefano Solis", resource.name());
        assertEquals("estefano@renticar.com", resource.email());
        assertTrue(resource.roles().isEmpty());
    }
}
