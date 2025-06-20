package com.example.inventory.service;

import com.example.inventory.dto.ReservationRequest;
import com.example.inventory.entity.Item;
import com.example.inventory.entity.Reservation;
import com.example.inventory.repository.ItemRepository;
import com.example.inventory.repository.ReservationRepository;
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
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ReservationService reservationService;

    private Item testItem;
    private Reservation testReservation;
    private ReservationRequest reservationRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testItem = Item.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .sku("IPHONE-15-PRO-256")
                .price(new BigDecimal("999.99"))
                .availableQuantity(50)
                .reservedQuantity(0)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        testReservation = Reservation.builder()
                .id(1L)
                .item(testItem)
                .customerId("CUST-001")
                .quantity(2)
                .status(Reservation.ReservationStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .createdAt(now)
                .updatedAt(now)
                .build();

        reservationRequest = ReservationRequest.builder()
                .itemId(1L)
                .customerId("CUST-001")
                .quantity(2)
                .expirationMinutes(30)
                .build();
    }

    @Test
    void createReservation_Success() {
        when(itemRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.createReservation(reservationRequest);

        assertNotNull(result);
        assertEquals(testReservation.getCustomerId(), result.getCustomerId());
        assertEquals(testReservation.getQuantity(), result.getQuantity());
        verify(cacheService).evictItemCache(testItem.getId());
    }

    @Test
    void createReservation_ItemNotFound_ThrowsException() {
        when(itemRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(reservationRequest));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_InsufficientQuantity_ThrowsException() {
        testItem.setAvailableQuantity(1);
        when(itemRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testItem));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(reservationRequest));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_ItemInactive_ThrowsException() {
        testItem.setIsActive(false);
        when(itemRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testItem));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(reservationRequest));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancelReservation_Success() {
        testItem.setReservedQuantity(testReservation.getQuantity());
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.cancelReservation(1L);

        verify(itemRepository).save(any(Item.class));
        verify(reservationRepository).save(any(Reservation.class));
        verify(cacheService).evictItemCache(testItem.getId());
        verify(cacheService).evictReservationCache(1L);
    }

    @Test
    void cancelReservation_ReservationNotFound_ThrowsException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservationService.cancelReservation(1L));
    }

    @Test
    void cancelReservation_NotActive_ThrowsException() {
        testReservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        assertThrows(IllegalStateException.class, () -> reservationService.cancelReservation(1L));
    }

    @Test
    void getReservationsByCustomer_Success() {
        List<Reservation> reservations = List.of(testReservation);
        when(reservationRepository.findByCustomerId("CUST-001")).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByCustomer("CUST-001");

        assertEquals(1, result.size());
        assertEquals(testReservation.getCustomerId(), result.get(0).getCustomerId());
    }

    @Test
    void getReservationsByItem_Success() {
        List<Reservation> reservations = List.of(testReservation);
        when(reservationRepository.findByItemId(1L)).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByItem(1L);

        assertEquals(1, result.size());
        assertEquals(testReservation.getItem().getId(), result.get(0).getItem().getId());
    }

    @Test
    void getReservation_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        Optional<Reservation> result = reservationService.getReservation(1L);

        assertTrue(result.isPresent());
        assertEquals(testReservation.getId(), result.get().getId());
    }

    @Test
    void getReservation_NotFound_ReturnsEmpty() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Reservation> result = reservationService.getReservation(1L);

        assertFalse(result.isPresent());
    }
} 