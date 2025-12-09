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
        List<AvailableTruck> trucks = req.availableTrucks().stream()
                .map(t -> AvailableTruck.builder()
                        .truckType(t.truckType())
                        .count(t.count())
                        .build())
                .toList();

        Transporter t = Transporter.builder()
                .companyName(req.companyName())
                .rating(req.rating())
                .availableTrucks(trucks)
                .build();

        return TransporterResponse.from(transporterRepository.save(t));
    }

    public TransporterResponse get(UUID id) {
        return TransporterResponse.from(find(id));
    }

    @Transactional
    public void updateTrucks(UUID id, TruckType type, int count) {
        Transporter t = find(id);
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
            list.add(AvailableTruck.builder().truckType(type).count(count).build());
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
            list.add(AvailableTruck.builder().truckType(type).count(count).build());
        }
        t.setAvailableTrucks(list);
        transporterRepository.save(t);
    }
}
