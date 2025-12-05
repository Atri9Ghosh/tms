package com.atri.tms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "bids",
        indexes = {
                @Index(
                        name = "idx_bid_load_transporter_status",
                        columnList = "load_id,transporter_id,status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

    @Id
    @GeneratedValue
    private UUID bidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id")
    private Load load;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;

    private double proposedRate;

    private int trucksOffered;

    @Enumerated(EnumType.STRING)
    private BidStatus status;

    private Instant submittedAt;
}
