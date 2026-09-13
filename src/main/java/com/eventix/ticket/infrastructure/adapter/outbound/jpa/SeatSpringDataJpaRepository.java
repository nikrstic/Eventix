package com.eventix.ticket.infrastructure.adapter.outbound.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatSpringDataJpaRepository extends JpaRepository<SeatJpaEntity, UUID> {
}
