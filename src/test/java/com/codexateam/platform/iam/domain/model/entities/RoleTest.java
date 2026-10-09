package com.codexateam.platform.iam.domain.model.entities;

import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    @Test
    @DisplayName("Role constructor with Roles.ROLE_ARRENDADOR sets name enum (AAA)")
    void constructor_WithRoleArrendador_ShouldSetName() {
        // Arrange & Act
        Role role = new Role(Roles.ROLE_ARRENDADOR);

        // Assert
        assertNotNull(role);
        assertEquals(Roles.ROLE_ARRENDADOR, role.getName());
        assertEquals("ROLE_ARRENDADOR", role.getStringName());
    }

    @Test
    @DisplayName("Role constructor with Roles.ROLE_ARRENDATARIO sets name enum (AAA)")
    void constructor_WithRoleArrendatario_ShouldSetName() {
        // Arrange & Act
        Role role = new Role(Roles.ROLE_ARRENDATARIO);

        // Assert
        assertNotNull(role);
        assertEquals(Roles.ROLE_ARRENDATARIO, role.getName());
        assertEquals("ROLE_ARRENDATARIO", role.getStringName());
    }

    @Test
    @DisplayName("Role default constructor and setters work properly (AAA)")
    void settersAndGetters_ShouldWorkProperly() {
        // Arrange
        Role role = new Role();

        // Act
        role.setId(10L);
        role.setName(Roles.ROLE_ARRENDADOR);

        // Assert
        assertEquals(10L, role.getId());
        assertEquals(Roles.ROLE_ARRENDADOR, role.getName());
        assertEquals("ROLE_ARRENDADOR", role.getStringName());
    }
}
