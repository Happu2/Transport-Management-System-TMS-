package com.transport.transport.service;

import com.transport.transport.dto.booking.BookingResponse;
import com.transport.transport.entity.Booking;
import com.transport.transport.entity.Bid;
import com.transport.transport.entity.Load;
import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.exceptions.InsufficientCapacityException;
import com.transport.transport.exceptions.InvalidStatusTransitionException;
import com.transport.transport.exceptions.ResourceNotFoundException;
import com.transport.transport.repository.BidRepository;
import com.transport.transport.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TransporterService transporterService;
    private final BidRepository bidRepository;

    @Transactional
    public BookingResponse createBooking(Bid acceptedBid) {
        Load load = acceptedBid.getLoad();
        int trucksToAllocate = acceptedBid.getTrucksOffered();

        // 1. Initial Status Check
        if (acceptedBid.getStatus() != BidStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only PENDING bids can be accepted for a booking");
        }
        if (load.getStatus() == LoadStatus.CANCELLED || load.getStatus() == LoadStatus.BOOKED) {
            throw new InvalidStatusTransitionException("Load is not available for booking");
        }

        // 2. Load Capacity Check (Rule 3)
        if (load.getRemainingTrucks() < trucksToAllocate) {
            throw new InsufficientCapacityException("Requested trucks (" + trucksToAllocate + ") exceeds remaining load capacity (" + load.getRemainingTrucks() + ")");
        }

        // 3. Deduct Transporter Trucks (Rule 1)
        // This throws InsufficientCapacityException if transporter capacity is insufficient
        transporterService.deductTrucks(acceptedBid.getTransporter(), acceptedBid.getTruckType(), trucksToAllocate);

        // 4. Update Load and Bid Status
        load.setRemainingTrucks(load.getRemainingTrucks() - trucksToAllocate);
        acceptedBid.setStatus(BidStatus.ACCEPTED);

        // 5. Check for Full Booking (Rule 3 & 2)
        if (load.getRemainingTrucks() == 0) {
            load.setStatus(LoadStatus.BOOKED);
            // RULE 2: Reject other bids only when fully booked
            bidRepository.rejectOtherBids(load.getLoadId(), acceptedBid.getBidId());
        }

        // 6. Create Booking Entity
        Booking booking = Booking.builder()
                .load(load)
                .acceptedBid(acceptedBid)
                .allocatedTrucks(trucksToAllocate)
                .shipperId(load.getShipperId())
                .transporterId(acceptedBid.getTransporter().getTransporterId())
                .truckType(load.getTruckType())
                .loadStatus(load.getStatus()) // Use the potentially updated status (BOOKED or OPEN_FOR_BIDS)
                .build();

        // Load and Bid save is handled implicitly by @Transactional and load/bid being managed entities,
        // which includes the optimistic lock check on Load (@Version) (Rule 4).
        return BookingResponse.from(bookingRepository.save(booking));
    }


    public BookingResponse get(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return BookingResponse.from(booking);
    }

    public List<BookingResponse> listByShipper(UUID shipperId) {
        return bookingRepository.findByShipperId(shipperId)
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    // ⭐ NEW: PATCH /booking/{bookingId}/cancel implementation
    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getLoadStatus() == LoadStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Booking is already cancelled");
        }

        Load load = booking.getLoad();

        // 1. Restore Trucks (Rule 1)
        transporterService.restoreTrucks(
                booking.getAcceptedBid().getTransporter(),
                booking.getTruckType(),
                booking.getAllocatedTrucks()
        );

        // 2. Update Load Status/Remaining Trucks (Rule 3)
        load.setRemainingTrucks(load.getRemainingTrucks() + booking.getAllocatedTrucks());

        // If load was fully BOOKED, move it back to OPEN_FOR_BIDS
        if (load.getStatus() == LoadStatus.BOOKED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
        }

        // 3. Update Booking Status
        booking.setLoadStatus(LoadStatus.CANCELLED);
        bookingRepository.save(booking);

        // NOTE: The accepted Bid associated with this booking remains ACCEPTED,
        // but the load is now OPEN_FOR_BIDS again for the unfulfilled truck quantity.
    }

}