package com.atri.tms.repository;

import com.atri.tms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByLoadAndStatus(Load load, BidStatus status);

    List<Bid> findByLoad_LoadId(UUID loadId);

    List<Bid> findByTransporter_TransporterId(UUID transporterId);

    List<Bid> findByLoad_LoadIdAndTransporter_TransporterIdAndStatus(
            UUID loadId, UUID transporterId, BidStatus status);
}
