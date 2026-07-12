package com.dealership.inventory.controller;

import com.dealership.inventory.dto.request.PurchaseRequest;
import com.dealership.inventory.dto.response.PurchaseResponse;
import com.dealership.inventory.security.JwtAuthenticationEntryPoint;
import com.dealership.inventory.security.JwtTokenProvider;
import com.dealership.inventory.service.PurchaseService;
import com.dealership.inventory.service.InvoiceService;
import com.dealership.inventory.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import com.dealership.inventory.config.SecurityConfig;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PurchaseController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private ObjectMapper objectMapper;

    private PurchaseRequest purchaseRequest;
    private PurchaseResponse purchaseResponse;

    @BeforeEach
    void setUp() {
        purchaseRequest = new PurchaseRequest("vehicle123", 2);
        purchaseResponse = PurchaseResponse.builder()
                .id("purchase123")
                .userId("user123")
                .vehicleId("vehicle123")
                .purchasePrice(40000.0)
                .quantity(2)
                .totalAmount(80000.0)
                .build();
    }

    @Test
    @WithMockUser(username = "customer")
    void purchaseVehicle_ShouldReturn201_WhenAuthenticated() throws Exception {
        when(purchaseService.purchaseVehicle(any(PurchaseRequest.class), anyString())).thenReturn(purchaseResponse);

        mockMvc.perform(post("/api/purchases")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("purchase123"))
                .andExpect(jsonPath("$.totalAmount").value(80000.0));
    }

    @Test
    @WithMockUser(username = "customer")
    void getPurchaseHistory_ShouldReturnList_WhenAuthenticated() throws Exception {
        when(purchaseService.getPurchaseHistory(anyString())).thenReturn(Collections.singletonList(purchaseResponse));

        mockMvc.perform(get("/api/purchases/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("purchase123"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllPurchases_ShouldReturnList_WhenAdmin() throws Exception {
        when(purchaseService.getAllPurchases()).thenReturn(Collections.singletonList(purchaseResponse));

        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("purchase123"));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void getAllPurchases_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer", roles = {"USER"})
    void downloadInvoice_ShouldReturnOk_WhenUserIsBuyer() throws Exception {
        com.dealership.inventory.model.User mockUser = com.dealership.inventory.model.User.builder()
                .id("user123")
                .username("customer")
                .build();
        when(userRepository.findByUsername("customer")).thenReturn(java.util.Optional.of(mockUser));

        com.dealership.inventory.model.PurchaseRecord mockRecord = com.dealership.inventory.model.PurchaseRecord.builder()
                .id("purchase123")
                .userId("user123")
                .build();
        when(purchaseService.getPurchaseRecordById("purchase123")).thenReturn(mockRecord);
        when(invoiceService.generateInvoicePdf(any())).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/purchases/purchase123/invoice"))
                .andExpect(status().isOk());
    }
}
