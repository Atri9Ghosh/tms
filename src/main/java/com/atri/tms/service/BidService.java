package com.atri.tms.service;

import com.atri.tms.dto.BidRequest;
import com.atri.tms.entity.*;
import com.atri.tms.exception.InsufficientCapacityException;
import com.atri.tms.exception.InvalidStatusTransitionException;
import com.atri.tms.exception.ResourceNotFoundException;
import com.atri.tms.repository.BidRepository;
import com.atri.tms.repository.LoadRepository;
import com.atri.tms.repository.TransporterRepository;
import com.atri.tms.repository.TransporterTruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;
    private final TransporterTruckRepository transporterTruckRepository;

    // POST /bid
    @Transactional
    public Bid createBid(BidRequest req) {

        Load load = loadRepository.findById(req.getLoadId())
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        if (load.getStatus() == LoadStatus.BOOKED || load.getStatus() == LoadStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot bid on BOOKED or CANCELLED load");
        }

        Transporter transporter = transporterRepository.findById(req.getTransporterId())
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

        // capacity validation
        TransporterTruck truck = transporterTruckRepository
                .findByTransporterAndTruckTypeForUpdate(transporter, load.getTruckType())
                .orElseThrow(() -> new InsufficientCapacityException("No trucks of required type"));

        if (req.getTrucksOffered() > truck.getCount()) {
            throw new InsufficientCapacityException("Offered trucks exceed available trucks");
        }

        // status transition: POSTED -> OPEN_FOR_BIDS on first bid
        if (load.getStatus() == LoadStatus.POSTED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }

        Bid bid = Bid.builder()
                .load(load)
                .transporter(transporter)
                .proposedRate(req.getProposedRate())
                .trucksOffered(req.getTrucksOffered())
                .status(BidStatus.PENDING)
                .submittedAt(Instant.now())
                .build();

        return bidRepository.save(bid);
    }

    // GET /bid?loadId=&transporterId=&status=
    public List<Bid> filterBids(UUID loadId, UUID transporterId, BidStatus status) {

        if (loadId != null && transporterId != null && status != null) {
            return bidRepository.findByLoad_LoadIdAndTransporter_TransporterIdAndStatus(
                    loadId, transporterId, status);
        }

        if (loadId != null && transporterId != null) {
            return bidRepository.findByLoad_LoadIdAndTransporter_TransporterIdAndStatus(
                    loadId, transporterId, status != null ? status : BidStatus.PENDING);
        }

        if (loadId != null) {
            return bidRepository.findByLoad_LoadId(loadId);
        }

        if (transporterId != null) {
            return bidRepository.findByTransporter_TransporterId(transporterId);
        }

        return bidRepository.findAll();
    }

    // GET /bid/{bidId}
    public Bid getBid(UUID bidId) {
        return bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
    }

    // PATCH /bid/{bidId}/reject
    @Transactional
    public Bid rejectBid(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only PENDING bids can be rejected");
        }
        bid.setStatus(BidStatus.REJECTED);
        return bidRepository.save(bid);
    }
}
