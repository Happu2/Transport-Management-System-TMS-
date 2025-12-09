package com.transport.transport.controller;

import com.transport.transport.dto.shipper.*;
import com.transport.transport.service.ShipperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipper")
@RequiredArgsConstructor
public class ShipperController {

    private final ShipperService shipperService;

    @PostMapping
    public ResponseEntity<ShipperResponse> create(@Valid @RequestBody ShipperCreateRequest req) {
        return ResponseEntity.ok(shipperService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipperResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(shipperService.get(id));
    }
}
