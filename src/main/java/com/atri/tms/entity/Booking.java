package com.atri.tms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(
                        name = "idx_booking_load_transporter_status",
                        columnList = "load_id,transporter_id,status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue
    private UUID bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id")
    private Load load;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_id", unique = true)
    private Bid bid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;

    private int allocatedTrucks;

    private double finalRate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Instant bookedAt;
}
