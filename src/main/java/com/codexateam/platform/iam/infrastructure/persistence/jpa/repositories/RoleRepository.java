package com.codexateam.platform.iam.infrastructure.persistence.jpa.repositories;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.domain.model.valueobjects.Roles;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository interface for the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Roles name);

    boolean existsByName(Roles name);
}
