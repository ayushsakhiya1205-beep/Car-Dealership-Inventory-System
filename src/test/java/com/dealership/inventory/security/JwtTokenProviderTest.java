package com.dealership.inventory.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "9a2f96e4875324d67389a6a8c432d6f78a2f96e4875324d67389a6a8c432d6f7";
    private final long expiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, expiration);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        Authentication authentication = Mockito.mock(Authentication.class);
        UserPrincipal userPrincipal = new UserPrincipal("1", "testuser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        String token = tokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("testuser", tokenProvider.getUsernameFromJWT(token));
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalidToken"));
    }
}
