package com.example.inventory.controller;

import com.example.inventory.dto.ApiResponse;
import com.example.inventory.dto.CreateItemRequest;
import com.example.inventory.dto.ItemDto;
import com.example.inventory.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ApiResponse<ItemDto>> createItem(@Valid @RequestBody CreateItemRequest request) {
        ItemDto item = itemService.createItem(request);
        return ResponseEntity.ok(ApiResponse.success(item, "Item created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemDto>> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(item -> ResponseEntity.ok(ApiResponse.success(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ItemDto>> getItemBySku(@PathVariable String sku) {
        return itemService.getItemBySku(sku)
                .map(item -> ResponseEntity.ok(ApiResponse.success(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDto>>> getAllItems() {
        return ResponseEntity.ok(ApiResponse.success(itemService.getAllItems()));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ItemDto>>> getAvailableItems() {
        return ResponseEntity.ok(ApiResponse.success(itemService.getAvailableItems()));
    }

    @PostMapping("/{id}/supply")
    public ResponseEntity<ApiResponse<ItemDto>> addSupply(@PathVariable Long id, @RequestParam Integer quantity) {
        ItemDto item = itemService.addSupply(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(item, "Supply added successfully"));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean available = itemService.checkAvailability(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(available));
    }
} 