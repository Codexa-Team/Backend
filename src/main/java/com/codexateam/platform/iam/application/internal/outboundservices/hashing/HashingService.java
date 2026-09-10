package com.codexateam.platform.iam.application.internal.outboundservices.hashing;

/**
 * Outbound service interface for password hashing.
 * This is a port in a Hexagonal Architecture.
 */
public interface HashingService {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
