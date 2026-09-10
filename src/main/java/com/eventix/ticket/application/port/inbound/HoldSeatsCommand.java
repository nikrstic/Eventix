package com.eventix.ticket.application.port.inbound;

import com.eventix.ticket.domain.exception.InvalidSeatBookingException;
import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.UserId;

import java.util.List;
import java.util.Objects;

public record HoldSeatsCommand(UserId userId, List<SeatId> seatIds) {
    public HoldSeatsCommand{
        Objects.requireNonNull(userId, "userId cannot be null");
        if(seatIds == null || seatIds.isEmpty()){
            throw new InvalidSeatBookingException("seatIds cannot be null or empty");
        }
    }

}
