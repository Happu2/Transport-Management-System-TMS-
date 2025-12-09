// BidResponse.java
package com.transport.transport.dto.bid;



import com.transport.transport.entity.Bid;
import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.entity.enums.TruckType;

import java.util.UUID;

public record BidResponse(
        UUID bidId,
        UUID loadId,
        UUID transporterId,
        TruckType truckType,
        int trucksOffered,
        double proposedRate,
        BidStatus status
) {
    public static BidResponse from(Bid entity) {
        return new BidResponse(
                entity.getBidId(),
                entity.getLoad().getLoadId(),
                entity.getTransporter().getTransporterId(),
                entity.getTruckType(),
                entity.getTrucksOffered(),
                entity.getProposedRate(),
                entity.getStatus()
        );
    }
}
