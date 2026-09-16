package com.eventix.ticket.domain.service;


import com.eventix.ticket.domain.exception.InvalidSeatBookingException;
import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.exception.SeatIsNotHeldException;
import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.model.SeatStatus;
import com.eventix.ticket.domain.model.UserId;

import java.util.List;

public class ReservationDomainService {

    private static final int MAX_SEATS_PER_USER = 4;
    public void holdMultipleSeats(UserId userId, List<Seat> seatsHold) throws SeatAlreadyHeldException, InvalidSeatBookingException {
        if(seatsHold.size() > MAX_SEATS_PER_USER){
            throw new InvalidSeatBookingException("Seats can't be more than " + MAX_SEATS_PER_USER);
        }
        for(Seat seat : seatsHold){
            if (seat.getStatus() == SeatStatus.HELD) {
                throw new SeatAlreadyHeldException("Seat " + seat.getId() + " is already held.");
            }
            seat.hold(userId);
        }
    }
    public void confirmMultipleSeats(List<Seat> seatsConfirm) throws SeatIsNotHeldException, InvalidSeatBookingException {
        if(seatsConfirm == null || seatsConfirm.isEmpty()){
            throw new InvalidSeatBookingException("Seats can't be empty");
        }
        for(Seat seat : seatsConfirm){
            seat.confirm();
        }
    }
}
