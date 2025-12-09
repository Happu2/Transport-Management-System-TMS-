// LoadService.java
package com.transport.transport.service;


import com.transport.transport.dto.load.LoadCreateRequest;
import com.transport.transport.dto.load.LoadResponse;
import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.exceptions.InvalidStatusTransitionException;
import com.transport.transport.exceptions.ResourceNotFoundException;
import com.transport.transport.repository.LoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoadService {

    private final LoadRepository loadRepository;

    @Transactional
    public LoadResponse createLoad(LoadCreateRequest req) {
        com.transport.transport.entity.Load load = com.transport.transport.entity.Load.builder()
                .shipperId(req.shipperId())
                .loadingCity(req.loadingCity())
                .unloadingCity(req.unloadingCity())
                .truckType(req.truckType())
                .noOfTrucks(req.noOfTrucks())
                .remainingTrucks(req.noOfTrucks()) // Initialize remaining trucks
                .weight(req.weight())
                .status(LoadStatus.POSTED)
                .loadingDate(req.loadingDate())
                .datePosted(LocalDate.now())
                .build();

        return LoadResponse.from(loadRepository.save(load));
    }

    // ⭐ CHANGE: Changed String to UUID for type consistency
    public Page<LoadResponse> getLoads(UUID shipperId, LoadStatus status, Pageable pageable) {
        Page<com.transport.transport.entity.Load> page;
        if (shipperId != null && status != null) {
            page = loadRepository.findByShipperIdAndStatus(shipperId, status, pageable);
        } else if (shipperId != null) {
            page = loadRepository.findByShipperId(shipperId, pageable);
        } else if (status != null) {
            page = loadRepository.findByStatus(status, pageable);
        } else {
            page = loadRepository.findAll(pageable);
        }
        return page.map(LoadResponse::from);
    }

    public LoadResponse getLoad(UUID id) {
        return LoadResponse.from(find(id));
    }

    @Transactional
    public void cancelLoad(UUID id) {
        com.transport.transport.entity.Load load = find(id);
        if (load.getStatus() == LoadStatus.BOOKED) {
            throw new InvalidStatusTransitionException("Cannot cancel a booked load");
        }
        load.setStatus(LoadStatus.CANCELLED);
        loadRepository.save(load);
    }

    public com.transport.transport.entity.Load find(UUID id) {
        return loadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));
    }
}