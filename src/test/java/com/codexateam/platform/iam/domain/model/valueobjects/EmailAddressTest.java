package com.codexateam.platform.iam.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class EmailAddressTest {

    @Test
    @DisplayName("EmailAddress with valid email creates instance successfully (AAA)")
    void constructor_WhenValidEmail_ShouldCreateInstance() {
        // Arrange
        String validEmail = "usuario@renticar.com";

        // Act
        EmailAddress emailAddress = new EmailAddress(validEmail);

        // Assert
        assertNotNull(emailAddress);
        assertEquals(validEmail, emailAddress.value());
        assertEquals(validEmail, emailAddress.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin@domain.pe", "john.doe123@sub.domain.org", "test_user+tag@renticar.com"})
    @DisplayName("EmailAddress accepts diverse valid email patterns (AAA)")
    void constructor_WhenValidPatterns_ShouldAccept(String email) {
        // Arrange & Act
        EmailAddress emailAddress = new EmailAddress(email);

        // Assert
        assertNotNull(emailAddress);
        assertEquals(email, emailAddress.value());
    }

    @Test
    @DisplayName("EmailAddress throws IllegalArgumentException when value is null (AAA)")
    void constructor_WhenNull_ShouldThrowException() {
        // Arrange
        String nullEmail = null;

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new EmailAddress(nullEmail));
        assertEquals("Email address cannot be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("EmailAddress throws IllegalArgumentException when value is blank (AAA)")
    void constructor_WhenBlank_ShouldThrowException(String blankEmail) {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new EmailAddress(blankEmail));
        assertEquals("Email address cannot be null or blank", ex.getMessage());
    }

    @Test
    @DisplayName("EmailAddress throws IllegalArgumentException when length exceeds 50 chars (AAA)")
    void constructor_WhenLengthExceeds50_ShouldThrowException() {
        // Arrange
        String longEmail = "a".repeat(45) + "@renticar.com"; // 58 chars

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new EmailAddress(longEmail));
        assertEquals("Email address length must be <= 50 characters", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-an-email", "missing-at.com", "user@invalid", "@domain.com", "user@.com"})
    @DisplayName("EmailAddress throws IllegalArgumentException when format is invalid (AAA)")
    void constructor_WhenInvalidFormat_ShouldThrowException(String invalidEmail) {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new EmailAddress(invalidEmail));
        assertTrue(ex.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("EmailAddress equals and hashCode contract holds for identical emails (AAA)")
    void equalsAndHashCode_WhenSameValue_ShouldBeEqual() {
        // Arrange
        EmailAddress email1 = new EmailAddress("user@renticar.pe");
        EmailAddress email2 = new EmailAddress("user@renticar.pe");
        EmailAddress email3 = new EmailAddress("other@renticar.pe");

        // Act & Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
        assertNotEquals(email1, email3);
    }
}
