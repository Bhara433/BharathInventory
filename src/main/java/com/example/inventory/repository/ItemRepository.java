package com.example.inventory.repository;

import com.example.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findBySku(String sku);
    
    Optional<Item> findByName(String name);
    
    List<Item> findByCategory(String category);
    
    List<Item> findByBrand(String brand);
    
    List<Item> findByIsActiveTrue();
    
    List<Item> findByIsActiveFalse();
    
    List<Item> findByIsActive(Boolean isActive);
    
    @Query("SELECT i FROM Item i WHERE i.isActive = :isActive")
    List<Item> findItemsByActiveStatus(@Param("isActive") Boolean isActive);
    
    @Query("SELECT i FROM Item i WHERE i.availableQuantity > 0 AND i.isActive = true")
    List<Item> findAvailableItems();
    
    @Query("SELECT i FROM Item i WHERE i.availableQuantity >= :quantity AND i.isActive = true")
    List<Item> findItemsWithAvailableQuantity(@Param("quantity") Integer quantity);
    
    @Query("SELECT i FROM Item i WHERE i.category = :category AND i.isActive = true")
    List<Item> findActiveItemsByCategory(@Param("category") String category);
    
    @Query("SELECT i FROM Item i WHERE i.brand = :brand AND i.isActive = true")
    List<Item> findActiveItemsByBrand(@Param("brand") String brand);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdWithLock(@Param("id") Long id);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.sku = :sku")
    Optional<Item> findBySkuWithLock(@Param("sku") String sku);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id AND i.isActive = true")
    Optional<Item> findActiveItemByIdWithLock(@Param("id") Long id);
    
    boolean existsBySku(String sku);
    
    boolean existsByName(String name);
    
    boolean existsBySkuAndIsActiveTrue(String sku);
    
    boolean existsByNameAndIsActiveTrue(String name);
    
    long countByIsActiveTrue();
    
    long countByIsActiveFalse();
} 