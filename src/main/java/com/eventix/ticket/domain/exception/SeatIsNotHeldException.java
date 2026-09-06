package com.eventix.ticket.domain.exception;

public class SeatIsNotHeldException extends DomainException {
    public SeatIsNotHeldException(String message) {
        super(message);
    }
}
