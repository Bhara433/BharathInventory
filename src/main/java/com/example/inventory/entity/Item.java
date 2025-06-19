package com.example.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Item name is required")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "SKU is required")
    @Column(name = "sku", nullable = false, unique = true)
    private String sku;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotNull(message = "Available quantity is required")
    @Positive(message = "Available quantity must be positive")
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;
    
    @NotNull(message = "Reserved quantity is required")
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version")
    private Long version;
    
    // Helper methods
    public Integer getTotalQuantity() {
        return availableQuantity + reservedQuantity;
    }
    
    public boolean hasAvailableQuantity(Integer requestedQuantity) {
        return availableQuantity >= requestedQuantity;
    }
    
    public void reserveQuantity(Integer quantity) {
        if (availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient available quantity");
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }
    
    public void cancelReservation(Integer quantity) {
        if (reservedQuantity < quantity) {
            throw new IllegalStateException("Insufficient reserved quantity");
        }
        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }
    
    public void addSupply(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Supply quantity must be positive");
        }
        availableQuantity += quantity;
    }
    
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
} 