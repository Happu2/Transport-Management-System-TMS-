// Transporter.java
package com.transport.transport.entity;

import com.transport.transport.entity.AvailableTruck;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Transporter {

    @Id
    @GeneratedValue
    private UUID transporterId;

    private String companyName;
    private double rating;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<AvailableTruck> availableTrucks;

    @Version
    private Long version;
}
