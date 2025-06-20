package com.example.inventory.controller;

import com.example.inventory.dto.CreateItemRequest;
import com.example.inventory.dto.ItemDto;
import com.example.inventory.entity.Item;
import com.example.inventory.service.ItemService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private Item testItem;
    private CreateItemRequest createItemRequest;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testItem = Item.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Latest iPhone with advanced features")
                .sku("IPHONE-15-PRO-256")
                .price(new BigDecimal("999.99"))
                .availableQuantity(50)
                .reservedQuantity(0)
                .category("Electronics")
                .brand("Apple")
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        createItemRequest = CreateItemRequest.builder()
                .name("iPhone 15 Pro")
                .description("Latest iPhone with advanced features")
                .sku("IPHONE-15-PRO-256")
                .price(new BigDecimal("999.99"))
                .availableQuantity(50)
                .category("Electronics")
                .brand("Apple")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Latest iPhone with advanced features")
                .sku("IPHONE-15-PRO-256")
                .price(new BigDecimal("999.99"))
                .availableQuantity(50)
                .reservedQuantity(0)
                .category("Electronics")
                .brand("Apple")
                .isActive(true)
                .createdAt(now.toString())
                .updatedAt(now.toString())
                .build();
    }

    @Test
    void createItem_Success() throws Exception {
        when(itemService.createItem(any(CreateItemRequest.class))).thenReturn(itemDto);

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.sku").value("IPHONE-15-PRO-256"));
    }

    @Test
    void createItem_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateItemRequest invalidRequest = CreateItemRequest.builder()
                .name("")
                .sku("IPHONE-15-PRO-256")
                .price(new BigDecimal("999.99"))
                .availableQuantity(50)
                .build();

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_Success() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(Optional.of(itemDto));

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"));
    }

    @Test
    void getItemById_NotFound() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemBySku_Success() throws Exception {
        when(itemService.getItemBySku("IPHONE-15-PRO-256")).thenReturn(Optional.of(itemDto));

        mockMvc.perform(get("/api/items/sku/IPHONE-15-PRO-256"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("IPHONE-15-PRO-256"));
    }

    @Test
    void getItemBySku_NotFound_Returns404() throws Exception {
        when(itemService.getItemBySku("INVALID-SKU")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/sku/INVALID-SKU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItems_Success() throws Exception {
        when(itemService.getAllItems()).thenReturn(Arrays.asList(itemDto));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("iPhone 15 Pro"));
    }

    @Test
    void getAvailableItems_Success() throws Exception {
        when(itemService.getAvailableItems()).thenReturn(Arrays.asList(itemDto));

        mockMvc.perform(get("/api/items/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void addSupply_Success() throws Exception {
        when(itemService.addSupply(1L, 10)).thenReturn(itemDto);

        mockMvc.perform(post("/api/items/1/supply")
                .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void checkAvailability_Success() throws Exception {
        when(itemService.checkAvailability(1L, 10)).thenReturn(true);

        mockMvc.perform(get("/api/items/1/availability")
                .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkAvailability_NotAvailable_ReturnsFalse() throws Exception {
        when(itemService.checkAvailability(eq(1L), eq(100))).thenReturn(false);

        mockMvc.perform(get("/api/items/1/availability")
                        .param("quantity", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
} 