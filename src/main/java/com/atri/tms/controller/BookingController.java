package com.atri.tms.controller;

import com.atri.tms.dto.BookingRequest;
import com.atri.tms.entity.Booking;
import com.atri.tms.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // POST /booking → accept bid & create booking
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    // GET /booking/{bookingId}
    @GetMapping("/{bookingId}")
    public Booking getBooking(@PathVariable UUID bookingId) {
        return bookingService.getBooking(bookingId);
    }

    // PATCH /booking/{bookingId}/cancel
    @PatchMapping("/{bookingId}/cancel")
    public Booking cancelBooking(@PathVariable UUID bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}
