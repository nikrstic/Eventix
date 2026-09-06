package com.eventix.ticket.domain.exception;

public class SeatAlreadyHeldException extends DomainException {
    public SeatAlreadyHeldException(String message) {
        super(message);
    }
}
