package com.example.inventory.repository;

import com.example.inventory.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByCustomerId(String customerId);
    
    List<Reservation> findByItemId(Long itemId);
    
    List<Reservation> findByCustomerIdAndStatus(String customerId, Reservation.ReservationStatus status);
    
    List<Reservation> findByItemIdAndStatus(Long itemId, Reservation.ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' AND r.expiresAt < :now")
    List<Reservation> findExpiredReservations(@Param("now") LocalDateTime now);
    
    @Query("SELECT r FROM Reservation r WHERE r.customerId = :customerId AND r.item.id = :itemId AND r.status = 'ACTIVE'")
    List<Reservation> findActiveReservationsByCustomerAndItem(@Param("customerId") String customerId, 
                                                             @Param("itemId") Long itemId);
    
    @Query("SELECT SUM(r.quantity) FROM Reservation r WHERE r.item.id = :itemId AND r.status = 'ACTIVE'")
    Integer getTotalReservedQuantityForItem(@Param("itemId") Long itemId);
    
    boolean existsByCustomerIdAndItemIdAndStatus(String customerId, Long itemId, Reservation.ReservationStatus status);
} 