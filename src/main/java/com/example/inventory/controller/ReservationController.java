package com.example.inventory.controller;

import com.example.inventory.dto.ApiResponse;
import com.example.inventory.dto.ReservationRequest;
import com.example.inventory.entity.Reservation;
import com.example.inventory.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Reservation>> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.createReservation(request);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation created successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reservation cancelled successfully"));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByCustomer(@PathVariable String customerId) {
        List<Reservation> reservations = reservationService.getReservationsByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByItem(@PathVariable Long itemId) {
        List<Reservation> reservations = reservationService.getReservationsByItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Reservation>> getReservation(@PathVariable Long id) {
        return reservationService.getReservation(id)
                .map(reservation -> ResponseEntity.ok(ApiResponse.success(reservation)))
                .orElse(ResponseEntity.notFound().build());
    }
} 