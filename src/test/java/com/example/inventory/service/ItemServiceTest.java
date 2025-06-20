package com.example.inventory.service;

import com.example.inventory.dto.CreateItemRequest;
import com.example.inventory.dto.ItemDto;
import com.example.inventory.entity.Item;
import com.example.inventory.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;
    private CreateItemRequest createItemRequest;

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
    }

    @Test
    void createItem_Success() {
        when(itemRepository.existsBySku(createItemRequest.getSku())).thenReturn(false);
        when(itemRepository.existsByName(createItemRequest.getName())).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemDto result = itemService.createItem(createItemRequest);

        assertNotNull(result);
        assertEquals(testItem.getName(), result.getName());
        assertEquals(testItem.getSku(), result.getSku());
        assertEquals(testItem.getPrice(), result.getPrice());
        verify(cacheService, times(1)).evictItemCache(testItem.getId());
        verify(cacheService, times(1)).evictItemCache(testItem.getSku());
    }

    @Test
    void createItem_DuplicateSku_ThrowsException() {
        when(itemRepository.existsBySku(createItemRequest.getSku())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(createItemRequest));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItem_DuplicateName_ThrowsException() {
        when(itemRepository.existsBySku(createItemRequest.getSku())).thenReturn(false);
        when(itemRepository.existsByName(createItemRequest.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(createItemRequest));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemById_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        Optional<ItemDto> result = itemService.getItemById(1L);

        assertTrue(result.isPresent());
        assertEquals(testItem.getName(), result.get().getName());
    }

    @Test
    void getItemById_NotFound_ReturnsEmpty() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ItemDto> result = itemService.getItemById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void getItemBySku_Success() {
        when(itemRepository.findBySku("IPHONE-15-PRO-256")).thenReturn(Optional.of(testItem));

        Optional<ItemDto> result = itemService.getItemBySku("IPHONE-15-PRO-256");

        assertTrue(result.isPresent());
        assertEquals(testItem.getSku(), result.get().getSku());
    }

    @Test
    void getAllItems_Success() {
        List<Item> items = List.of(testItem);
        when(itemRepository.findAll()).thenReturn(items);

        List<ItemDto> result = itemService.getAllItems();

        assertEquals(1, result.size());
        assertEquals(testItem.getName(), result.get(0).getName());
    }

    @Test
    void getAvailableItems_Success() {
        List<Item> items = List.of(testItem);
        when(itemRepository.findAvailableItems()).thenReturn(items);

        List<ItemDto> result = itemService.getAvailableItems();

        assertEquals(1, result.size());
        assertEquals(testItem.getName(), result.get(0).getName());
    }

    @Test
    void addSupply_Success() {
        when(itemRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemDto result = itemService.addSupply(1L, 10);

        assertNotNull(result);
        verify(cacheService, times(1)).evictItemCache(testItem.getId());
        verify(cacheService, times(1)).evictItemCache(testItem.getSku());
    }

    @Test
    void addSupply_ItemNotFound_ThrowsException() {
        when(itemRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> itemService.addSupply(1L, 10));
    }

    @Test
    void checkAvailability_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        boolean result = itemService.checkAvailability(1L, 10);

        assertTrue(result);
    }

    @Test
    void checkAvailability_InsufficientQuantity_ReturnsFalse() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        boolean result = itemService.checkAvailability(1L, 100);

        assertFalse(result);
    }

    @Test
    void checkAvailability_ItemNotFound_ReturnsFalse() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = itemService.checkAvailability(1L, 10);

        assertFalse(result);
    }
} 