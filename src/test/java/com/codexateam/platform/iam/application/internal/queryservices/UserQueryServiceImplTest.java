package com.codexateam.platform.iam.application.internal.queryservices;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.codexateam.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.codexateam.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.codexateam.platform.iam.domain.model.valueobjects.EmailAddress;
import com.codexateam.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Test
    @DisplayName("handle(GetUserByIdQuery) should return user when user exists (AAA)")
    void handle_GetUserByIdQuery_ShouldReturnUser_WhenUserExists() {
        // Arrange
        var user = new User("Bruce Via", "bruce@renticar.com", "secret123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userQueryService.handle(new GetUserByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Bruce Via", result.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("handle(GetUserByIdQuery) should return empty Optional when user does not exist (AAA)")
    void handle_GetUserByIdQuery_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userQueryService.handle(new GetUserByIdQuery(99L));

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("handle(GetAllUsersQuery) should return list of users (AAA)")
    void handle_GetAllUsersQuery_ShouldReturnListOfUsers() {
        // Arrange
        var user1 = new User("Bruce", "bruce@renticar.com", "pass1");
        var user2 = new User("Estefano", "estefano@renticar.com", "pass2");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> result = userQueryService.handle(new GetAllUsersQuery());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("handle(GetAllUsersQuery) should return empty list when no users exist (AAA)")
    void handle_GetAllUsersQuery_ShouldReturnEmptyList_WhenNoUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<User> result = userQueryService.handle(new GetAllUsersQuery());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("handle(GetUserByIdQuery) should handle boundary ID Long.MAX_VALUE (AAA)")
    void handle_GetUserByIdQuery_ShouldHandleMaxLongId() {
        // Arrange
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userQueryService.handle(new GetUserByIdQuery(Long.MAX_VALUE));

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(Long.MAX_VALUE);
    }
}
