package com.transport.transport.dto.load;

import com.transport.transport.entity.enums.TruckType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record LoadCreateRequest(
        @NotNull UUID shipperId,
        @NotBlank String loadingCity,
        @NotBlank String unloadingCity,
        @NotNull TruckType truckType,
        @Min(1) int noOfTrucks,
        @Min(1) double weight,
        @NotNull LocalDate loadingDate
) {}
