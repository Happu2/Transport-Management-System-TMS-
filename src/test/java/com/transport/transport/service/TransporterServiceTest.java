package com.transport.transport.service;

import com.transport.transport.entity.AvailableTruck;
import com.transport.transport.entity.Transporter;
import com.transport.transport.entity.enums.TruckType;
import com.transport.transport.exceptions.InsufficientCapacityException;
import com.transport.transport.repository.TransporterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransporterServiceTest {

    @Mock
    private TransporterRepository transporterRepository;

    @InjectMocks
    private TransporterService transporterService;

    private Transporter transporter;
    private AvailableTruck openTrucks;
    private AvailableTruck containerTrucks;

    @BeforeEach
    void setUp() {
        openTrucks = AvailableTruck.builder().truckType(TruckType.OPEN).count(5).build();
        containerTrucks = AvailableTruck.builder().truckType(TruckType.CONTAINER).count(3).build();

        transporter = Transporter.builder()
                .transporterId(UUID.randomUUID())
                .availableTrucks(List.of(openTrucks, containerTrucks))
                .build();
    }

    @Test
    void getAvailableCount_shouldReturnCorrectCount() {
        assertEquals(5, transporterService.getAvailableCount(transporter, TruckType.OPEN));
        assertEquals(3, transporterService.getAvailableCount(transporter, TruckType.CONTAINER));
        assertEquals(0, transporterService.getAvailableCount(transporter, TruckType.TRAILER));
    }

    @Test
    void deductTrucks_shouldSucceedAndDeductCount() {
        transporterService.deductTrucks(transporter, TruckType.OPEN, 3);
        assertEquals(2, openTrucks.getCount());
        verify(transporterRepository, times(1)).save(transporter);
    }

    @Test
    void deductTrucks_shouldFailDueToInsufficientCapacity() {
        assertThrows(InsufficientCapacityException.class,
                () -> transporterService.deductTrucks(transporter, TruckType.CONTAINER, 5));
        assertEquals(3, containerTrucks.getCount());
        verify(transporterRepository, never()).save(any());
    }

    @Test
    void restoreTrucks_shouldSucceedAndRestoreCount() {
        openTrucks.setCount(2); // Start at 2
        transporterService.restoreTrucks(transporter, TruckType.OPEN, 3);
        assertEquals(5, openTrucks.getCount());
        verify(transporterRepository, times(1)).save(transporter);
    }

    @Test
    void restoreTrucks_shouldAddNewTruckTypeIfNotFound() {
        // TruckType.TRAILER not in the initial list
        transporterService.restoreTrucks(transporter, TruckType.TRAILER, 2);

        assertEquals(3, transporter.getAvailableTrucks().size());

        AvailableTruck newTruck = transporter.getAvailableTrucks().stream()
                .filter(t -> t.getTruckType() == TruckType.TRAILER)
                .findFirst()
                .orElseThrow();

        assertEquals(2, newTruck.getCount());
        verify(transporterRepository, times(1)).save(transporter);
    }
}