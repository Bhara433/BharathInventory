package com.example.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    
    private final CacheManager cacheManager;
    
    public void evictItemCache(Long itemId) {
        log.debug("Evicting item cache for ID: {}", itemId);
        cacheManager.getCache("items").evict(itemId);
    }
    
    public void evictItemCache(String sku) {
        log.debug("Evicting item cache for SKU: {}", sku);
        cacheManager.getCache("items").evict(sku);
    }
    
    public void evictAllItemCache() {
        log.debug("Evicting all item cache");
        cacheManager.getCache("items").clear();
    }
    
    public void evictReservationCache(Long reservationId) {
        log.debug("Evicting reservation cache for ID: {}", reservationId);
        cacheManager.getCache("reservations").evict(reservationId);
    }
    
    public void evictAllReservationCache() {
        log.debug("Evicting all reservation cache");
        cacheManager.getCache("reservations").clear();
    }
} 