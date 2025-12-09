package com.transport.transport.repository;

import com.transport.transport.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.load WHERE b.shipperId = :shipperId")
    List<Booking> findByShipperId(@Param("shipperId") UUID shipperId);
}
