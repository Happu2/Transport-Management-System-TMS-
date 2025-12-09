package com.transport.transport.service;

import com.transport.transport.dto.bid.BidCreateRequest;
import com.transport.transport.entity.Bid;
import com.transport.transport.entity.Load;
import com.transport.transport.entity.Transporter;
import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.entity.enums.TruckType;
import com.transport.transport.exceptions.InsufficientCapacityException;
import com.transport.transport.exceptions.InvalidStatusTransitionException;
import com.transport.transport.repository.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    private BidRepository bidRepository;
    @Mock
    private LoadService loadService;
    @Mock
    private TransporterService transporterService;

    @InjectMocks
    private BidService bidService;

    private Load load;
    private Transporter transporter;
    private UUID bidId;

    @BeforeEach
    void setUp() {
        load = Load.builder()
                .loadId(UUID.randomUUID())
                .truckType(TruckType.OPEN)
                .status(LoadStatus.POSTED)
                .build();

        transporter = Transporter.builder().transporterId(UUID.randomUUID()).build();
        bidId = UUID.randomUUID();
    }

    @Test
    void create_shouldSucceedAndTransitionLoadStatus() {
        BidCreateRequest req = new BidCreateRequest(
                load.getLoadId(), transporter.getTransporterId(), TruckType.OPEN, 2, 100.0);

        when(loadService.find(load.getLoadId())).thenReturn(load);
        when(transporterService.find(transporter.getTransporterId())).thenReturn(transporter);
        when(transporterService.getAvailableCount(transporter, TruckType.OPEN)).thenReturn(5);
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));

        bidService.create(req);

        // Rule 2 check: POSTED -> OPEN_FOR_BIDS
        assertEquals(LoadStatus.OPEN_FOR_BIDS, load.getStatus());
        verify(bidRepository, times(1)).save(any(Bid.class));
    }

    @Test
    void create_shouldFail_whenTruckTypeMismatch() {
        BidCreateRequest req = new BidCreateRequest(
                load.getLoadId(), transporter.getTransporterId(), TruckType.CONTAINER, 1, 100.0);
        when(loadService.find(load.getLoadId())).thenReturn(load);

        assertThrows(InvalidStatusTransitionException.class, () -> bidService.create(req));
        verify(bidRepository, never()).save(any());
    }

    @Test
    void create_shouldFail_whenInsufficientCapacity() {
        BidCreateRequest req = new BidCreateRequest(
                load.getLoadId(), transporter.getTransporterId(), TruckType.OPEN, 6, 100.0);

        when(loadService.find(load.getLoadId())).thenReturn(load);
        when(transporterService.find(transporter.getTransporterId())).thenReturn(transporter);
        when(transporterService.getAvailableCount(transporter, TruckType.OPEN)).thenReturn(5); // Only 5 available

        assertThrows(InsufficientCapacityException.class, () -> bidService.create(req));
        verify(bidRepository, never()).save(any());
    }

    @Test
    void rejectBid_shouldSucceedForPendingBid() {
        Bid bid = Bid.builder().bidId(bidId).status(BidStatus.PENDING).build();
        when(bidRepository.findById(bidId)).thenReturn(Optional.of(bid));

        bidService.rejectBid(bidId);

        assertEquals(BidStatus.REJECTED, bid.getStatus());
        verify(bidRepository, times(1)).save(bid);
    }

    @Test
    void rejectBid_shouldFailForAcceptedBid() {
        Bid bid = Bid.builder().bidId(bidId).status(BidStatus.ACCEPTED).build();
        when(bidRepository.findById(bidId)).thenReturn(Optional.of(bid));

        assertThrows(InvalidStatusTransitionException.class, () -> bidService.rejectBid(bidId));
        verify(bidRepository, never()).save(any());
    }
}