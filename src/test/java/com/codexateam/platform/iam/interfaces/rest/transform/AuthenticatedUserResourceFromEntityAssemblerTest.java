package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticatedUserResourceFromEntityAssemblerTest {

    @Test
    @DisplayName("toResourceFromEntity transforms User and token to AuthenticatedUserResource (AAA)")
    void toResourceFromEntity_ShouldTransformCorrectly() {
        // Arrange
        User user = new User("Bruce Via", "bruce@renticar.com", "pass123", Set.of(new Role(Roles.ROLE_ARRENDADOR)));
        String token = "jwt.test.token.xyz";

        // Act
        AuthenticatedUserResource resource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, token);

        // Assert
        assertNotNull(resource);
        assertEquals("Bruce Via", resource.name());
        assertEquals("bruce@renticar.com", resource.email());
        assertEquals(token, resource.token());
        assertEquals(1, resource.roles().size());
        assertTrue(resource.roles().contains("ROLE_ARRENDADOR"));
    }
}
