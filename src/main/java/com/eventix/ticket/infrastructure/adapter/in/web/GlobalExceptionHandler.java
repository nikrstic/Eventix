package com.eventix.ticket.infrastructure.adapter.in.web;

import com.eventix.ticket.domain.exception.InvalidSeatBookingException;
import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SeatAlreadyHeldException.class)
    public ProblemDetail handleSeatAlreadyHeld(SeatAlreadyHeldException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "The requested seat is already locked or undergoing reservation."
        );
        problem.setTitle("Seat Unavailable");
        return problem;
    }
    @ExceptionHandler(InvalidSeatBookingException.class)
    public ProblemDetail invalidSeatBookingException(InvalidSeatBookingException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "seatIds cannot be null or empty."
        );
        problem.setTitle("Validation Failed");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid request payload provided."
        );
        problem.setTitle("Validation Failed");
        return problem;
    }
}
