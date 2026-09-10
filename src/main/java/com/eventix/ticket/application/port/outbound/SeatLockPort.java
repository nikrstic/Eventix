package com.eventix.ticket.application.port.outbound;

import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.UserId;

import java.time.Duration;

public interface SeatLockPort {
    boolean acquireLock(SeatId seatId, UserId userId, Duration duration);
    void releaseLock(SeatId seatId, UserId userId);
}
