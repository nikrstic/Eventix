package com.eventix.ticket.application.service;

import com.eventix.ticket.application.port.inbound.HoldSeatUseCase;
import com.eventix.ticket.application.port.inbound.HoldSeatsCommand;
import com.eventix.ticket.application.port.outbound.SeatLockPort;
import com.eventix.ticket.application.port.outbound.SeatRepositoryPort;
import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.service.ReservationDomainService;

import java.time.Duration;
import java.util.List;

public class HoldSeatApplicationService implements HoldSeatUseCase {

    private final SeatRepositoryPort seatRepositoryPort;
    private final SeatLockPort seatLockPort;
    private final ReservationDomainService  reservationDomainService;

    public HoldSeatApplicationService(SeatRepositoryPort seatRepositoryPort, SeatLockPort seatLockPort, ReservationDomainService reservationDomainService) {
        this.seatRepositoryPort = seatRepositoryPort;
        this.seatLockPort = seatLockPort;
        this.reservationDomainService = reservationDomainService;
    }


    @Override
    public void holdSeat(HoldSeatsCommand command) {
        command.seatIds().forEach(seatId -> {
            boolean locked = seatLockPort.acquireLock(seatId, command.userId(), Duration.ofMinutes(10));
            if (!locked) {
                throw new SeatAlreadyHeldException("Hold seat lock failed");
            }
        });

            List<Seat> seats = seatRepositoryPort.findAllByIds(command.seatIds());

            reservationDomainService.holdMultipleSeats(command.userId(), seats);

            seatRepositoryPort.saveAll(seats);


    }



}
