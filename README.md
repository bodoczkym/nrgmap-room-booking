# NRGmap Room Booking API

Meeting room booking REST API built with Spring Boot. Supports creating, listing, and cancelling room reservations with automatic conflict detection to prevent overlapping bookings.

## Requirements

- Java 21
- Maven 3.9+ (or use the included Maven wrapper)

## How to Run

```bash
./mvnw install && ./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/rooms` | List all meeting rooms |
| POST | `/api/bookings` | Create a new booking |
| GET | `/api/bookings` | List all bookings |
| GET | `/api/bookings?roomId={id}` | List bookings for a specific room |
| DELETE | `/api/bookings/{id}` | Cancel a booking (requires email verification) |

### Create Booking — Request Body

```json
{
  "roomId": 1,
  "bookerName": "Mike B",
  "bookerEmail": "mike@example.com",
  "startTime": "2026-03-10T10:00:00",
  "endTime": "2026-03-10T12:00:00"
}
```

### Cancel Booking — Request Body

```json
{
  "bookerEmail": "mike@example.com"
}
```

## Swagger UI

Interactive API documentation available at:

`http://localhost:8080/swagger-ui.html`

## H2 Console

Database console available at:

`http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:roombooking`
- Username: `sa`
- Password: *(empty)*

## Running Tests

```bash
./mvnw test
```

Tests include:
- **BookingConflictIntegrationTest** — All overlap scenarios (exact match, partial overlap, wrapping) and edge cases (adjacent bookings, different rooms)
- **BookingControllerIntegrationTest** — CRUD operations, validation errors, email ownership verification
- **RoomControllerIntegrationTest** — Seeded room data verification

## Pre-seeded Rooms

| Name | Capacity | Floor | Projector | Whiteboard |
|------|----------|-------|-----------|------------|
| Blue Room | 10 | 1 | Yes | Yes |
| Red Room | 6 | 1 | Yes | No |
| Green Room | 4 | 2 | No | Yes |
| Board Room | 20 | 3 | Yes | Yes |
| Phone Booth | 2 | 1 | No | No |

## Design Decisions

- **Conflict Detection:** JPQL overlap query using the interval overlap formula (`start1 < end2 AND end1 > start2`). Prevents any overlapping bookings in the same room.
- **Concurrency Safety:** Pessimistic locking (`SELECT ... FOR UPDATE`) on the room row during booking creation ensures no race conditions when two users book the same room simultaneously.
- **Liquibase:** Schema creation and room seed data managed via Liquibase changelogs instead of `data.sql` or Hibernate auto-DDL.
- **Layered Architecture:** Controller (routing) → Service interface/impl (business logic) → Repository (data access), with separate DTOs, Mappers, and a global exception handler.
- **Email Ownership:** Booking deletion requires the booker's email as a lightweight ownership check without full authentication.
- **Input Validation:** Bean Validation on DTOs with a custom `@ValidTimeRange` class-level annotation ensuring end time is after start time.

## Project Structure

```
src/main/java/com/nrgmap/roombooking/
├── NrgmapRoomBookingApplication.java
├── controller/         REST endpoints
├── service/            Service interfaces
├── service/impl/       Service implementations
├── repository/         JPA repositories
├── model/              JPA entities
├── dto/                Request/response objects
├── dto/validation/     Custom validators
├── exception/          Error handling
└── mapper/             Entity-DTO conversion
```
