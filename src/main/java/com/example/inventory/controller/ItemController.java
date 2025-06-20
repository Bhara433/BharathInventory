package com.example.inventory.controller;

import com.example.inventory.dto.CreateItemRequest;
import com.example.inventory.dto.ItemDto;
import com.example.inventory.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody CreateItemRequest request) {
        ItemDto item = itemService.createItem(request);
        return ResponseEntity.status(201).body(item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ItemDto> getItemBySku(@PathVariable String sku) {
        return itemService.getItemBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ItemDto>> getAvailableItems() {
        return ResponseEntity.ok(itemService.getAvailableItems());
    }

    @PostMapping("/{id}/supply")
    public ResponseEntity<ItemDto> addSupply(@PathVariable Long id, @RequestParam Integer quantity) {
        ItemDto item = itemService.addSupply(id, quantity);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean available = itemService.checkAvailability(id, quantity);
        return ResponseEntity.ok(available);
    }
} 