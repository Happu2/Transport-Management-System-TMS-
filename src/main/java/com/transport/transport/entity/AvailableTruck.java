// com/transport/transport/entity/AvailableTruck.java
package com.transport.transport.entity;

import com.transport.transport.entity.enums.TruckType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AvailableTruck {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TruckType truckType;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;
}
