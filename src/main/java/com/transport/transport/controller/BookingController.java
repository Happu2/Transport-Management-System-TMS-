package com.transport.transport.controller;

import com.transport.transport.dto.booking.BookingCreateRequest;
import com.transport.transport.dto.booking.BookingResponse;
import com.transport.transport.entity.Bid;
import com.transport.transport.service.BidService;
import com.transport.transport.service.BookingService;
import com.transport.transport.service.LoadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 // Import for Rule 4 handling

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BidService bidService;
    private final LoadService loadService;

    // ⭐ Rule 4: Optimistic Lock Failure is handled by GlobalExceptionHandler mapping
    // ObjectOptimisticLockingFailureException to LoadAlreadyBookedException
    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingCreateRequest req) {
        Bid acceptedBid = bidService.find(req.bidId());

        // NOTE: Load logic moved to service. We just pass the Bid.
        return ResponseEntity.ok(bookingService.createBooking(acceptedBid));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> listByShipper(@RequestParam UUID shipperId) {
        return ResponseEntity.ok(bookingService.listByShipper(shipperId));
    }

    // ⭐ NEW: PATCH /booking/{bookingId}/cancel implementation (API Requirement)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully, trucks restored");
    }

}