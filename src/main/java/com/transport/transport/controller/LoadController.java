package com.transport.transport.controller;

import com.transport.transport.dto.bid.BestBidDto;
import com.transport.transport.dto.load.LoadCreateRequest;
import com.transport.transport.dto.load.LoadResponse;
import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.service.BidService;
import com.transport.transport.service.LoadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/load")
@RequiredArgsConstructor
public class LoadController {

    private final LoadService loadService;
    private final BidService bidService;

    @PostMapping
    public ResponseEntity<LoadResponse> create(@Valid @RequestBody LoadCreateRequest req) {
        return ResponseEntity.ok(loadService.createLoad(req));
    }

    @GetMapping
    public ResponseEntity<Page<LoadResponse>> list(
            // ⭐ CHANGE: Changed String to UUID for type consistency
            @RequestParam(required = false) UUID shipperId,
            @RequestParam(required = false) LoadStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePosted").descending());
        return ResponseEntity.ok(loadService.getLoads(shipperId, status, pageable));
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<LoadResponse> get(@PathVariable UUID loadId) {
        return ResponseEntity.ok(loadService.getLoad(loadId));
    }

    @PatchMapping("/{loadId}/cancel")
    public ResponseEntity<String> cancel(@PathVariable UUID loadId) {
        loadService.cancelLoad(loadId);
        return ResponseEntity.ok("Load cancelled successfully");
    }

    @GetMapping("/{loadId}/best-bids")
    public ResponseEntity<List<BestBidDto>> bestBids(@PathVariable UUID loadId) {
        return ResponseEntity.ok(bidService.getBestBids(loadId));
    }
}