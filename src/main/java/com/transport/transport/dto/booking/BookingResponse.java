package com.transport.transport.dto.booking;

import com.transport.transport.entity.Booking;
import com.transport.transport.entity.enums.TruckType;
import com.transport.transport.entity.enums.LoadStatus;

import java.util.UUID;

public record BookingResponse(
        UUID bookingId,
        UUID loadId,
        UUID bidId,
        UUID shipperId,       // ✔ FIXED
        UUID transporterId,   // ✔ already correct
        TruckType truckType,
        int allocatedTrucks,
        LoadStatus loadStatus
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getLoad().getLoadId(),
                booking.getAcceptedBid().getBidId(),
                booking.getShipperId(),       // returns UUID now
                booking.getTransporterId(),   // UUID
                booking.getTruckType(),
                booking.getAllocatedTrucks(),
                booking.getLoadStatus()
        );
    }
}
