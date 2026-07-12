package com.dealership.inventory.controller;

import com.dealership.inventory.dto.request.RestockRequest;
import com.dealership.inventory.dto.request.VehicleRequest;
import com.dealership.inventory.dto.response.VehicleResponse;
import com.dealership.inventory.security.JwtAuthenticationEntryPoint;
import com.dealership.inventory.security.JwtTokenProvider;
import com.dealership.inventory.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VehicleController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Security filters for slice test mapping verification
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private ObjectMapper objectMapper;

    private VehicleResponse vehicleResponse;
    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        vehicleResponse = VehicleResponse.builder()
                .id("vehicle123")
                .make("Tesla")
                .model("Model 3")
                .year(2023)
                .price(40000.0)
                .stock(5)
                .description("Red Tesla")
                .build();

        vehicleRequest = new VehicleRequest("Tesla", "Model 3", 2023, 40000.0, 5, "Red Tesla", "Electric");
    }

    @Test
    void createVehicle_ShouldReturn201() throws Exception {
        when(vehicleService.createVehicle(any(VehicleRequest.class))).thenReturn(vehicleResponse);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("vehicle123"))
                .andExpect(jsonPath("$.make").value("Tesla"));
    }

    @Test
    void getVehicleById_ShouldReturn200() throws Exception {
        when(vehicleService.getVehicleById("vehicle123")).thenReturn(vehicleResponse);

        mockMvc.perform(get("/api/vehicles/vehicle123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("vehicle123"))
                .andExpect(jsonPath("$.model").value("Model 3"));
    }

    @Test
    void searchVehicles_ShouldReturnList() throws Exception {
        when(vehicleService.searchVehicles(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(vehicleResponse));

        mockMvc.perform(get("/api/vehicles?keyword=Tesla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].make").value("Tesla"));
    }

    @Test
    void updateVehicle_ShouldReturn200() throws Exception {
        when(vehicleService.updateVehicle(anyString(), any(VehicleRequest.class))).thenReturn(vehicleResponse);

        mockMvc.perform(put("/api/vehicles/vehicle123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("vehicle123"));
    }

    @Test
    void deleteVehicle_ShouldReturn204() throws Exception {
        doNothing().when(vehicleService).deleteVehicle("vehicle123");

        mockMvc.perform(delete("/api/vehicles/vehicle123"))
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1)).deleteVehicle("vehicle123");
    }

    @Test
    void restockVehicle_ShouldReturn200() throws Exception {
        RestockRequest restockRequest = new RestockRequest(10);
        when(vehicleService.restockVehicle(anyString(), anyInt())).thenReturn(vehicleResponse);

        mockMvc.perform(patch("/api/vehicles/vehicle123/restock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restockRequest)))
                .andExpect(status().isOk());
    }
}
