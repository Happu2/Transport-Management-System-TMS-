package com.transport.transport.entity;

import com.transport.transport.entity.enums.LoadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue
    private UUID bookingId;

    @OneToOne(optional = false)
    @JoinColumn(name = "accepted_bid_id", nullable = false)
    private Bid acceptedBid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "load_id", nullable = false)
    private Load load;

    @Column(nullable = false)
    private int allocatedTrucks;

    @Enumerated(EnumType.STRING)
    @Column(name = "load_status", nullable = false)
    private LoadStatus loadStatus;

    @Column(nullable = false)
    private UUID shipperId; // FIXED TYPE

    @Column(nullable = false)
    private UUID transporterId; // FIXED TYPE

    @Enumerated(EnumType.STRING)
    @Column(name = "truck_type")
    private com.transport.transport.entity.enums.TruckType truckType;

    @Version
    private Long version;
}
