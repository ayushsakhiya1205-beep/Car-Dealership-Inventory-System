package com.dealership.inventory.controller;

import com.dealership.inventory.config.SecurityConfig;
import com.dealership.inventory.dto.response.AnalyticsResponse;
import com.dealership.inventory.security.JwtAuthenticationEntryPoint;
import com.dealership.inventory.security.JwtTokenProvider;
import com.dealership.inventory.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnalyticsController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class})
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAnalyticsSummary_ShouldReturnOk_WhenUserIsAdmin() throws Exception {
        AnalyticsResponse response = AnalyticsResponse.builder()
                .totalVehicles(10)
                .totalDistinctModels(3L)
                .totalInventoryValue(500000.0)
                .totalSoldVehicles(4)
                .lowStockVehicles(1L)
                .outOfStockVehicles(0L)
                .categoryDistribution(Collections.emptyList())
                .inventoryByCategory(Collections.emptyList())
                .monthlySales(Collections.emptyList())
                .build();

        when(analyticsService.getAnalyticsSummary()).thenReturn(response);

        mockMvc.perform(get("/api/analytics/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVehicles").value(10))
                .andExpect(jsonPath("$.totalDistinctModels").value(3))
                .andExpect(jsonPath("$.totalInventoryValue").value(500000.0));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAnalyticsSummary_ShouldReturnForbidden_WhenUserIsCustomer() throws Exception {
        mockMvc.perform(get("/api/analytics/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAnalyticsSummary_ShouldReturnUnauthorized_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/analytics/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
