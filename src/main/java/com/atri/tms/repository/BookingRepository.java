package com.atri.tms.repository;

import com.atri.tms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("select coalesce(sum(b.allocatedTrucks), 0) from Booking b where b.load = :load")
    int sumAllocatedTrucksByLoad(Load load);

    @Query("select coalesce(sum(b.allocatedTrucks), 0) from Booking b where b.load = :load and b.status = :status")
    int sumAllocatedTrucksByLoadAndStatus(Load load, BookingStatus status);
}
