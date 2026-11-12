package com.codexateam.platform.iam.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.codexateam.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.commands.SignInCommand;
import com.codexateam.platform.iam.domain.model.commands.SignUpCommand;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.queries.GetRoleByNameQuery;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.iam.domain.services.RoleQueryService;
import com.codexateam.platform.iam.domain.services.UserCommandService;
import com.codexateam.platform.iam.interfaces.rest.resources.SignInResource;
import com.codexateam.platform.iam.interfaces.rest.resources.SignUpResource;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private RoleQueryService roleQueryService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    @DisplayName("signUp creates arrendador and arrendatario users cleanly")
    void signUp_ShouldCreateUser_ForArrendadorAndArrendatario() {
        var arrendadorRole = new Role(Roles.ROLE_ARRENDADOR);
        var arrendatarioRole = new Role(Roles.ROLE_ARRENDATARIO);
        var owner = new User("Owner", "owner@renticar.com", "hash", Set.of(arrendadorRole));
        var renter = new User("Renter", "renter@renticar.com", "hash", Set.of(arrendatarioRole));

        when(roleQueryService.handle(new GetRoleByNameQuery(Roles.ROLE_ARRENDADOR)))
                .thenReturn(Optional.of(arrendadorRole));
        when(roleQueryService.handle(new GetRoleByNameQuery(Roles.ROLE_ARRENDATARIO)))
                .thenReturn(Optional.of(arrendatarioRole));
        when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Optional.of(owner))
                .thenReturn(Optional.of(renter));

        var ownerResponse = authenticationController.signUp(
                new SignUpResource("Owner", "owner@renticar.com", "Secret123!", "arrendador"));
        var renterResponse = authenticationController.signUp(
                new SignUpResource("Renter", "renter@renticar.com", "Secret123!", "arrendatario"));

        assertEquals(HttpStatus.CREATED, ownerResponse.getStatusCode());
        assertEquals(HttpStatus.CREATED, renterResponse.getStatusCode());
    }

    @Test
    @DisplayName("signUp returns 400 when role is unsupported")
    void signUp_ShouldReturnBadRequest_WhenRoleUnsupported() {
        var response = authenticationController.signUp(
                new SignUpResource("User", "user@renticar.com", "Secret123!", "unknown"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("signIn returns 200 on valid credentials and 401 on invalid credentials")
    void signIn_ShouldReturnOkOrUnauthorized() {
        var user = new User("User", "user@renticar.com", "hash", Set.of(new Role(Roles.ROLE_ARRENDATARIO)));
        when(userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.empty());
        when(tokenService.generateToken("user@renticar.com")).thenReturn("jwt-token");

        var okResponse = authenticationController.signIn(new SignInResource("user@renticar.com", "Secret123!"));
        var unauthorizedResponse = authenticationController.signIn(new SignInResource("user@renticar.com", "wrong"));

        assertEquals(HttpStatus.OK, okResponse.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedResponse.getStatusCode());
    }
}
