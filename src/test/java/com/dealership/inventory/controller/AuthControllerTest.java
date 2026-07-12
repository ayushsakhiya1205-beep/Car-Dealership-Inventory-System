package com.dealership.inventory.controller;

import com.dealership.inventory.dto.request.LoginRequest;
import com.dealership.inventory.dto.request.RegisterRequest;
import com.dealership.inventory.dto.response.AuthResponse;
import com.dealership.inventory.dto.response.UserResponse;
import com.dealership.inventory.security.JwtAuthenticationEntryPoint;
import com.dealership.inventory.security.JwtTokenProvider;
import com.dealership.inventory.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Security filters for slice test mapping verification
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_ShouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password123", Collections.singleton("ROLE_USER"));
        UserResponse response = new UserResponse("user123", "testuser", Collections.singleton("ROLE_USER"));

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void authenticateUser_ShouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");
        AuthResponse response = AuthResponse.builder()
                .token("jwtTokenHere")
                .username("testuser")
                .roles(Collections.singleton("ROLE_USER"))
                .build();

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtTokenHere"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}
