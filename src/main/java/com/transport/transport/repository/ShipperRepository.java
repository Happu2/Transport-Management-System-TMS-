package com.transport.transport.repository;

import com.transport.transport.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ShipperRepository extends JpaRepository<Shipper, UUID> {}
