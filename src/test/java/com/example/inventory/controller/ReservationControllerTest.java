package com.example.inventory.controller;

import com.example.inventory.dto.ReservationRequest;
import com.example.inventory.entity.Item;
import com.example.inventory.entity.Reservation;
import com.example.inventory.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Item testItem;
    private Reservation testReservation;
    private ReservationRequest reservationRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testItem = Item.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .sku("IPHONE-15-PRO-256")
                .price(new BigDecimal("999.99"))
                .availableQuantity(50)
                .reservedQuantity(0)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        testReservation = Reservation.builder()
                .id(1L)
                .item(testItem)
                .customerId("CUST-001")
                .quantity(2)
                .status(Reservation.ReservationStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .createdAt(now)
                .updatedAt(now)
                .build();

        reservationRequest = ReservationRequest.builder()
                .itemId(1L)
                .customerId("CUST-001")
                .quantity(2)
                .expirationMinutes(30)
                .build();
    }

    @Test
    void createReservation_Success() throws Exception {
        when(reservationService.createReservation(any(ReservationRequest.class))).thenReturn(testReservation);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void createReservation_InvalidRequest_ReturnsBadRequest() throws Exception {
        ReservationRequest invalidRequest = ReservationRequest.builder()
                .itemId(1L)
                .customerId("")
                .quantity(2)
                .build();

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelReservation_Success() throws Exception {
        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reservation cancelled successfully"));
    }

    @Test
    void getReservationsByCustomer_Success() throws Exception {
        when(reservationService.getReservationsByCustomer("CUST-001")).thenReturn(Arrays.asList(testReservation));

        mockMvc.perform(get("/api/reservations/customer/CUST-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerId").value("CUST-001"));
    }

    @Test
    void getReservationsByItem_Success() throws Exception {
        when(reservationService.getReservationsByItem(1L)).thenReturn(Arrays.asList(testReservation));

        mockMvc.perform(get("/api/reservations/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1));
    }

    @Test
    void getReservation_Success() throws Exception {
        when(reservationService.getReservation(1L)).thenReturn(Optional.of(testReservation));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value("CUST-001"));
    }

    @Test
    void getReservation_NotFound() throws Exception {
        when(reservationService.getReservation(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isNotFound());
    }
} 