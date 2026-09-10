package com.codexateam.platform.iam.domain.model.queries;

import com.codexateam.platform.iam.domain.model.valueobjects.Roles;

/**
 * Query to find a role by its enum name.
 * @param name The Roles enum value.
 */
public record GetRoleByNameQuery(Roles name) {
}
