package com.eventix.ticket.domain.exception;

public class InvalidSeatBookingException extends DomainException {
    public InvalidSeatBookingException(String message) {
        super(message);
    }
}
