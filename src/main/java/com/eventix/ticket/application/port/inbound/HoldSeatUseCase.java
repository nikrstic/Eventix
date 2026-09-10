package com.eventix.ticket.application.port.inbound;

public interface HoldSeatUseCase {
    void holdSeat(HoldSeatsCommand command);
}
