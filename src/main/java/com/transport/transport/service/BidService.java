package com.transport.transport.service;

import com.transport.transport.dto.bid.BidCreateRequest;
import com.transport.transport.dto.bid.BidResponse;
import com.transport.transport.dto.bid.BestBidDto;
import com.transport.transport.entity.Bid;
import com.transport.transport.entity.Transporter;
import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.exceptions.InsufficientCapacityException;
import com.transport.transport.exceptions.InvalidStatusTransitionException;
import com.transport.transport.exceptions.ResourceNotFoundException;
import com.transport.transport.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final LoadService loadService;
    private final TransporterService transporterService;

    @Transactional
    public BidResponse create(BidCreateRequest req) {
        var load = loadService.find(req.loadId());

        if (load.getStatus() == LoadStatus.CANCELLED || load.getStatus() == LoadStatus.BOOKED) {
            throw new InvalidStatusTransitionException("Cannot bid on cancelled or fully booked load");
        }
        if (load.getTruckType() != req.truckType()) {
            throw new InvalidStatusTransitionException("Bid truckType must match Load truckType");
        }

        Transporter transporter = transporterService.find(req.transporterId());

        int available = transporterService.getAvailableCount(transporter, req.truckType());
        if (available < req.trucksOffered()) {
            throw new InsufficientCapacityException("Transporter does not have enough trucks available for this type");
        }

        // ⭐ RULE 2: POSTED -> OPEN_FOR_BIDS transition
        if (load.getStatus() == LoadStatus.POSTED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
        }

        Bid bid = Bid.builder()
                .load(load)
                .transporter(transporter)
                .truckType(req.truckType())
                .trucksOffered(req.trucksOffered())
                .proposedRate(req.proposedRate())
                .status(BidStatus.PENDING)
                .build();

        return BidResponse.from(bidRepository.save(bid));
    }

    public BidResponse get(UUID id) {
        return BidResponse.from(find(id));
    }

    public Bid find(UUID id) {
        return bidRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
    }

    @Transactional
    public void rejectBid(UUID id) {
        Bid bid = find(id);
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending bids can be rejected");
        }
        bid.setStatus(BidStatus.REJECTED);
        bidRepository.save(bid);
    }

    // ⭐ REMOVED acceptBid logic. Acceptance is now part of the POST /booking process.
    // However, keeping this method if required by other parts, but removing Load status change.
    @Transactional
    public void acceptBid(UUID id) {
        Bid bid = find(id);
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending bids can be accepted");
        }

        // NOTE: Load status change and other complex logic moved to BookingService.createBooking
        // This method is now effectively just setting the bid status to ACCEPTED.
        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);
    }

    public List<BidResponse> getFilteredBids(UUID loadId, UUID transporterId, BidStatus status) {
        return bidRepository.search(loadId, transporterId, status)
                .stream()
                .map(BidResponse::from)
                .toList();
    }

    public List<BestBidDto> getBestBids(UUID loadId) {
        return bidRepository.findBestBids(loadId);
    }
}