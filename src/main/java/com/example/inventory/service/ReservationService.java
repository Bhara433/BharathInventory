package com.example.inventory.service;

import com.example.inventory.dto.ReservationRequest;
import com.example.inventory.entity.Item;
import com.example.inventory.entity.Reservation;
import com.example.inventory.repository.ItemRepository;
import com.example.inventory.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final CacheService cacheService;

    @Transactional
    public Reservation createReservation(ReservationRequest request) {
        // Lock the item for concurrency
        Item item = itemRepository.findByIdWithLock(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!item.isActive() || !item.hasAvailableQuantity(request.getQuantity())) {
            throw new IllegalStateException("Insufficient inventory or item inactive");
        }
        // Reserve quantity
        item.reserveQuantity(request.getQuantity());
        itemRepository.save(item);
        // Create reservation
        Reservation reservation = Reservation.builder()
                .item(item)
                .customerId(request.getCustomerId())
                .quantity(request.getQuantity())
                .status(Reservation.ReservationStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(request.getExpirationMinutes() != null ? request.getExpirationMinutes() : 30))
                .build();
        Reservation saved = reservationRepository.save(reservation);
        cacheService.evictItemCache(item.getId());
        return saved;
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (reservation.getStatus() != Reservation.ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        // Return reserved quantity to item
        Item item = reservation.getItem();
        item.cancelReservation(reservation.getQuantity());
        itemRepository.save(item);
        // Update reservation status
        reservation.cancel();
        reservationRepository.save(reservation);
        cacheService.evictItemCache(item.getId());
        cacheService.evictReservationCache(reservationId);
    }

    public List<Reservation> getReservationsByCustomer(String customerId) {
        return reservationRepository.findByCustomerId(customerId);
    }

    public List<Reservation> getReservationsByItem(Long itemId) {
        return reservationRepository.findByItemId(itemId);
    }

    public Optional<Reservation> getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    // Expire reservations whose time has passed
    @Transactional
    public void expireReservations() {
        List<Reservation> expired = reservationRepository.findExpiredReservations(LocalDateTime.now());
        for (Reservation reservation : expired) {
            if (reservation.getStatus() == Reservation.ReservationStatus.ACTIVE) {
                reservation.markAsExpired();
                Item item = reservation.getItem();
                item.cancelReservation(reservation.getQuantity());
                itemRepository.save(item);
                reservationRepository.save(reservation);
                cacheService.evictItemCache(item.getId());
                cacheService.evictReservationCache(reservation.getId());
            }
        }
    }
} 