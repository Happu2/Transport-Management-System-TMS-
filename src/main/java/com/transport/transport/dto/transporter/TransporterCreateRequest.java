// TransporterCreateRequest.java
package com.transport.transport.dto.transporter;


import com.transport.transport.entity.enums.TruckType;
import jakarta.validation.constraints.*;

import java.util.List;

public record TransporterCreateRequest(
        @NotBlank String companyName,
        @Min(0) @Max(5) double rating,
        @Size(min = 1) List<TruckRequest> availableTrucks
) {
    public record TruckRequest(
            @NotNull TruckType truckType,
            @Min(1) int count
    ) {}
}
