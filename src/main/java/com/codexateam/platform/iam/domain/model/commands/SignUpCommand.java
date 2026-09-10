package com.codexateam.platform.iam.domain.model.commands;

import com.codexateam.platform.iam.domain.model.entities.Role;
import java.util.Set;

/**
 * Command to initiate a user sign-up process.
 * @param name The user's full name.
 * @param email The user's email.
 * @param password The user's raw password.
 * @param roles The set of roles to assign to the new user.
 */
public record SignUpCommand(String name, String email, String password, Set<Role> roles) {
}
