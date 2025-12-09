package com.transport.transport.dto.load;

import com.transport.transport.entity.Load;

import java.util.UUID;

public record LoadResponse(
        UUID loadId,
        UUID shipperId,
        int noOfTrucks,
        int remainingTrucks,   // ⭐ SHOW IN API
        String loadingCity,
        String unloadingCity,
        String truckType,
        double weight,
        String status
) {
    public static LoadResponse from(Load load) {
        return new LoadResponse(
                load.getLoadId(),
                load.getShipperId(),
                load.getNoOfTrucks(),
                load.getRemainingTrucks(),
                load.getLoadingCity(),
                load.getUnloadingCity(),
                load.getTruckType().name(),
                load.getWeight(),
                load.getStatus().name()
        );
    }
}
