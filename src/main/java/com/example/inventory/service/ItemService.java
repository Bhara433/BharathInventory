package com.example.inventory.service;

import com.example.inventory.dto.CreateItemRequest;
import com.example.inventory.dto.ItemDto;
import com.example.inventory.entity.Item;
import com.example.inventory.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final CacheService cacheService;
    
    public ItemDto createItem(CreateItemRequest request) {
        log.info("Creating new item with SKU: {}", request.getSku());
        
        if (itemRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Item with SKU " + request.getSku() + " already exists");
        }
        
        if (itemRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Item with name " + request.getName() + " already exists");
        }
        
        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(request.getPrice())
                .availableQuantity(request.getAvailableQuantity())
                .category(request.getCategory())
                .brand(request.getBrand())
                .isActive(true)
                .build();
        
        Item savedItem = itemRepository.save(item);
        cacheService.evictItemCache(savedItem.getId());
        cacheService.evictItemCache(savedItem.getSku());
        
        log.info("Item created successfully with ID: {}", savedItem.getId());
        return mapToDto(savedItem);
    }
    
    @Cacheable(value = "items", key = "#id")
    public Optional<ItemDto> getItemById(Long id) {
        log.debug("Fetching item by ID: {}", id);
        return itemRepository.findById(id).map(this::mapToDto);
    }
    
    @Cacheable(value = "items", key = "#sku")
    public Optional<ItemDto> getItemBySku(String sku) {
        log.debug("Fetching item by SKU: {}", sku);
        return itemRepository.findBySku(sku).map(this::mapToDto);
    }
    
    public List<ItemDto> getAllItems() {
        log.debug("Fetching all items");
        return itemRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<ItemDto> getAvailableItems() {
        log.debug("Fetching available items");
        return itemRepository.findAvailableItems().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<ItemDto> getItemsByCategory(String category) {
        log.debug("Fetching items by category: {}", category);
        return itemRepository.findByCategory(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<ItemDto> getItemsByBrand(String brand) {
        log.debug("Fetching items by brand: {}", brand);
        return itemRepository.findByBrand(brand).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ItemDto addSupply(Long itemId, Integer quantity) {
        log.info("Adding supply for item ID: {} with quantity: {}", itemId, quantity);
        
        Optional<Item> itemOpt = itemRepository.findByIdWithLock(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found with ID: " + itemId);
        }
        
        Item item = itemOpt.get();
        item.addSupply(quantity);
        Item savedItem = itemRepository.save(item);
        
        // Evict cache
        cacheService.evictItemCache(itemId);
        cacheService.evictItemCache(item.getSku());
        
        log.info("Supply added successfully. New available quantity: {}", savedItem.getAvailableQuantity());
        return mapToDto(savedItem);
    }
    
    @Transactional
    public ItemDto addSupplyBySku(String sku, Integer quantity) {
        log.info("Adding supply for item SKU: {} with quantity: {}", sku, quantity);
        
        Optional<Item> itemOpt = itemRepository.findBySkuWithLock(sku);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found with SKU: " + sku);
        }
        
        Item item = itemOpt.get();
        item.addSupply(quantity);
        Item savedItem = itemRepository.save(item);
        
        // Evict cache
        cacheService.evictItemCache(item.getId());
        cacheService.evictItemCache(sku);
        
        log.info("Supply added successfully. New available quantity: {}", savedItem.getAvailableQuantity());
        return mapToDto(savedItem);
    }
    
    public boolean checkAvailability(Long itemId, Integer quantity) {
        log.debug("Checking availability for item ID: {} with quantity: {}", itemId, quantity);
        
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return false;
        }
        
        Item item = itemOpt.get();
        return item.isActive() && item.hasAvailableQuantity(quantity);
    }
    
    public boolean checkAvailabilityBySku(String sku, Integer quantity) {
        log.debug("Checking availability for item SKU: {} with quantity: {}", sku, quantity);
        
        Optional<Item> itemOpt = itemRepository.findBySku(sku);
        if (itemOpt.isEmpty()) {
            return false;
        }
        
        Item item = itemOpt.get();
        return item.isActive() && item.hasAvailableQuantity(quantity);
    }
    
    @CacheEvict(value = "items", allEntries = true)
    public void evictAllItemCache() {
        log.debug("Evicting all item cache");
    }
    
    private ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .sku(item.getSku())
                .price(item.getPrice())
                .availableQuantity(item.getAvailableQuantity())
                .reservedQuantity(item.getReservedQuantity())
                .category(item.getCategory())
                .brand(item.getBrand())
                .isActive(item.getIsActive())
                .createdAt(item.getCreatedAt().toString())
                .updatedAt(item.getUpdatedAt().toString())
                .build();
    }
} 