// LoadRepository.java
package com.transport.transport.repository;

import com.transport.transport.entity.Load;

import com.transport.transport.entity.enums.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoadRepository extends JpaRepository<Load, UUID> {

    // ⭐ CHANGE: Changed String to UUID for type consistency
    Page<Load> findByShipperIdAndStatus(UUID shipperId, LoadStatus status, Pageable pageable);

    // ⭐ CHANGE: Changed String to UUID for type consistency
    Page<Load> findByShipperId(UUID shipperId, Pageable pageable);

    Page<Load> findByStatus(LoadStatus status, Pageable pageable);
}