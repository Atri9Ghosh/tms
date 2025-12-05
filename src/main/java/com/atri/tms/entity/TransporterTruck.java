package com.atri.tms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "transporter_trucks",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_transporter_trucktype",
                columnNames = {"transporter_id", "truckType"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransporterTruck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String truckType;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;
}
