package com.eventix.ticket.infrastructure.adapter.in.web.dto;



import java.util.List;
import java.util.UUID;

public record HoldSeatRequest (UUID userId, List<UUID> seatIds) {
}
