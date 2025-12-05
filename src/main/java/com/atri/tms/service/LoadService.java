package com.atri.tms.service;

import com.atri.tms.dto.LoadRequest;
import com.atri.tms.dto.LoadResponse;
import com.atri.tms.entity.*;
import com.atri.tms.exception.InvalidStatusTransitionException;
import com.atri.tms.exception.ResourceNotFoundException;
import com.atri.tms.repository.BidRepository;
import com.atri.tms.repository.BookingRepository;
import com.atri.tms.repository.LoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoadService {

    private final LoadRepository loadRepository;
    private final BidRepository bidRepository;
    private final BookingRepository bookingRepository;

    // ----------------------------------------------------------------------
    // CREATE LOAD  →  POST /load
    // ----------------------------------------------------------------------
    public LoadResponse createLoad(LoadRequest req) {

        Load load = Load.builder()
                .shipperId(req.getShipperId())
                .loadingCity(req.getLoadingCity())
                .unloadingCity(req.getUnloadingCity())
                .loadingDate(req.getLoadingDate())
                .productType(req.getProductType())
                .weight(req.getWeight())
                .weightUnit(req.getWeightUnit())
                .truckType(req.getTruckType())
                .noOfTrucks(req.getNoOfTrucks())
                .status(LoadStatus.POSTED)
                .datePosted(Instant.now())
                .build();

        Load saved = loadRepository.save(load);
        return toResponse(saved);
    }

    // ----------------------------------------------------------------------
    // GET LOAD BY ID (Load + active bids) → GET /load/{loadId}
    // ----------------------------------------------------------------------
    public LoadResponse getLoad(UUID loadId) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        return toResponse(load);
    }

    // ----------------------------------------------------------------------
    // LIST LOADS WITH FILTERING → GET /load?shipperId=&status=&page=&size=
    // ----------------------------------------------------------------------
    public Page<LoadResponse> listLoads(String shipperId, LoadStatus status, int page, int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<Load> pageData;

        if (shipperId != null && status != null) {
            pageData = loadRepository.findByShipperIdAndStatus(shipperId, status, pageable);
        } else if (shipperId != null) {
            pageData = loadRepository.findByShipperId(shipperId, pageable);
        } else {
            pageData = loadRepository.findAll(pageable);
        }

        return pageData.map(this::toResponse);
    }

    // ----------------------------------------------------------------------
    // CANCEL LOAD → PATCH /load/{loadId}/cancel
    // ----------------------------------------------------------------------
    public void cancelLoad(UUID loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        if (load.getStatus() == LoadStatus.BOOKED) {
            throw new InvalidStatusTransitionException("Cannot cancel a booked load");
        }

        load.setStatus(LoadStatus.CANCELLED);
        loadRepository.save(load);
    }

    // ----------------------------------------------------------------------
    // BEST BID CALCULATION → GET /load/{loadId}/best-bids
    //
    // Score = (1/rate)*0.7 + (rating/5)*0.3
    // Higher score = better bid
    // ----------------------------------------------------------------------
    public List<Bid> getBestBids(UUID loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        List<Bid> bids = bidRepository.findByLoadAndStatus(load, BidStatus.PENDING);

        return bids.stream()
                .sorted((b1, b2) -> Double.compare(score(b2), score(b1)))
                .collect(Collectors.toList());
    }

    // Score formula
    private double score(Bid b) {
        double rateScore = (1.0 / b.getProposedRate()) * 0.7;
        double ratingScore = (b.getTransporter().getRating() / 5.0) * 0.3;
        return rateScore + ratingScore;
    }

    // ----------------------------------------------------------------------
    // Utility → Entity to Response DTO
    // ----------------------------------------------------------------------
    private LoadResponse toResponse(Load load) {
        return LoadResponse.builder()
                .loadId(load.getLoadId())
                .shipperId(load.getShipperId())
                .loadingCity(load.getLoadingCity())
                .unloadingCity(load.getUnloadingCity())
                .loadingDate(load.getLoadingDate())
                .productType(load.getProductType())
                .weight(load.getWeight())
                .weightUnit(load.getWeightUnit())
                .truckType(load.getTruckType())
                .noOfTrucks(load.getNoOfTrucks())
                .status(load.getStatus())
                .datePosted(load.getDatePosted())
                .build();
    }
}
