package com.eventix.ticket.service;

import com.eventix.ticket.domain.exception.InvalidSeatBookingException;
import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.SeatStatus;
import com.eventix.ticket.domain.model.UserId;
import com.eventix.ticket.domain.service.ReservationDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationDomainTest {
    private ReservationDomainService domainService;
    private UserId userId;

    @BeforeEach
    void setUp(){
        domainService = new ReservationDomainService();
        userId = UserId.generate();
    }

    @Test
    void shouldHoldSeatSuccessfully(){
        List<Seat> seatsHold = List.of(new Seat(SeatId.generate()),new Seat(SeatId.generate()));
        domainService.holdMultipleSeats(userId, seatsHold);
        assertTrue(seatsHold.stream().allMatch(seat -> seat.getStatus() == SeatStatus.HELD));
        assertTrue(seatsHold.stream().allMatch(seat -> seat.getHeldBy() == userId));
    }

    @Test
    void shouldFailWhenExceedingMaxSeatLimit(){
        List<Seat> seatsHold = List.of(new Seat(SeatId.generate()),new Seat(SeatId.generate()), new Seat(SeatId.generate()),new Seat(SeatId.generate()),new Seat(SeatId.generate()));
        assertThrows(InvalidSeatBookingException.class, ()-> domainService.holdMultipleSeats(userId, seatsHold));
    }

    @Test
    void shouldFailBatchHoldIfOneSeatIsAlreadyHeld(){
        Seat seat1 = new Seat(SeatId.generate());
        Seat seat2 = new Seat(SeatId.generate());
        seat2.hold(UserId.generate());

        assertThrows(SeatAlreadyHeldException.class, ()-> domainService.holdMultipleSeats(userId, List.of(seat1, seat2)));
    }
}
