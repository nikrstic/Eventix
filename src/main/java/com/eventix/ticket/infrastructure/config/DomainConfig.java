package com.eventix.ticket.infrastructure.config;

import com.eventix.ticket.domain.service.ReservationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ReservationDomainService reservationDomainService() {
        return new ReservationDomainService();
    }
}
