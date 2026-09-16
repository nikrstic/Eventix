# Eventix - High-Concurrency Ticket Reservation Microservice

Eventix is a high-throughput event ticket reservation engine built with **Java 21**, **Spring Boot 3**, **PostgreSQL**, and **Redis**. It uses **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design (DDD)** to handle high-concurrency flash sales while guaranteeing zero race conditions, overselling, or thread starvation.

---

## Architecture & Tech Stack

* **Java 21 (Virtual Threads / Project Loom):** Lightweight threads managed by the JVM to handle concurrent HTTP requests and non-blocking I/O efficiently under heavy load.
* **Hexagonal Architecture:** Domain logic is entirely isolated from frameworks and databases, communicating via inbound and outbound ports.
* **Redis Distributed Locking:** Fast-fail lock mechanism protecting database resources from race conditions during simultaneous reservation attempts.
* **PostgreSQL & HikariCP:** Persistent transactional storage for seat status and hold expirations.
* **Testcontainers:** Integration-tested against real Redis and PostgreSQL Docker instances.

---

## Directory Structure

```text
com.eventix.ticket
├── application
│   ├── port
│   │   ├── inbound         <-- Use case interfaces & Commands
│   │   └── outbound        <-- Database & Lock abstractions
│   └── service             <-- Orchestration & Application logic
├── domain
│   ├── exception           <-- SeatAlreadyHeldException, etc.
│   ├── model               <-- Seat, SeatStatus, UserId, SeatId
│   └── service             <-- Business validation rules
└── infrastructure
    ├── adapter
    │   ├── in.web          <-- REST Controllers & GlobalExceptionHandler
    │   └── outbound
    │       ├── jpa         <-- PostgreSQL Repositories & Entities
    │       └── redis       <-- Redis Distributed Lock Implementation
    └── config              <-- Spring Boot & Thread configurations

```

---

## Prerequisites

* **Java 21** or higher
* **Maven 3.9+**
* **Docker Desktop** (for local databases and running integration tests)

---

## Getting Started

### 1. Start Infrastructure Services

Spin up local PostgreSQL and Redis containers using Docker Compose:

```bash
docker-compose up -d

```

* **PostgreSQL:** `localhost:5432` (DB: `eventix`, User: `postgres`, Password: `postgrespassword`)
* **Redis:** `localhost:6379`

### 2. Run the Application

Execute the following command to start the Spring Boot application:

```bash
mvn spring-boot:run

```

The server will start on `http://localhost:8080`.

---

## REST API Endpoints

### 1. Hold Seats

* **Endpoint:** `POST /api/v1/seats/hold`
* **Headers:** `Content-Type: application/json`

#### Request Body

```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "seatIds": [
    "987e6543-e21b-12d3-a456-426614174000"
  ]
}

```

#### Responses

* **`202 Accepted`**: Seat hold successfully placed.
* **`409 Conflict`**: Seat is already held or currently locked by another request.
* **`400 Bad Request`**: Request validation failed (e.g., missing user ID or empty seat list).

---

## Concurrency Benchmarks & Testing

Run all unit and integration tests (uses Testcontainers to spin up real Redis & Postgres instances):

```bash
mvn test

```

### Key Test Suite

* `HoldSeatConcurrencyIntegrationTest`: Simulates **10,000 concurrent Virtual Threads** contending for a single seat using a `CountDownLatch`. Verifies that exactly 1 thread succeeds while 9,999 fail gracefully without database corruption or thread exhaustion.