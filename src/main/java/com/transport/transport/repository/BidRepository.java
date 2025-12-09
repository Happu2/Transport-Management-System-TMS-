package com.transport.transport.repository;

import com.transport.transport.entity.Bid;
import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.dto.bid.BestBidDto;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    @Modifying
    @Query("""
        update Bid b
        set b.status = 'REJECTED'
        where b.load.loadId = :loadId
          and b.bidId <> :acceptedBidId
    """)
    void rejectOtherBids(@Param("loadId") UUID loadId,
                         @Param("acceptedBidId") UUID acceptedBidId);

    // This query is optimized for DTO projection and sorting, not susceptible to the standard N+1 issue.
    @Query("""
        select new com.transport.transport.dto.bid.BestBidDto(
            b.bidId,
            b.transporter.transporterId,
            b.proposedRate,
            b.trucksOffered
        )
        from Bid b
        where b.load.loadId = :loadId
          and b.status = com.transport.transport.entity.enums.BidStatus.PENDING
        order by ((1 / b.proposedRate) * 0.7 + (b.transporter.rating / 5) * 0.3) desc
    """)
    List<BestBidDto> findBestBids(@Param("loadId") UUID loadId);

    // ⭐ REWRITTEN FOR N+1 OPTIMIZATION:
    // Uses JOIN FETCH to load the 'load' and 'transporter' entities in a single query.
    // This prevents separate queries from running when BidResponse.from() is called.
    @Query("""
        select b from Bid b 
        join fetch b.load 
        join fetch b.transporter
        where (:loadId is null or b.load.loadId = :loadId)
          and (:transporterId is null or b.transporter.transporterId = :transporterId)
          and (:status is null or b.status = :status)
    """)
    List<Bid> search(@Param("loadId") UUID loadId,
                     @Param("transporterId") UUID transporterId,
                     @Param("status") BidStatus status);
}