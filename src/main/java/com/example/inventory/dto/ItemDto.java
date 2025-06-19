package com.example.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    
    private Long id;
    
    @NotBlank(message = "Item name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Available quantity is required")
    @Positive(message = "Available quantity must be positive")
    private Integer availableQuantity;
    
    private Integer reservedQuantity;
    
    private String category;
    
    private String brand;
    
    private Boolean isActive;
    
    private String createdAt;
    
    private String updatedAt;
} 