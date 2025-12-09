// TransporterResponse.java
package com.transport.transport.dto.transporter;



import com.transport.transport.entity.Transporter;
import com.transport.transport.entity.enums.TruckType;

import java.util.List;
import java.util.UUID;

public record TransporterResponse(
        UUID transporterId,
        String companyName,
        double rating,
        List<TruckDto> availableTrucks
) {
    public static TransporterResponse from(Transporter entity) {
        return new TransporterResponse(
                entity.getTransporterId(),
                entity.getCompanyName(),
                entity.getRating(),
                entity.getAvailableTrucks().stream()
                        .map(t -> new TruckDto(t.getTruckType(), t.getCount()))
                        .toList()
        );
    }

    public record TruckDto(TruckType truckType, int count) {}
}
