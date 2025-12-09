// BidController.java
package com.transport.transport.controller;

import com.transport.transport.dto.bid.BidResponse;
import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponse> create(@RequestBody com.transport.transport.dto.bid.BidCreateRequest req) {
        return ResponseEntity.ok(bidService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BidResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(bidService.get(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable UUID id) {
        bidService.rejectBid(id);
        return ResponseEntity.ok("Bid rejected successfully");
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<String> accept(@PathVariable UUID id) {
        bidService.acceptBid(id);
        return ResponseEntity.ok("Bid accepted successfully");
    }

    // ⬇️ NEW ENDPOINT FOR FILTER SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<BidResponse>> search(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) UUID transporterId,
            @RequestParam(required = false) BidStatus status
    ) {
        return ResponseEntity.ok(bidService.getFilteredBids(loadId, transporterId, status));
    }
}
