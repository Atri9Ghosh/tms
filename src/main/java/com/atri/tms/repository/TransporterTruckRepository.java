package com.atri.tms.repository;

import com.atri.tms.entity.Transporter;
import com.atri.tms.entity.TransporterTruck;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TransporterTruckRepository extends JpaRepository<TransporterTruck, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TransporterTruck t where t.transporter = :transporter and t.truckType = :truckType")
    Optional<TransporterTruck> findByTransporterAndTruckTypeForUpdate(
            @Param("transporter") Transporter transporter,
            @Param("truckType") String truckType
    );
}
