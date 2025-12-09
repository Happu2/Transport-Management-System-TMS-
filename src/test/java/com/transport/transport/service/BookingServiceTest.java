package com.transport.transport.service;

import com.transport.transport.entity.Bid;
import com.transport.transport.entity.Load;
import com.transport.transport.entity.Transporter;
import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.entity.enums.TruckType;
import com.transport.transport.exceptions.InsufficientCapacityException;
import com.transport.transport.exceptions.InvalidStatusTransitionException;
import com.transport.transport.repository.BidRepository;
import com.transport.transport.repository.BookingRepository;
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
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TransporterService transporterService;
    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private BookingService bookingService;

    private Load load;
    private Transporter transporter;
    private Bid bid;
    private UUID bookingId;

    @BeforeEach
    void setUp() {
        transporter = Transporter.builder().transporterId(UUID.randomUUID()).build();

        load = Load.builder()
                .loadId(UUID.randomUUID())
                .shipperId(UUID.randomUUID())
                .noOfTrucks(5)
                .remainingTrucks(5)
                .truckType(TruckType.OPEN)
                .status(LoadStatus.OPEN_FOR_BIDS)
                .build();

        bid = Bid.builder()
                .bidId(UUID.randomUUID())
                .load(load)
                .transporter(transporter)
                .trucksOffered(3)
                .truckType(TruckType.OPEN)
                .status(BidStatus.PENDING)
                .build();

        bookingId = UUID.randomUUID();
    }

    @Test
    void createBooking_shouldSucceed_PartialAllocation() {
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        bookingService.createBooking(bid);

        // Rule 3: Multi-Truck Allocation check
        assertEquals(2, load.getRemainingTrucks()); // 5 - 3 = 2
        assertEquals(LoadStatus.OPEN_FOR_BIDS, load.getStatus()); // Should not be BOOKED yet
        assertEquals(BidStatus.ACCEPTED, bid.getStatus());

        verify(transporterService, times(1)).deductTrucks(transporter, TruckType.OPEN, 3);
        verify(bidRepository, never()).rejectOtherBids(any(), any()); // Not fully booked
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_shouldSucceed_FullAllocation() {
        bid.setTrucksOffered(5); // Allocate all 5 trucks
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        bookingService.createBooking(bid);

        // Rule 3 & 2 check: Full allocation results in BOOKED status
        assertEquals(0, load.getRemainingTrucks());
        assertEquals(LoadStatus.BOOKED, load.getStatus());

        verify(transporterService, times(1)).deductTrucks(transporter, TruckType.OPEN, 5);
        verify(bidRepository, times(1)).rejectOtherBids(any(), any()); // Should reject others
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_shouldFail_InsufficientLoadCapacity() {
        bid.setTrucksOffered(6); // Load only has 5

        assertThrows(InsufficientCapacityException.class, () -> bookingService.createBooking(bid));

        assertEquals(5, load.getRemainingTrucks());
        verify(transporterService, never()).deductTrucks(any(), any(), anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_shouldSucceed_AndRestoreTrucks() {
        // Setup a confirmed booking for cancellation
        com.transport.transport.entity.Booking booking = com.transport.transport.entity.Booking.builder()
                .bookingId(bookingId)
                .load(load)
                .acceptedBid(bid)
                .allocatedTrucks(3)
                .truckType(TruckType.OPEN)
                .loadStatus(LoadStatus.OPEN_FOR_BIDS)
                .build();

        load.setRemainingTrucks(2); // Load was partially filled

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(bookingId);

        // Rule 1 check: Trucks restored
        verify(transporterService, times(1)).restoreTrucks(transporter, TruckType.OPEN, 3);

        // Rule 3 check: Load capacity restored
        assertEquals(5, load.getRemainingTrucks()); // 2 + 3 = 5

        // Final Status check
        assertEquals(LoadStatus.CANCELLED, booking.getLoadStatus());
        assertEquals(LoadStatus.OPEN_FOR_BIDS, load.getStatus()); // Load status remains OPEN_FOR_BIDS
        verify(bookingRepository, times(1)).save(booking);
    }
}