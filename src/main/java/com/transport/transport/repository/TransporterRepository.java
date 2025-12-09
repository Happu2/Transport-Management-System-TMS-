// TransporterRepository.java
package com.transport.transport.repository;


import com.transport.transport.entity.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransporterRepository extends JpaRepository<Transporter, UUID> {
}
