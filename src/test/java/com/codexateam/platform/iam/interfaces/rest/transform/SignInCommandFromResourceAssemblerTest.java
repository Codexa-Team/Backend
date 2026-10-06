package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.interfaces.rest.resources.SignInResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignInCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should transform SignInResource into SignInCommand (AAA)")
    void toCommandFromResource_ShouldTransformSuccessfully() {
        // Arrange
        var resource = new SignInResource("user@renticar.com", "SecretPass123");

        // Act
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertNotNull(command);
        assertEquals("user@renticar.com", command.email());
        assertEquals("SecretPass123", command.password());
    }
}
