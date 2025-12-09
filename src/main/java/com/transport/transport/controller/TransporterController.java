package com.transport.transport.controller;

import com.transport.transport.dto.transporter.TransporterCreateRequest;
import com.transport.transport.dto.transporter.TransporterResponse;
import com.transport.transport.dto.transporter.TruckUpdateRequest;
import com.transport.transport.service.TransporterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transporter")
@RequiredArgsConstructor
public class TransporterController {

    private final TransporterService transporterService;

    @PostMapping
    public ResponseEntity<TransporterResponse> create(@Valid @RequestBody TransporterCreateRequest req) {
        return ResponseEntity.ok(transporterService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransporterResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(transporterService.get(id));
    }

    // ⭐ FIXED: Changed to @PutMapping as per API requirement (3. PUT /transporter/{transporterId}/trucks)
    @PutMapping("/{id}/trucks")
    public ResponseEntity<String> updateTrucks(
            @PathVariable UUID id,
            @Valid @RequestBody TruckUpdateRequest req
    ) {
        transporterService.updateTrucks(id, req.type(), req.count());
        return ResponseEntity.ok("Truck capacity updated successfully");
    }
}