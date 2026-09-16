package com.eventix.ticket.infrastructure.adapter.in.web;


import com.eventix.ticket.application.port.inbound.HoldSeatUseCase;
import com.eventix.ticket.application.port.inbound.HoldSeatsCommand;
import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.UserId;
import com.eventix.ticket.infrastructure.adapter.in.web.dto.HoldSeatRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
public class HoldSeatsController {

    private final HoldSeatUseCase holdSeatUseCase;

    public HoldSeatsController(HoldSeatUseCase holdSeatUseCase) {
        this.holdSeatUseCase = holdSeatUseCase;
    }

    @PostMapping("/hold")
    public ResponseEntity<Void> holdSeat(@Valid @RequestBody HoldSeatRequest request){
        UserId userId = new UserId(request.userId());
        List<SeatId> seatIds = request.seatIds().stream()
                .map(SeatId::new)
                .toList();

        HoldSeatsCommand command = new HoldSeatsCommand(userId, seatIds);
        holdSeatUseCase.holdSeat(command);
        return ResponseEntity.accepted().build();
    }

}
