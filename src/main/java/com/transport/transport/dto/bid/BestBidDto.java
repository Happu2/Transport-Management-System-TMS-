package com.transport.transport.dto.bid;

import java.util.UUID;

public record BestBidDto(
        UUID bidId,
        UUID transporterId,
        double proposedRate,
        int trucksOffered
) {}
