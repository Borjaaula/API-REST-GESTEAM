package com.backend.gesteam.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private static final String SECRET = "kNFi+goa0y5EL8W04bjiV5H0ais5kWFnlkl2bo+kmw/u9Cd3mjKRnZowR5HFlYSFnJ//d2K2WhNuShnjw4J4KQ==";

    @Test
    void generateAndValidateTokenForUser() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("Pep Guardiola")
                .password("admin")
                .authorities("ROLE_COACH")
                .build();

        String token = provider.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("Pep Guardiola", provider.extractUsernameFromToken(token));
        assertTrue(provider.isTokenValid(token, userDetails));
    }

    @Test
    void rejectsTokenForDifferentUser() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("Haaland")
                .password("1234")
                .authorities("ROLE_PLAYER")
                .build();
        UserDetails otherUser = org.springframework.security.core.userdetails.User.withUsername("Messi")
                .password("1234")
                .authorities("ROLE_PLAYER")
                .build();

        String token = provider.generateToken(userDetails);

        assertFalse(provider.isTokenValid(token, otherUser));
    }
}

