package com.codexateam.platform.iam.domain.model.aggregates;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.EmailAddress;
import com.codexateam.platform.iam.domain.model.valueobjects.EmailAddressAttributeConverter;
import com.codexateam.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the User aggregate root in the IAM bounded context.
 * Based on the 'users' table in db.json.
 * Extends AuditableAbstractAggregateRoot to include auditing fields.
 */
@NoArgsConstructor
@Setter
@Entity
@Table(name = "users")
public class User extends AuditableAbstractAggregateRoot<User> {

    /**
     * The user's full name.
     * Based on 'name' from db.json.
     */
    @NotBlank
    @Size(max = 100)
    private String name;

    @Convert(converter = EmailAddressAttributeConverter.class)
    private EmailAddress email;

    /**
     * The user's hashed password.
     * Based on 'password' from db.json.
     */
    @NotBlank
    @Size(max = 120)
    private String password;

    /**
     * A set of roles assigned to the user.
     * Based on 'role' from db.json.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Creates a user with name, email address, and encoded password.
     *
     * @param name user's full name
     * @param email user's email address
     * @param password encoded password
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = new EmailAddress(email);
        this.password = password;
    }

    public User(String name, String email, String password, Set<Role> roles) {
        this(name, email, password);
        this.roles.addAll(roles);
    }

    /**
     * Adds a role to the user's set of roles.
     *
     * @param role The Role entity to add.
     * @return The current User instance.
     */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public String getName() {
        return name;
    }

    public EmailAddress getEmailAddress() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setEmail(String newEmail) {
        this.email = new EmailAddress(newEmail);
    }
}
