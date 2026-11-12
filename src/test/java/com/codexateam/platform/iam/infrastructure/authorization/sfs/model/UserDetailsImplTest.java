package com.codexateam.platform.iam.infrastructure.authorization.sfs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserDetailsImplTest {

    @Test
    @DisplayName("build(User) maps user attributes and granted authorities accurately")
    void build_ShouldMapUserAttributesAndAuthorities() {
        var roles = Set.of(new Role(Roles.ROLE_ARRENDADOR), new Role(Roles.ROLE_ARRENDATARIO));
        var user = new User("Ana Perez", "ana@renticar.com", "encodedPass", roles);

        UserDetailsImpl details = UserDetailsImpl.build(user);

        assertEquals("Ana Perez", details.getName());
        assertEquals("ana@renticar.com", details.getUsername());
        assertEquals("encodedPass", details.getPassword());
        assertEquals(2, details.getAuthorities().size());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }
}
