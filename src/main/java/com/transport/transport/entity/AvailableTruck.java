// AvailableTruck.java (Assuming this is your structure)
package com.transport.transport.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transport.transport.entity.enums.TruckType;
import jakarta.persistence.*;
import lombok.*;

// Assuming this entity has its own ID and is mapped to the transporter

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AvailableTruck {

    @Id
    @GeneratedValue
    private Long id; // Assuming a Long ID for simplicity

    @Enumerated(EnumType.STRING)
    private TruckType truckType;

    private int count;

    // ⭐ FIX: Add the required @ManyToOne back-reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id") // This column will hold the Foreign Key
    @JsonIgnore // Already added for Swagger fix
    private Transporter transporter;

    // ... other fields
}