package com.eventix.ticket.domain.model;

import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.exception.SeatIsNotHeldException;

public class Seat {
    private final SeatId id;

    private SeatStatus status;
    private UserId heldBy;

    public Seat(SeatId id) {
        this.id = id;
        this.status = SeatStatus.AVAILABLE;
    }
    public void hold(UserId userId) {
        if(this.status != SeatStatus.AVAILABLE ) {
            throw new SeatAlreadyHeldException("Seat is already holding");
        }
        this.status = SeatStatus.HELD;
        this.heldBy = userId;
    }
    public void confirm() {
        if(this.status != SeatStatus.HELD) {
            throw new SeatIsNotHeldException("Seat must be held before confirmation");
        }
        this.status = SeatStatus.CONFIRMED;
    }

    public SeatId getId() {
        return id;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public UserId getHeldBy() {
        return heldBy;
    }
}
