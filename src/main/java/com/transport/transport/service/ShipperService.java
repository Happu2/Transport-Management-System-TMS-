package com.transport.transport.service;

import com.transport.transport.dto.shipper.*;
import com.transport.transport.entity.Shipper;
import com.transport.transport.exceptions.ResourceNotFoundException;
import com.transport.transport.repository.ShipperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipperService {

    private final ShipperRepository repo;

    @Transactional
    public ShipperResponse create(ShipperCreateRequest req) {
        Shipper s = Shipper.builder()
                .companyName(req.companyName())
                .contactName(req.contactName())
                .email(req.email())
                .build();
        return ShipperResponse.from(repo.save(s));
    }

    public ShipperResponse get(UUID id) {
        return ShipperResponse.from(find(id));
    }

    public Shipper find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found"));
    }
}
