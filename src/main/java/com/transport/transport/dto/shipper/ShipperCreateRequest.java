package com.transport.transport.dto.shipper;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ShipperCreateRequest(
        @NotBlank String companyName,
        @NotBlank String contactName,
        @Email String email
) {}
