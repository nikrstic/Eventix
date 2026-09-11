package com.eventix.ticket.application.service;

import com.eventix.ticket.application.port.inbound.HoldSeatsCommand;
import com.eventix.ticket.application.port.outbound.SeatLockPort;
import com.eventix.ticket.application.port.outbound.SeatRepositoryPort;
import com.eventix.ticket.domain.exception.InvalidSeatBookingException;
import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.UserId;
import com.eventix.ticket.domain.service.ReservationDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HoldSeatApplicationServiceTest {

    @Mock
    private SeatRepositoryPort seatRepositoryPort;

    @Mock
    private SeatLockPort seatLockPort;

    @Mock
    private ReservationDomainService reservationDomainService;

    @InjectMocks
    private HoldSeatApplicationService holdSeatApplicationService;

    @Test
    public void shouldThrowExceptionOnSecondHoldAttempt(){
        SeatId seatId = SeatId.generate();
        UserId userId = UserId.generate();
        HoldSeatsCommand holdSeatsCommand = new HoldSeatsCommand(userId, List.of(seatId));
        HoldSeatsCommand holdSeatsCommand1 = new HoldSeatsCommand(userId, List.of(seatId));

        when(seatLockPort.acquireLock(seatId,userId, Duration.ofMinutes(10)))
                .thenReturn(true)
                .thenReturn(false);
        holdSeatApplicationService.holdSeat(holdSeatsCommand);

        assertThrows(SeatAlreadyHeldException.class,()-> holdSeatApplicationService.holdSeat(holdSeatsCommand1));
    }
    @Test
    public void shouldThrowExceptionWhenPartialLockFails(){
        SeatId seat1 = SeatId.generate();
        SeatId seat2 = SeatId.generate();
        UserId userId = UserId.generate();
        HoldSeatsCommand command = new HoldSeatsCommand(userId, List.of(seat1, seat2));

        when(seatLockPort.acquireLock(seat1,userId, Duration.ofMinutes(10))).thenReturn(true);
        when(seatLockPort.acquireLock(seat2,userId, Duration.ofMinutes(10))).thenReturn(false);

        assertThrows(SeatAlreadyHeldException.class,()-> holdSeatApplicationService.holdSeat(command));

        verifyNoInteractions(seatRepositoryPort,reservationDomainService);
    }

    @Test
    public void shouldThrowInvalidSeatBookingExceptionWhenExceedingLimit(){
        List<SeatId> seatIds = List.of(
                SeatId.generate(), SeatId.generate(), SeatId.generate(),
                SeatId.generate(), SeatId.generate()
        );

        UserId userId = UserId.generate();
        HoldSeatsCommand command = new HoldSeatsCommand(userId, seatIds);
        when(seatLockPort.acquireLock(any(SeatId.class), eq(userId), any(Duration.class)))
                .thenReturn(true);

        List<Seat> mockSeat = seatIds.stream().map(Seat::new).toList();
        when(seatRepositoryPort.findAllByIds(seatIds)).thenReturn(mockSeat);
        doThrow(InvalidSeatBookingException.class)
                .when(reservationDomainService)
                .holdMultipleSeats(userId, mockSeat);

        assertThrows(InvalidSeatBookingException.class, ()-> holdSeatApplicationService.holdSeat(command));

    }
    @Test
    public void shouldSuccessfullyHoldSeat(){
        UserId userId = UserId.generate();
        List<SeatId> seatIds = List.of(
                SeatId.generate(), SeatId.generate(), SeatId.generate());

        HoldSeatsCommand command = new HoldSeatsCommand(userId, seatIds);
        when(seatLockPort.acquireLock(any(SeatId.class), eq(userId), any(Duration.class)))
                .thenReturn(true);
        List<Seat> mockSeat = seatIds.stream().map(Seat::new).toList();
        when(seatRepositoryPort.findAllByIds(seatIds)).thenReturn(mockSeat);

        holdSeatApplicationService.holdSeat(command);

        verify(reservationDomainService, times(1)).holdMultipleSeats(userId,mockSeat);

        verify(seatRepositoryPort, times(1)).saveAll(mockSeat);
    }

}
