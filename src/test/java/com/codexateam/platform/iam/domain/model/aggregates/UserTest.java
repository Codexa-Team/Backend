package com.codexateam.platform.iam.domain.model.aggregates;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.EmailAddress;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    @DisplayName("User constructor without roles initializes fields and empty roles set (AAA)")
    void constructor_WithoutRoles_ShouldInitializeFields() {
        // Arrange
        String name = "Estefano Solis";
        String email = "estefano@renticar.com";
        String password = "hashedPassword123";

        // Act
        User user = new User(name, email, password);

        // Assert
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmailAddress().value());
        assertEquals(password, user.getPassword());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    @DisplayName("User constructor with roles initializes roles set properly (AAA)")
    void constructor_WithRoles_ShouldInitializeRoles() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(Roles.ROLE_ARRENDADOR));

        // Act
        User user = new User("Bruce Via", "bruce@renticar.com", "secret", roles);

        // Assert
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().stream().anyMatch(r -> r.getName() == Roles.ROLE_ARRENDADOR));
    }

    @Test
    @DisplayName("User addRole adds role and returns current instance (AAA)")
    void addRole_ShouldAddRoleAndReturnThis() {
        // Arrange
        User user = new User("Cesar Linares", "cesar@renticar.com", "secret");
        Role role = new Role(Roles.ROLE_ARRENDATARIO);

        // Act
        User result = user.addRole(role);

        // Assert
        assertSame(user, result);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    @DisplayName("User setEmail updates email Value Object correctly (AAA)")
    void setEmail_ShouldUpdateEmailValueObject() {
        // Arrange
        User user = new User("Sergio Landa", "old@renticar.com", "pass");

        // Act
        user.setEmail("new@renticar.com");

        // Assert
        assertEquals("new@renticar.com", user.getEmailAddress().value());
    }

    @Test
    @DisplayName("User setters for name and password update properties properly (AAA)")
    void setters_ShouldUpdateProperties() {
        // Arrange
        User user = new User("Old Name", "user@renticar.com", "oldPass");

        // Act
        user.setName("New Name");
        user.setPassword("newPass");

        // Assert
        assertEquals("New Name", user.getName());
        assertEquals("newPass", user.getPassword());
    }
}
