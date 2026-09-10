package com.codexateam.platform.iam.application.internal.outboundservices.tokens;

/**
 * Outbound service interface for JWT generation and validation.
 * This is a port in a Hexagonal Architecture.
 */
public interface TokenService {
    String generateToken(String email);
    String getEmailFromToken(String token);
    boolean validateToken(String token);
}
