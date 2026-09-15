package com.eventix.ticket.application.service;

import com.eventix.ticket.application.port.inbound.HoldSeatUseCase;
import com.eventix.ticket.application.port.inbound.HoldSeatsCommand;
import com.eventix.ticket.application.port.outbound.SeatLockPort;
import com.eventix.ticket.application.port.outbound.SeatRepositoryPort;
import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.service.ReservationDomainService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class HoldSeatApplicationService implements HoldSeatUseCase {

    private final SeatRepositoryPort seatRepositoryPort;
    private final SeatLockPort seatLockPort;
    private final ReservationDomainService  reservationDomainService;
    private final RedisTemplate<Object, Object> redisTemplate;

    public HoldSeatApplicationService(SeatRepositoryPort seatRepositoryPort, SeatLockPort seatLockPort, ReservationDomainService reservationDomainService, RedisTemplate<Object, Object> redisTemplate) {
        this.seatRepositoryPort = seatRepositoryPort;
        this.seatLockPort = seatLockPort;
        this.reservationDomainService = reservationDomainService;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void holdSeat(HoldSeatsCommand command) {
        command.seatIds().forEach(seatId -> {
            boolean locked = seatLockPort.acquireLock(seatId, command.userId(), Duration.ofMinutes(10));
            if (!locked) {
                throw new SeatAlreadyHeldException("Hold seat lock failed");
            }
        });
        try {
            List<Seat> seats = seatRepositoryPort.findAllByIds(command.seatIds());

            reservationDomainService.holdMultipleSeats(command.userId(), seats);

            seatRepositoryPort.saveAll(seats);
        }finally {
            command.seatIds().forEach(seatId -> seatLockPort.releaseLock(seatId, command.userId()));
        }

    }



}
