package com.atri.tms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "transporters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transporter {

    @Id
    @GeneratedValue
    private UUID transporterId;

    private String companyName;

    private double rating; // 1–5

    @OneToMany(
            mappedBy = "transporter",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    private Set<TransporterTruck> availableTrucks = new HashSet<>();
}
