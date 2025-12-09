package com.transport.transport.dto.transporter;

import com.transport.transport.entity.enums.TruckType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TruckUpdateRequest(
        @NotNull TruckType type,
        @Min(1) int count
) {}
