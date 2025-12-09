package com.transport.transport.dto.shipper;

import com.transport.transport.entity.Shipper;

import java.util.UUID;

public record ShipperResponse(
        UUID shipperId,
        String companyName,
        String contactName,
        String email
) {
    public static ShipperResponse from(Shipper s) {
        return new ShipperResponse(
                s.getShipperId(),
                s.getCompanyName(),
                s.getContactName(),
                s.getEmail()
        );
    }
}
