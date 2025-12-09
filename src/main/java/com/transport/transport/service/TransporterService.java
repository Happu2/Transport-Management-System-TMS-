// TransporterService.java
package com.transport.transport.service;


import com.transport.transport.dto.transporter.TransporterCreateRequest;
import com.transport.transport.dto.transporter.TransporterResponse;
import com.transport.transport.entity.AvailableTruck;
import com.transport.transport.entity.Transporter;
import com.transport.transport.entity.enums.TruckType;
import com.transport.transport.exceptions.ResourceNotFoundException;
import com.transport.transport.repository.TransporterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TransporterService {

    private final TransporterRepository transporterRepository;

    @Transactional
    public TransporterResponse create(TransporterCreateRequest req) {
        // 1. Map DTOs to transient AvailableTruck entities
        List<AvailableTruck> trucks = req.availableTrucks().stream()
                .map(t -> AvailableTruck.builder()
                        .truckType(t.truckType())
                        .count(t.count())
                        .build())
                .toList();

        // 2. Build the Transporter entity
        Transporter t = Transporter.builder()
                .companyName(req.companyName())
                .rating(req.rating())
                .availableTrucks(trucks) // Set the list of transient trucks
                .build();

        // 3. ⭐ CRITICAL FIX: Set the back-reference (parent on the child)
        if (t.getAvailableTrucks() != null) {
            for (AvailableTruck truck : t.getAvailableTrucks()) {
                // This prevents TransientPropertyValueException
                truck.setTransporter(t);
            }
        }

        // 4. Save the Transporter (Hibernate cascades save to AvailableTrucks)
        return TransporterResponse.from(transporterRepository.save(t));
    }

    public TransporterResponse get(UUID id) {
        return TransporterResponse.from(find(id));
    }

    @Transactional
    public void updateTrucks(UUID id, TruckType type, int count) {
        Transporter t = find(id);
        // It's safer to operate on a mutable copy of the collection
        List<AvailableTruck> list = new ArrayList<>(t.getAvailableTrucks());

        boolean found = false;
        for (AvailableTruck at : list) {
            if (at.getTruckType() == type) {
                at.setCount(count);
                found = true;
                break;
            }
        }
        if (!found) {
            // New truck created, MUST set the back-reference here too
            AvailableTruck newTruck = AvailableTruck.builder().truckType(type).count(count).build();
            newTruck.setTransporter(t); // ⭐ Added for updates
            list.add(newTruck);
        }
        t.setAvailableTrucks(list);
        transporterRepository.save(t);
    }

    public Transporter find(UUID id) {
        return transporterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));
    }

    public int getAvailableCount(Transporter t, TruckType type) {
        return t.getAvailableTrucks().stream()
                .filter(a -> a.getTruckType() == type)
                .mapToInt(AvailableTruck::getCount)
                .sum();
    }

    @Transactional
    public void deductTrucks(Transporter t, TruckType type, int count) {
        List<AvailableTruck> list = new ArrayList<>(t.getAvailableTrucks());
        for (AvailableTruck at : list) {
            if (at.getTruckType() == type) {
                if (at.getCount() < count) {
                    throw new com.transport.transport.exceptions.InsufficientCapacityException("Transporter does not have enough trucks");

                }
                at.setCount(at.getCount() - count);
            }
        }
        t.setAvailableTrucks(list);
        transporterRepository.save(t);
    }

    @Transactional
    public void restoreTrucks(Transporter t, TruckType type, int count) {
        List<AvailableTruck> list = new ArrayList<>(t.getAvailableTrucks());
        boolean found = false;
        for (AvailableTruck at : list) {
            if (at.getTruckType() == type) {
                at.setCount(at.getCount() + count);
                found = true;
            }
        }
        if (!found) {
            // New truck created, MUST set the back-reference here too
            AvailableTruck newTruck = AvailableTruck.builder().truckType(type).count(count).build();
            newTruck.setTransporter(t); // ⭐ Added for restores
            list.add(newTruck);
        }
        t.setAvailableTrucks(list);
        transporterRepository.save(t);
    }
}