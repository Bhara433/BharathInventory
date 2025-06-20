package com.example.inventory.controller;

import com.example.inventory.dto.ReservationRequest;
import com.example.inventory.entity.Reservation;
import com.example.inventory.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.createReservation(request);
        return ResponseEntity.status(201).body(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok("Reservation cancelled successfully");
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Reservation>> getReservationsByCustomer(@PathVariable String customerId) {
        List<Reservation> reservations = reservationService.getReservationsByCustomer(customerId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<Reservation>> getReservationsByItem(@PathVariable Long itemId) {
        List<Reservation> reservations = reservationService.getReservationsByItem(itemId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        return reservationService.getReservation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 