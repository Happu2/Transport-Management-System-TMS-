// BidCreateRequest.java
package com.transport.transport.dto.bid;


import com.transport.transport.entity.enums.TruckType;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record BidCreateRequest(
        @NotNull UUID loadId,
        @NotNull UUID transporterId,
        @NotNull TruckType truckType,
        @Min(1) int trucksOffered,
        @Positive double proposedRate
) {}
