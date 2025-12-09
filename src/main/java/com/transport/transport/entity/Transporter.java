// Transporter.java (Entity)
package com.transport.transport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList; // NEW IMPORT
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

    // ⭐ FIX: Change to @OneToMany for proper entity relationship management
    // CascadeType.ALL ensures AvailableTrucks are saved when Transporter is saved
    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, orphanRemoval = true)
    // Initialize list to prevent NullPointerException if DTO doesn't include it
    @Builder.Default // Required by Lombok builder to use the default initializer
    private List<AvailableTruck> availableTrucks = new ArrayList<>();

    @Version
    private Long version;
}