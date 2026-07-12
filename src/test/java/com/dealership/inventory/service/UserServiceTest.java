package com.dealership.inventory.service;

import com.dealership.inventory.dto.request.LoginRequest;
import com.dealership.inventory.dto.request.RegisterRequest;
import com.dealership.inventory.dto.response.AuthResponse;
import com.dealership.inventory.dto.response.UserResponse;
import com.dealership.inventory.exception.BadRequestException;
import com.dealership.inventory.model.Role;
import com.dealership.inventory.model.User;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.security.JwtTokenProvider;
import com.dealership.inventory.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("newuser", "password123", Collections.singleton("ROLE_USER"));
        loginRequest = new LoginRequest("newuser", "password123");
        user = User.builder()
                .id("user123")
                .username("newuser")
                .password("encodedPassword")
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();
    }

    @Test
    void registerUser_ShouldReturnUserResponse_WhenUsernameIsAvailable() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.registerUser(registerRequest);

        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_USER"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowBadRequestException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal principal = new UserPrincipal("user123", "newuser", "encodedPassword",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwtTokenValue");

        AuthResponse response = userService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals("jwtTokenValue", response.getToken());
        assertEquals("newuser", response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }
}
