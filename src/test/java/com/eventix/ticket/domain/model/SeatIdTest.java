package com.eventix.ticket.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SeatIdTest {

    @Test
    void shouldThrowExceptionForNullValue(){
        assertThrows(IllegalArgumentException.class, ()-> new SeatId(null));
    }

    @Test
    void shouldBeEqualsForSameUUID(){
        UUID rawId = UUID.randomUUID();
        SeatId seatId1 = new SeatId(rawId);
        SeatId seatId2 = new SeatId(rawId);

        assertEquals(seatId1, seatId2);
        assertEquals(seatId1.hashCode(), seatId2.hashCode());
    }
}
