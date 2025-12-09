// Bid.java
package com.transport.transport.entity;


import com.transport.transport.entity.enums.BidStatus;
import com.transport.transport.entity.enums.TruckType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Bid {

    @Id
    @GeneratedValue
    private UUID bidId;

    @ManyToOne(optional = false)
    private com.transport.transport.entity.Load load;

    @ManyToOne(optional = false)
    private Transporter transporter;

    private double proposedRate;

    @Enumerated(EnumType.STRING)
    private TruckType truckType;

    private int trucksOffered;

    @Enumerated(EnumType.STRING)
    private BidStatus status;

    @Version
    private Long version;
}
