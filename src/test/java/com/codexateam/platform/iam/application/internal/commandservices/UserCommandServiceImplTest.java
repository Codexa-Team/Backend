package com.codexateam.platform.iam.application.internal.commandservices;

import com.codexateam.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.codexateam.platform.iam.domain.exceptions.InvalidPasswordException;
import com.codexateam.platform.iam.domain.exceptions.UserAlreadyExistsException;
import com.codexateam.platform.iam.domain.exceptions.UserNotFoundException;
import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.commands.DeleteUserCommand;
import com.codexateam.platform.iam.domain.model.commands.SignInCommand;
import com.codexateam.platform.iam.domain.model.commands.SignUpCommand;
import com.codexateam.platform.iam.domain.model.commands.UpdatePasswordCommand;
import com.codexateam.platform.iam.domain.model.commands.UpdateUserCommand;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.EmailAddress;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    // -------------------------------------------------------------------------
    // handle(SignUpCommand command) - CREATE USER
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(SignUpCommand) should save and return User when email does not exist (AAA)")
    void handle_SignUpCommand_ShouldSaveAndReturnUser_WhenEmailDoesNotExist() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(Roles.ROLE_ARRENDATARIO));
        var command = new SignUpCommand("Bruce Via", "bruce@renticar.com", "SecurePass123!", roles);

        when(userRepository.existsByEmail(any(EmailAddress.class))).thenReturn(false);
        when(hashingService.encode("SecurePass123!")).thenReturn("hashed_pass_xyz");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent(), "Expected User to be present upon registration");
        assertEquals("Bruce Via", result.get().getName());
        assertEquals("bruce@renticar.com", result.get().getEmailAddress().value());
        assertEquals("hashed_pass_xyz", result.get().getPassword());

        verify(userRepository, times(1)).existsByEmail(any(EmailAddress.class));
        verify(hashingService, times(1)).encode("SecurePass123!");
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, hashingService);
    }

    @Test
    @DisplayName("handle(SignUpCommand) should throw UserAlreadyExistsException when email exists (AAA)")
    void handle_SignUpCommand_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        var command = new SignUpCommand("Bruce Via", "existing@renticar.com", "Pass123!", roles);

        when(userRepository.existsByEmail(any(EmailAddress.class))).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userCommandService.handle(command));

        verify(userRepository, times(1)).existsByEmail(any(EmailAddress.class));
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(hashingService);
    }

    // -------------------------------------------------------------------------
    // handle(SignInCommand command) - AUTHENTICATION
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(SignInCommand) should return User when credentials are valid (AAA)")
    void handle_SignInCommand_ShouldReturnUser_WhenCredentialsAreValid() {
        // Arrange
        var command = new SignInCommand("bruce@renticar.com", "Password123!");
        var user = new User("Bruce Via", "bruce@renticar.com", "hashed_pwd");

        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(user));
        when(hashingService.matches("Password123!", "hashed_pwd")).thenReturn(true);

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Bruce Via", result.get().getName());

        verify(userRepository, times(1)).findByEmail(any(EmailAddress.class));
        verify(hashingService, times(1)).matches("Password123!", "hashed_pwd");
        verifyNoMoreInteractions(userRepository, hashingService);
    }

    @Test
    @DisplayName("handle(SignInCommand) should throw UserNotFoundException when email does not exist (AAA)")
    void handle_SignInCommand_ShouldThrowUserNotFoundException_WhenEmailNotFound() {
        // Arrange
        var command = new SignInCommand("unknown@renticar.com", "Password123!");

        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userCommandService.handle(command));

        verify(userRepository, times(1)).findByEmail(any(EmailAddress.class));
        verify(hashingService, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("handle(SignInCommand) should throw InvalidPasswordException when password mismatch (AAA)")
    void handle_SignInCommand_ShouldThrowInvalidPasswordException_WhenPasswordDoesNotMatch() {
        // Arrange
        var command = new SignInCommand("bruce@renticar.com", "WrongPassword");
        var user = new User("Bruce Via", "bruce@renticar.com", "hashed_pwd");

        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(user));
        when(hashingService.matches("WrongPassword", "hashed_pwd")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidPasswordException.class, () -> userCommandService.handle(command));

        verify(userRepository, times(1)).findByEmail(any(EmailAddress.class));
        verify(hashingService, times(1)).matches("WrongPassword", "hashed_pwd");
    }

    // -------------------------------------------------------------------------
    // handle(UpdateUserCommand command) - UPDATE PROFILE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(UpdateUserCommand) should update and return User when user exists (AAA)")
    void handle_UpdateUserCommand_ShouldUpdateUser_WhenUserExists() {
        // Arrange
        var user = new User("Estefano", "old@renticar.com", "hashed");
        var command = new UpdateUserCommand(1L, "Estefano Solis", "estefano@renticar.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Estefano Solis", result.get().getName());
        assertEquals("estefano@renticar.com", result.get().getEmailAddress().value());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("handle(UpdateUserCommand) should throw UserNotFoundException when user does not exist (AAA)")
    void handle_UpdateUserCommand_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        var command = new UpdateUserCommand(999L, "Estefano", "estefano@renticar.com");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userCommandService.handle(command));

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    // -------------------------------------------------------------------------
    // handle(UpdatePasswordCommand command) - UPDATE PASSWORD
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(UpdatePasswordCommand) should update password when current password matches (AAA)")
    void handle_UpdatePasswordCommand_ShouldUpdatePassword_WhenCurrentPasswordMatches() {
        // Arrange
        var user = new User("Estefano", "estefano@renticar.com", "old_hash");
        var command = new UpdatePasswordCommand(1L, "CurrentPass123!", "NewPass123!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hashingService.matches("CurrentPass123!", "old_hash")).thenReturn(true);
        when(hashingService.encode("NewPass123!")).thenReturn("new_hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("new_hash", result.get().getPassword());

        verify(userRepository, times(1)).findById(1L);
        verify(hashingService, times(1)).matches("CurrentPass123!", "old_hash");
        verify(hashingService, times(1)).encode("NewPass123!");
        verify(userRepository, times(1)).save(user);
    }

    // -------------------------------------------------------------------------
    // handle(DeleteUserCommand command) - DELETE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("handle(DeleteUserCommand) should delete user when user exists (AAA)")
    void handle_DeleteUserCommand_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        var command = new DeleteUserCommand(1L);
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userCommandService.handle(command);

        // Assert
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("handle(DeleteUserCommand) should throw UserNotFoundException when user does not exist (AAA)")
    void handle_DeleteUserCommand_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        var command = new DeleteUserCommand(999L);
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userCommandService.handle(command));
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("handle(UpdatePasswordCommand) should throw UserNotFoundException when user not found (AAA)")
    void handle_UpdatePasswordCommand_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        // Arrange
        var command = new UpdatePasswordCommand(999L, "CurrentPass123!", "NewPass123!");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userCommandService.handle(command));
        verify(userRepository, times(1)).findById(999L);
        verifyNoInteractions(hashingService);
    }

    @Test
    @DisplayName("handle(UpdatePasswordCommand) should throw InvalidPasswordException when new password is blank (AAA)")
    void handle_UpdatePasswordCommand_ShouldThrowException_WhenNewPasswordIsBlank() {
        // Arrange
        var user = new User("Estefano", "estefano@renticar.com", "hash");
        var command = new UpdatePasswordCommand(1L, "CurrentPass123!", "   ");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> userCommandService.handle(command));
        assertEquals("New password cannot be empty", ex.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("handle(UpdatePasswordCommand) should throw InvalidPasswordException when current password mismatch (AAA)")
    void handle_UpdatePasswordCommand_ShouldThrowException_WhenCurrentPasswordMismatch() {
        // Arrange
        var user = new User("Estefano", "estefano@renticar.com", "hash");
        var command = new UpdatePasswordCommand(1L, "WrongCurrentPass!", "NewPass123!");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hashingService.matches("WrongCurrentPass!", "hash")).thenReturn(false);

        // Act & Assert
        InvalidPasswordException ex = assertThrows(InvalidPasswordException.class, () -> userCommandService.handle(command));
        assertEquals("Current password is incorrect", ex.getMessage());
        verify(hashingService, times(1)).matches("WrongCurrentPass!", "hash");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("handle(UpdateUserCommand) should only update name when email is null (AAA)")
    void handle_UpdateUserCommand_ShouldOnlyUpdateName_WhenEmailIsNull() {
        // Arrange
        var user = new User("Old Name", "same@renticar.com", "hash");
        var command = new UpdateUserCommand(1L, "New Name", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getName());
        assertEquals("same@renticar.com", result.get().getEmailAddress().value());
        verify(userRepository, times(1)).save(user);
    }
}
