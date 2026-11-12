package com.codexateam.platform.iam.infrastructure.authorization.sfs.model;

import com.codexateam.platform.iam.domain.model.aggregates.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A custom UserDetails implementation that wraps the User aggregate.
 */
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String name;
    private final String email;
    @JsonIgnore
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Creates an immutable {@link UserDetailsImpl} backed by domain user credentials.
     *
     * @param id user ID
     * @param name user's full name
     * @param email user's email address
     * @param password encoded password hash
     * @param authorities granted Spring Security authorities
     */
    public UserDetailsImpl(
            Long id,
            String name,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Factory method to build a UserDetailsImpl from a User aggregate.
     *
     * @param user The User aggregate.
     * @return A UserDetailsImpl instance.
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority(role.getStringName()))
                .toList();
        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getEmailAddress().value(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Use email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
