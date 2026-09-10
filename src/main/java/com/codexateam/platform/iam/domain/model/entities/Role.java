package com.codexateam.platform.iam.domain.model.entities;

import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import com.codexateam.platform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Role entity.
 * Extends AuditableModel to include auditing fields.
 * The role name is stored as an enum.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the role (e.g., ROLE_ARRENDADOR, ROLE_ARRENDATARIO).
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 30, unique = true, nullable = false)
    private Roles name;

    public Role(Roles name) {
        this.name = name;
    }

    /**
     * Gets the role name as a string.
     * @return The string representation of the role enum.
     */
    public String getStringName() {
        return name.name();
    }
}
