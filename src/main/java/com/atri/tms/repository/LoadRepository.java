package com.atri.tms.repository;

import com.atri.tms.entity.Load;
import com.atri.tms.entity.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface LoadRepository extends JpaRepository<Load, UUID> {

    // Optimistic locking for double-booking prevention
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select l from Load l where l.loadId = :id")
    Optional<Load> findByIdWithLock(@Param("id") UUID id);

    // Required methods for LoadService.listLoads()
    Page<Load> findByShipperId(String shipperId, Pageable pageable);

    Page<Load> findByShipperIdAndStatus(String shipperId, LoadStatus status, Pageable pageable);
}
