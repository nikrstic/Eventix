package com.eventix.ticket.infrastructure.adapter.outbound.jpa;

import com.eventix.ticket.application.port.outbound.SeatRepositoryPort;
import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.model.SeatId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SeatRepositoryAdapter implements SeatRepositoryPort {

    private final SeatSpringDataJpaRepository SpringDataRepository;

    public SeatRepositoryAdapter(SeatSpringDataJpaRepository SpringDataRepository) {
        this.SpringDataRepository = SpringDataRepository;
    }

    @Override
    public Optional<Seat> findById(SeatId seatId) {
        return SpringDataRepository.findById(seatId.value()).map(SeatMapper::toDomain);
    }

    @Override
    public List<Seat> findAllByIds(List<SeatId> seatIds) {
        List<UUID> uuids = seatIds.stream().map(SeatId::value).toList();
        return SpringDataRepository.findAllById(uuids).stream()
                .map(SeatMapper::toDomain)
                .toList();
    }

    @Override
    public void save(Seat seat) {
        SeatJpaEntity entity = SeatMapper.toJpaEntity(seat);
        SpringDataRepository.save(entity);
    }

    @Override
    public void saveAll(List<Seat> seats) {
        List<SeatJpaEntity> entities = seats.stream().map(SeatMapper::toJpaEntity).toList();
        SpringDataRepository.saveAll(entities);
    }
}
