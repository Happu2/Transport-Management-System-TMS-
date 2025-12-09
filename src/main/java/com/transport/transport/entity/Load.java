package com.transport.transport.entity;

import com.transport.transport.entity.enums.LoadStatus;
import com.transport.transport.entity.enums.TruckType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Load {

    @Id
    @GeneratedValue
    private UUID loadId;

    @Column(nullable = false)
    private UUID shipperId;   // <-- FIXED: UUID not String

    private String loadingCity;
    private String unloadingCity;

    @Enumerated(EnumType.STRING)
    private TruckType truckType;

    private int noOfTrucks;
    private int remainingTrucks; // <-- Add this for partial booking logic

    private double weight;

    @Enumerated(EnumType.STRING)
    private LoadStatus status;

    private LocalDate loadingDate;
    private LocalDate datePosted;

    @Version
    private Long version;
}
