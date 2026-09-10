package com.eventix.ticket.application.port.outbound;

import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.model.SeatId;

import java.util.List;
import java.util.Optional;

public interface SeatRepositoryPort {
    Optional<Seat> findById(SeatId seatId);
    List<Seat> findAllByIds(List<SeatId> seatIds);
    void save(Seat seat);
    void saveAll(List<Seat> seats);
}
