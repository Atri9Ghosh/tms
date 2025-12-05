package com.atri.tms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "loads",
        indexes = {
                @Index(name = "idx_load_shipper_status", columnList = "shipperId,status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Load {

    @Id
    @GeneratedValue
    private UUID loadId;

    private String shipperId;

    private String loadingCity;
    private String unloadingCity;

    private Instant loadingDate;

    private String productType;

    private double weight;

    @Enumerated(EnumType.STRING)
    private WeightUnit weightUnit;

    private String truckType;

    private int noOfTrucks;

    @Enumerated(EnumType.STRING)
    private LoadStatus status;

    private Instant datePosted;

    // optimistic locking to prevent double booking
    @Version
    private Long version;
}
