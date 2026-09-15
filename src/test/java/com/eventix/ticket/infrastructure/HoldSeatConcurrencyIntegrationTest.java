package com.eventix.ticket.infrastructure;

import com.eventix.ticket.application.port.inbound.HoldSeatUseCase;
import com.eventix.ticket.application.port.inbound.HoldSeatsCommand;
import com.eventix.ticket.application.port.outbound.SeatRepositoryPort;
import com.eventix.ticket.domain.exception.SeatAlreadyHeldException;
import com.eventix.ticket.domain.model.Seat;
import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.SeatStatus;
import com.eventix.ticket.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class HoldSeatConcurrencyIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.threads.virtual.enabled", () -> "true");
    }

    private final HoldSeatUseCase holdSeatUseCase;

    private final SeatRepositoryPort seatRepositoryPort;

    public HoldSeatConcurrencyIntegrationTest(
            HoldSeatUseCase holdSeatUseCase,
            SeatRepositoryPort seatRepositoryPort) {
        this.holdSeatUseCase = holdSeatUseCase;
        this.seatRepositoryPort = seatRepositoryPort;
    }

    private SeatId targetSeatId;


    @BeforeEach
    void setUp() {
        targetSeatId = SeatId.generate();
        Seat seat = new Seat(targetSeatId);
        seatRepositoryPort.save(seat);
    }

    @Test
    void testConcurrentHoldRequestOnSameSeat() {
        int numOfThreads = 10000;
        CountDownLatch latch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < numOfThreads; i++) {
                executorService.submit(() -> {
                    UserId concurrentUser = UserId.generate();
                    HoldSeatsCommand command = new HoldSeatsCommand(concurrentUser, List.of(targetSeatId));

                    try {
                        latch.await();
                        holdSeatUseCase.holdSeat(command);
                        successCount.incrementAndGet();
                    } catch (SeatAlreadyHeldException ex) {
                        failureCount.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return null;
                });
            }
            latch.countDown();
        }

        assertEquals(1, successCount.get(), "Exactly 1 concurrent request must succeed");
        assertEquals(numOfThreads - 1, failureCount.get(), "Exactly n-1 concurrent requests must fail with lock exception");

        Seat persistedSeat = seatRepositoryPort.findById(targetSeatId).orElseThrow();
        assertEquals(SeatStatus.HELD, persistedSeat.getStatus());
        assertNotNull(persistedSeat.getHeldBy());
    }

    }
