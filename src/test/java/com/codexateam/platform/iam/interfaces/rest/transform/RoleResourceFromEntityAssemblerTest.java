package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.interfaces.rest.resources.RoleResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleResourceFromEntityAssemblerTest {

    @Test
    @DisplayName("toResourceFromEntity transforms Role entity to RoleResource (AAA)")
    void toResourceFromEntity_ShouldTransformCorrectly() {
        // Arrange
        Role role = new Role(Roles.ROLE_ARRENDATARIO);
        role.setId(5L);

        // Act
        RoleResource resource = RoleResourceFromEntityAssembler.toResourceFromEntity(role);

        // Assert
        assertNotNull(resource);
        assertEquals(5L, resource.id());
        assertEquals("ROLE_ARRENDATARIO", resource.name());
    }
}
