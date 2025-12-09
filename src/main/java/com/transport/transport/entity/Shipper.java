// com/transport/transport/entity/Shipper.java
package com.transport.transport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Shipper {

    @Id
    @GeneratedValue
    private UUID shipperId;

    private String companyName;
    private String contactName;
    private String email;

    @Version
    private Long version;
}
