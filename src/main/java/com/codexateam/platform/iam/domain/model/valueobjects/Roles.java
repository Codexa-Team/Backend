package com.codexateam.platform.iam.domain.model.valueobjects;

/**
 * Enum representing the available roles in the system.
 * Based on the 'role' field in db.json ('arrendador', 'arrendatario').
 */
public enum Roles {
    /**
     * Represents a user who lists vehicles for rent (Owner).
     */
    ROLE_ARRENDADOR,
    
    /**
     * Represents a user who rents vehicles (Renter).
     */
    ROLE_ARRENDATARIO
}
