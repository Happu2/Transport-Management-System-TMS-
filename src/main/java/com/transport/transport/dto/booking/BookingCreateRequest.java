package com.transport.transport.dto.booking;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingCreateRequest(
        @NotNull UUID bidId
) {}
