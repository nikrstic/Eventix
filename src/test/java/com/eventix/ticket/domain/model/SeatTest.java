package com.eventix.ticket.domain.model;

import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.exception.SeatIsNotHeldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeatTest {

    private Seat seat;
    private SeatId seatId;
    private UserId userId;

    @BeforeEach
    void setUp() {
        seatId = SeatId.generate();
        userId = UserId.generate();
        seat = new Seat(seatId);
    }

    @Test
    public void shouldInitializeSeat() {
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        assertEquals(seatId, seat.getId());
        assertNull(seat.getHeldBy());
    }
    @Test
    void shouldHoldAvailableSeat() {
        seat.hold(userId);

        assertEquals(SeatStatus.HELD, seat.getStatus());
        assertEquals(userId, seat.getHeldBy());
    }
    @Test
    void shouldFailHoldingAlreadyHeldSeat() {
        seat.hold(userId);
        UserId secondUser = UserId.generate();

        assertThrows(SeatAlreadyHeldException.class, () -> seat.hold(secondUser));
    }
    @Test
    void shouldConfirmHeldSeat() {
        seat.hold(userId);
        seat.confirm();

        assertEquals(SeatStatus.CONFIRMED, seat.getStatus());
    }

    @Test
    void shouldFailConfirmingAvailableSeat() {
        assertThrows(SeatIsNotHeldException.class, () -> seat.confirm());
    }
}
