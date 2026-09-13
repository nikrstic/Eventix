package com.eventix.ticket.infrastructure.adapter.outbound.jpa;

import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.SeatStatus;
import com.eventix.ticket.domain.model.UserId;

import java.util.UUID;

public class SeatMapper {

    public static Seat toDomain(SeatJpaEntity entity) {
        if (entity == null) return null;

        Seat seat = new Seat(new SeatId(entity.getId()));

        if(entity.getStatus() == SeatStatus.HELD && entity.getHeldBy() != null) {
            seat.hold(new UserId(entity.getHeldBy()));
        }
        else if(entity.getStatus() == SeatStatus.CONFIRMED) {
            if(entity.getHeldBy() != null) {
                seat.hold(new UserId(entity.getHeldBy()));
            }
            seat.confirm();
        }
        return seat;
    }

    public static SeatJpaEntity toJpaEntity(Seat seat) {
        if(seat == null) return null;

        UUID heldByUuid = seat.getHeldBy() != null ?  seat.getHeldBy().value() : null;
        return new SeatJpaEntity(seat.getId().value(), seat.getStatus(), heldByUuid);
    }
}
