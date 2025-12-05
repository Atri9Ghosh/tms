package com.atri.tms.service;

import com.atri.tms.dto.BookingRequest;
import com.atri.tms.entity.*;
import com.atri.tms.exception.InsufficientCapacityException;
import com.atri.tms.exception.InvalidStatusTransitionException;
import com.atri.tms.exception.LoadAlreadyBookedException;
import com.atri.tms.exception.ResourceNotFoundException;
import com.atri.tms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterTruckRepository transporterTruckRepository;

    // POST /booking
    @Transactional
    public Booking createBooking(BookingRequest req) {

        Bid bid = bidRepository.findById(req.getBidId())
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        if (bid.getStatus() != BidStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only PENDING bids can be accepted");
        }

        // lock the load for optimistic concurrency control
        Load load = loadRepository.findByIdWithLock(bid.getLoad().getLoadId())
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        if (load.getStatus() == LoadStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot book a CANCELLED load");
        }

        Transporter transporter = bid.getTransporter();

        // capacity: check trucks again
        TransporterTruck truck = transporterTruckRepository
                .findByTransporterAndTruckTypeForUpdate(transporter, load.getTruckType())
                .orElseThrow(() -> new InsufficientCapacityException("No trucks of required type"));

        if (bid.getTrucksOffered() > truck.getCount()) {
            throw new InsufficientCapacityException("Insufficient trucks for booking");
        }

        // multi-truck allocation
        int allocatedSoFar = bookingRepository.sumAllocatedTrucksByLoad(load);
        int remaining = load.getNoOfTrucks() - allocatedSoFar;

        if (bid.getTrucksOffered() > remaining) {
            throw new LoadAlreadyBookedException("Not enough remaining trucks for this load");
        }

        // deduct trucks from transporter
        truck.setCount(truck.getCount() - bid.getTrucksOffered());
        transporterTruckRepository.save(truck);

        // create booking
        Booking booking = Booking.builder()
                .load(load)
                .bid(bid)
                .transporter(transporter)
                .allocatedTrucks(bid.getTrucksOffered())
                .finalRate(bid.getProposedRate())
                .status(BookingStatus.CONFIRMED)
                .bookedAt(Instant.now())
                .build();

        bookingRepository.save(booking);

        // mark bid as ACCEPTED
        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        // update load status
        allocatedSoFar += bid.getTrucksOffered();
        if (allocatedSoFar == load.getNoOfTrucks()) {
            load.setStatus(LoadStatus.BOOKED);
        } else {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
        }

        try {
            loadRepository.save(load); // may throw OptimisticLockException
        } catch (OptimisticLockException e) {
            throw new LoadAlreadyBookedException("Concurrent booking detected");
        }

        return booking;
    }

    // GET /booking/{id}
    public Booking getBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    // PATCH /booking/{id}/cancel
    @Transactional
    public Booking cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // restore trucks
        Load load = booking.getLoad();
        Transporter transporter = booking.getTransporter();

        TransporterTruck truck = transporterTruckRepository
                .findByTransporterAndTruckTypeForUpdate(transporter, load.getTruckType())
                .orElseThrow(() -> new IllegalStateException("Transporter truck config missing"));

        truck.setCount(truck.getCount() + booking.getAllocatedTrucks());
        transporterTruckRepository.save(truck);

        // update load status based on remaining confirmed bookings
        int stillAllocated = bookingRepository
                .sumAllocatedTrucksByLoadAndStatus(load, BookingStatus.CONFIRMED);

        if (stillAllocated == 0) {
            if (load.getStatus() != LoadStatus.CANCELLED) {
                load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            }
        } else if (stillAllocated < load.getNoOfTrucks()) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
        }

        loadRepository.save(load);

        return booking;
    }
}
