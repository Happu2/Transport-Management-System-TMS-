package com.transport.transport.service;

import com.transport.transport.dto.load.LoadCreateRequest;
import com.transport.transport.entity.Load;
import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.entity.enums.TruckType;
import com.transport.transport.exceptions.InvalidStatusTransitionException;
import com.transport.transport.exceptions.ResourceNotFoundException;
import com.transport.transport.repository.LoadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @InjectMocks
    private LoadService loadService;

    private UUID loadId;
    private Load load;

    @BeforeEach
    void setUp() {
        loadId = UUID.randomUUID();
        // Base load object used for find/cancel tests
        load = Load.builder()
                .loadId(loadId)
                .shipperId(UUID.randomUUID())
                .noOfTrucks(2)
                .remainingTrucks(2)
                .truckType(TruckType.CONTAINER) // Added TruckType to prevent potential DTO errors in other tests
                .status(LoadStatus.POSTED)
                .build();
    }

    @Test
    void createLoad_shouldSaveAndReturnLoad() {
        LoadCreateRequest req = new LoadCreateRequest(
                UUID.randomUUID(), "A", "B", TruckType.CONTAINER, 2, 10.0, LocalDate.now());

        // ⭐ FIX: Use thenAnswer to return the fully populated Load object passed to save().
        // This prevents the NullPointerException in LoadResponse.from() because the returned
        // object will have TruckType and other fields correctly set from the request.
        when(loadRepository.save(any(Load.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0); // Return the Load object passed to save()
        });

        assertNotNull(loadService.createLoad(req));
        verify(loadRepository, times(1)).save(any(Load.class));
    }

    @Test
    void cancelLoad_shouldSucceedForPostedLoad() {
        load.setStatus(LoadStatus.POSTED);
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));

        loadService.cancelLoad(loadId);

        assertEquals(LoadStatus.CANCELLED, load.getStatus());
        verify(loadRepository, times(1)).save(load);
    }

    @Test
    void cancelLoad_shouldFailForBookedLoad() {
        load.setStatus(LoadStatus.BOOKED);
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));

        assertThrows(InvalidStatusTransitionException.class, () -> loadService.cancelLoad(loadId));
        assertEquals(LoadStatus.BOOKED, load.getStatus());
        verify(loadRepository, never()).save(any());
    }

    @Test
    void find_shouldThrowResourceNotFound() {
        when(loadRepository.findById(loadId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loadService.find(loadId));
    }
}