package com.codexateam.platform.iam.interfaces.rest.transform;

import com.codexateam.platform.iam.domain.model.entities.Role;
import com.codexateam.platform.iam.interfaces.rest.resources.RoleResource;

/**
 * Assembler to convert Role entity to RoleResource DTO.
 */
public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}
